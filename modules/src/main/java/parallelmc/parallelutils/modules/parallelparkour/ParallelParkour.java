package parallelmc.parallelutils.modules.parallelparkour;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelClassLoader;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.parallelparkour.commands.ParallelCreateCourse;
import parallelmc.parallelutils.modules.parallelparkour.commands.ParallelDeleteCourse;
import parallelmc.parallelutils.modules.parallelparkour.commands.ParallelEndRun;
import parallelmc.parallelutils.modules.parallelparkour.commands.ParallelLeaderboard;
import parallelmc.parallelutils.modules.parallelparkour.events.EndParkourEvents;
import parallelmc.parallelutils.modules.parallelparkour.events.OnBlockPlace;
import parallelmc.parallelutils.modules.parallelparkour.events.OnPlayerInteract;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ParallelParkour extends ParallelModule {
    private ParallelUtils puPlugin;

    private final HashMap<String, ParkourLayout> parkourLayouts = new HashMap<>();
    private final HashMap<UUID, ParkourPlayer> playersInParkour = new HashMap<>();
    private final HashMap<UUID, ParkourLayout> creatingParkour = new HashMap<>();
    private final HashMap<UUID, List<ParkourTime>> leaderboardCache = new HashMap<>();

    private static Path jsonPath;

    public ParallelParkour(ParallelClassLoader classLoader, List<String> dependents) { super(classLoader, dependents); }

    private static final SimpleDateFormat timerFormat = new SimpleDateFormat("mm:ss:SS");

    private static ParallelParkour Instance;

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

        if (plugin == null) {
            ParallelUtils.log(Level.SEVERE, "Unable to enable ParallelParkour. Plugin " + Constants.PLUGIN_NAME
                    + " does not exist!");
            return;
        }

        this.puPlugin = (ParallelUtils) plugin;

        if (!puPlugin.registerModule(this)) {
            ParallelUtils.log(Level.SEVERE, "Unable to register module ParallelParkour! " +
                    "Module may already be registered. Quitting...");
            return;
        }

        try (Connection conn = puPlugin.getDbConn()) {
            if (conn == null) throw new SQLException("Unable to establish connection!");
            Statement statement = conn.createStatement();
            statement.setQueryTimeout(15);
            statement.execute("""
                    create table if not exists Leaderboard
                    (
                        Id          int          not null auto_increment,
                        UUID        varchar(36)  not null,
                        Course      varchar(256) not null,
                        Time        bigint       not null,
                        constraint Leaderboard_Id_uindex
                            unique (Id),
                        PRIMARY KEY (Id)
                    );""");
            conn.commit();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        manager.registerEvents(new OnPlayerInteract(), puPlugin);
        manager.registerEvents(new OnBlockPlace(), puPlugin);
        manager.registerEvents(new EndParkourEvents(), puPlugin);

        puPlugin.getCommand("createcourse").setExecutor(new ParallelCreateCourse());
        puPlugin.getCommand("deletecourse").setExecutor(new ParallelDeleteCourse());
        puPlugin.getCommand("endrun").setExecutor(new ParallelEndRun());
        puPlugin.getCommand("leaderboard").setExecutor(new ParallelLeaderboard());

        jsonPath = Path.of(puPlugin.getDataFolder().getAbsolutePath() + "/parkour.json");

        loadParkourFromFile();
        cacheLeaderboard();

        Instance = this;
    }

    @Override
    public void onDisable() {
        uploadLeaderboardCache();
        saveParkourToFile();
    }

    @Override
    public void onUnload() {

    }

    @Override
    public @NotNull String getName() {
        return "ParallelParkour";
    }

    private void cacheLeaderboard() {
        try (Connection dbConn = puPlugin.getDbConn()) {
            if (dbConn == null) throw new SQLException("Unable to establish connection!");
            Statement statement = dbConn.createStatement();
            statement.setQueryTimeout(60);
            ResultSet result = statement.executeQuery("SELECT * FROM Leaderboard");
            while (result.next()) {
                UUID uuid = UUID.fromString(result.getString("UUID"));
                ParkourTime time = new ParkourTime(uuid, result.getString("Course"), result.getLong("Time"));
                if (leaderboardCache.containsKey(uuid)) {
                    leaderboardCache.get(uuid).add(time);
                }
                else {
                    leaderboardCache.put(uuid, List.of(time));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void uploadLeaderboardCache() {
        try (Connection dbConn = puPlugin.getDbConn()) {
            if (dbConn == null) throw new SQLException("Unable to establish connection!");
            PreparedStatement statement = dbConn.prepareStatement("""
                    IF EXISTS (SELECT 1 FROM Leaderboard WHERE UUID = ? AND Course = ?)
                        BEGIN
                            UPDATE Leaderboard SET Time = ? WHERE UUID = ? AND Course = ?
                        END
                    ELSE
                        BEGIN
                            INSERT INTO Leaderboard (UUID, Course, Time) VALUES (?, ?, ?)
                        END""");
            statement.setQueryTimeout(60);
            leaderboardCache.forEach((u, v) -> {
                v.forEach((t) -> {
                    try {
                        String p = t.player().toString();
                        String c = t.course();
                        long l = t.time();
                        statement.setString(1, p);
                        statement.setString(2, c);
                        statement.setLong(3, l);
                        statement.setString(4, p);
                        statement.setString(5, c);
                        statement.setString(6, p);
                        statement.setString(7, c);
                        statement.setLong(8, l);
                        statement.addBatch();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            });
            statement.executeBatch();
            dbConn.commit();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ParkourTime> getTopTimesFor(String course, int amount) {
        if (amount < 1) {
            ParallelUtils.log(Level.SEVERE, "Illegal amount passed into getTopTimesFor: " + amount);
            return new ArrayList<>();
        }
        // thanks chatgpt
        // jk fuck you chatgpt
        List<ParkourTime> filter = leaderboardCache.values().stream()
                .flatMap(List::stream)
                .filter(x -> x.course().equals(course))
                .sorted(Comparator.comparingLong(ParkourTime::time))
                .toList();

        if (filter.size() == 0)
            return new ArrayList<>();

        return filter.subList(0, Math.min(amount, leaderboardCache.size()));
    }

    public long getBestTimeFor(Player player, ParkourLayout layout) {
        List<ParkourTime> times = leaderboardCache.get(player.getUniqueId());
        if (times == null)
            return 0L;
        var time = times.stream()
                .filter(x -> x.course().equals(layout.name()))
                .min(Comparator.comparingLong(ParkourTime::time));
        return time.map(ParkourTime::time).orElse(0L);
    }

    public void saveBestTimeFor(Player player, ParkourPlayer pp) {
        UUID uuid = player.getUniqueId();
        List<ParkourTime> times = leaderboardCache.computeIfAbsent(uuid, k -> new ArrayList<>());
        // since ParkourTime is a record, remove all existing times for the map and re-add the best one
        // this function is only run if the player got a new best time, so it should only replace the one existing time
        times = times.stream()
                .filter(x -> !x.course().equals(pp.getLayout().name()))
                .collect(Collectors.toList());
        times.add(new ParkourTime(player.getUniqueId(), pp.getLayout().name(), pp.getFinishTime()));
        leaderboardCache.put(uuid, times);
        List<ParkourTime> leaderboard = getTopTimesFor(pp.getLayout().name(), 1);
        if (leaderboard.size() > 0) {
            ParkourTime best = leaderboard.get(0);
            if (best.time() == pp.getFinishTime()) {
                Component msg = MiniMessage.miniMessage().deserialize(String.format("<yellow>%s<gold> just set a new speedrun world record on <yellow>%s<gold> with a time of <green>%s!",
                        player.getName(), pp.getLayout().name(), getTimeString(pp.getFinishTime())));
                puPlugin.getServer().getOnlinePlayers().forEach(x -> {
                    ParallelChat.sendParallelMessageTo(x, msg);
                });
            }
        }
    }

    public @Nullable ParkourLayout getParkourCreation(Player player) {
        return creatingParkour.get(player.getUniqueId());
    }

    public void startCreatingParkour(Player player, String name, boolean allowEffects) {
        creatingParkour.put(player.getUniqueId(), new ParkourLayout(name, new ArrayList<>(), allowEffects));
    }

    public void saveParkourCreation(Player player) {
        UUID uuid = player.getUniqueId();
        ParkourLayout layout = creatingParkour.get(uuid);
        if (layout == null) {
            ParallelUtils.log(Level.SEVERE, "Tried to save a parkour layout for a player that is not building one!");
            return;
        }
        parkourLayouts.put(layout.name(), layout);
        creatingParkour.remove(uuid);
        ParallelUtils.log(Level.WARNING, "Created new parkour layout: " + layout.name());
    }

    public @Nullable ParkourPlayer getParkourPlayer(Player player) {
        return playersInParkour.get(player.getUniqueId());
    }

    public @Nullable ParkourLayout getParkourStartingAt(Location location) {
        for (ParkourLayout l : parkourLayouts.values()) {
            if (l.positions().get(0).equals(location))
                return l;
        }
        return null;
    }

    public void deleteParkour(String name) {
        parkourLayouts.remove(name);
    }

    public void startParkourFor(Player player, ParkourLayout layout) {
        playersInParkour.put(player.getUniqueId(), new ParkourPlayer(player, layout));
    }

    public void endParkourFor(Player player) {
        playersInParkour.remove(player.getUniqueId());
    }

    public void cancelParkourRunFor(Player player) {
        UUID uuid = player.getUniqueId();
        ParkourPlayer pp = playersInParkour.get(uuid);
        if (pp == null) {
            ParallelChat.sendParallelMessageTo(player, "You are not in a parkour course right now!");
            return;
        }
        pp.cancel(null);
        playersInParkour.remove(uuid);
    }

    public void loadParkourFromFile() {
        if (!jsonPath.toFile().exists()) {
            ParallelUtils.log(Level.WARNING, "Parkour JSON file does not exist, skipping loading.");
            return;
        }
        String data;
        try {
            data = Files.readString(jsonPath);
            JSONParser parser = new JSONParser();
            JSONArray arr = (JSONArray)parser.parse(data);
            for (Object o : arr) {
                JSONObject json = (JSONObject)o;
                List<Location> positions = new ArrayList<>();
                String name = (String)json.get("name");
                for (Object p : (JSONArray)json.get("positions")) {
                    JSONObject position = (JSONObject)p;
                    String w = (String)position.get("world");
                    World world = puPlugin.getServer().getWorld(w);
                    if (world == null) {
                        ParallelUtils.log(Level.SEVERE, "Parkour course " + name + " has a position set in an unknown world: " + w);
                        return;
                    }
                    int x = ((Long)position.get("x")).intValue();
                    int y = ((Long)position.get("y")).intValue();
                    int z = ((Long)position.get("z")).intValue();
                    positions.add(new Location(world, x, y, z));
                }
                boolean effects = (boolean)json.get("allow_effects");
                parkourLayouts.put(name, new ParkourLayout(name, positions, effects));
            }
            ParallelUtils.log(Level.INFO, "Loaded " + parkourLayouts.size() + " parkour layouts.");
        } catch (IOException e) {
            ParallelUtils.log(Level.SEVERE, "Failed to load parkour layouts!\n" + e.getMessage());
        } catch (ParseException e) {
            ParallelUtils.log(Level.SEVERE, "Failed to parse parkour layout data!\n" + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void saveParkourToFile() {
        JSONArray json = new JSONArray();
        for (Map.Entry<String, ParkourLayout> e : parkourLayouts.entrySet()) {
            ParkourLayout p = e.getValue();
            JSONObject entry = new JSONObject();
            entry.put("name", p.name());
            JSONArray positions = new JSONArray();
            p.positions().forEach(x -> {
               JSONObject position = new JSONObject();
               position.put("world", x.getWorld().getName());
               position.put("x", x.getBlockX());
               position.put("y", x.getBlockY());
               position.put("z", x.getBlockZ());
               positions.add(position);
            });
            entry.put("positions", positions);
            entry.put("allow_effects", p.allowEffects());
            json.add(entry);
        }
        try {
            Files.writeString(jsonPath, json.toJSONString());
            ParallelUtils.log(Level.INFO, "Saved " + parkourLayouts.size() + " parkour layouts.");
        } catch (IOException e) {
            ParallelUtils.log(Level.SEVERE, "Failed to save parkour layouts!\n" + e.getMessage());
        }
    }

    public List<String> getAllLayoutNames() { return parkourLayouts.keySet().stream().toList(); }

    public String getTimeString(long time) {
        return timerFormat.format(new Date(time));
    }

    public boolean parkourNameExists(String name) { return parkourLayouts.get(name) != null; }

    public ParallelUtils getPlugin() { return puPlugin; }

    public static ParallelParkour get() { return Instance; }
}
