package parallelmc.parallelutils.modules.parallelparkour;

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
import parallelmc.parallelutils.modules.parallelparkour.commands.ParallelCreateParkour;
import parallelmc.parallelutils.modules.parallelparkour.commands.ParallelEndParkour;
import parallelmc.parallelutils.modules.parallelparkour.events.OnBlockPlace;
import parallelmc.parallelutils.modules.parallelparkour.events.OnPlayerInteract;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;

public class ParallelParkour extends ParallelModule {
    private ParallelUtils puPlugin;

    private final HashMap<String, ParkourLayout> parkourLayouts = new HashMap<>();
    private final HashMap<UUID, ParkourPlayer> playersInParkour = new HashMap<>();
    private final HashMap<UUID, ParkourLayout> creatingParkour = new HashMap<>();

    private static Path jsonPath;

    public ParallelParkour(ParallelClassLoader classLoader, List<String> dependents) { super(classLoader, dependents); }

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
                        UUID        varchar(36)  not null,
                        Course      varchar(256) not null,
                        Time        bigint       not null,
                        constraint Leaderboard_UUID_index
                            unique(UUID),
                        PRIMARY KEY(UUID)
                    );""");
            conn.commit();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        manager.registerEvents(new OnPlayerInteract(), puPlugin);
        manager.registerEvents(new OnBlockPlace(), puPlugin);

        puPlugin.getCommand("createparkour").setExecutor(new ParallelCreateParkour());
        puPlugin.getCommand("endparkour").setExecutor(new ParallelEndParkour());

        jsonPath = Path.of(puPlugin.getDataFolder().getAbsolutePath() + "/parkour.json");

        loadParkourFromFile();

        Instance = this;
    }

    @Override
    public void onDisable() {
        saveParkourToFile();
    }

    @Override
    public void onUnload() {

    }

    @Override
    public @NotNull String getName() {
        return "ParallelParkour";
    }

    public long getBestTimeFor(Player player, ParkourLayout layout) {
        try (Connection conn = puPlugin.getDbConn()) {
            if (conn == null) throw new SQLException("Unable to establish connection!");
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM Leaderboard WHERE UUID = ? AND Course = ?");
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, layout.name());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                long time = result.getLong("Time");
                statement.close();
                return time;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void saveBestTimeFor(Player player, ParkourPlayer pp) {
        try (Connection conn = puPlugin.getDbConn()) {
            if (conn == null) throw new SQLException("Unable to establish connection!");
            PreparedStatement statement = conn.prepareStatement(
                    "INSERT INTO Leaderboard (UUID, Course, Time) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE Time = ?");
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, pp.getLayout().name());
            statement.setLong(3, pp.endTime);
            statement.setLong(4, pp.endTime);
            statement.execute();
            conn.commit();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public @Nullable ParkourLayout getParkourCreation(Player player) {
        return creatingParkour.get(player.getUniqueId());
    }

    public void startCreatingParkour(Player player, String name) {
        creatingParkour.put(player.getUniqueId(), new ParkourLayout(name, new ArrayList<>()));
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

    public void startParkourFor(Player player, ParkourLayout layout) {
        playersInParkour.put(player.getUniqueId(), new ParkourPlayer(player, layout));
    }

    public void endParkourFor(Player player) {
        UUID uuid = player.getUniqueId();
        ParkourPlayer pp = playersInParkour.get(uuid);
        saveBestTimeFor(player, pp);
        playersInParkour.remove(uuid);
    }

    public void cancelParkourRunFor(Player player) {
        UUID uuid = player.getUniqueId();
        ParkourPlayer pp = playersInParkour.get(uuid);
        if (pp == null) {
            ParallelChat.sendParallelMessageTo(player, "You are not in a parkour course right now!");
            return;
        }
        pp.cancel();
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
                    int x = (int)position.get("x");
                    int y = (int)position.get("y");
                    int z = (int)position.get("z");
                    positions.add(new Location(world, x, y, z));
                }
                parkourLayouts.put(name, new ParkourLayout(name, positions));
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
            json.add(entry);
        }
        try {
            Files.writeString(jsonPath, json.toJSONString());
            ParallelUtils.log(Level.INFO, "Saved " + parkourLayouts + " parkour layouts.");
        } catch (IOException e) {
            ParallelUtils.log(Level.SEVERE, "Failed to save parkour layouts!\n" + e.getMessage());
        }
    }

    public boolean parkourNameExists(String name) { return parkourLayouts.get(name) != null; }

    public ParallelUtils getPlugin() { return puPlugin; }

    public static ParallelParkour get() { return Instance; }
}
