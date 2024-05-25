package parallelmc.parallelutils.modules.points;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
import parallelmc.parallelutils.modules.points.commands.OpenPointsRedemption;
import parallelmc.parallelutils.modules.points.commands.RecalculatePoints;
import parallelmc.parallelutils.modules.points.commands.ViewPoints;
import parallelmc.parallelutils.modules.points.events.OnAdvancementDone;
import parallelmc.parallelutils.modules.points.gui.PointsRedeemInventory;
import parallelmc.parallelutils.util.GUIManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;

public class Points extends ParallelModule {
    public Points(ParallelClassLoader classLoader, List<String> dependents) { super(classLoader, dependents); }

    private final HashMap<String, Integer> advancementMap = new HashMap<>();

    private final HashMap<UUID, Integer> playerPoints = new HashMap<>();

    private final List<RedeemableItem> redeemableItems = new ArrayList<>();

    private static Points Instance;

    private  ParallelUtils puPlugin;

    @Override
    public void onLoad() { }

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

        if (plugin == null) {
            ParallelUtils.log(Level.SEVERE, "Unable to enable Points. Plugin " + Constants.PLUGIN_NAME + " does not exist!");
            return;
        }

        puPlugin = (ParallelUtils) plugin;

        if (!puPlugin.registerModule(this)) {
            ParallelUtils.log(Level.SEVERE, "Unable to register module Points! Module may already be registered. Quitting...");
        }

        manager.registerEvents(new OnAdvancementDone(), puPlugin);

        puPlugin.getCommand("points").setExecutor(new ViewPoints());
        puPlugin.getCommand("recalculatepoints").setExecutor(new RecalculatePoints(puPlugin));
        puPlugin.getCommand("openpointsredemption").setExecutor(new OpenPointsRedemption());

        loadAdvancements();
        loadItems();
        loadPlayerPoints();

        Instance = this;
    }

    @Override
    public void onDisable() {
        savePlayerPoints();
    }

    @Override
    public void onUnload() { }

    @Override
    public @NotNull String getName() { return "Points"; }

    public int getPointsForAdvancement(Advancement advancement) {
        return advancementMap.getOrDefault(advancement.getKey().asString(), -1);
    }

    public void awardPoints(Player player, int amount) {
        playerPoints.put(player.getUniqueId(), getPlayerPoints(player) + amount);
    }

    public int getPlayerPoints(Player player) {
        return playerPoints.getOrDefault(player.getUniqueId(), 0);
    }

    public List<RedeemableItem> getRedeemableItems() { return redeemableItems; }

    public void openPointsRedemptionFor(Player player) {
        GUIManager.get().openInventoryForPlayer(player, new PointsRedeemInventory());
    }

    private void loadAdvancements() {
        File file = new File(puPlugin.getDataFolder(), "points.yml");
        FileConfiguration pointsConfig = new YamlConfiguration();
        try {
            if (file.createNewFile()) {
                ParallelUtils.log(Level.WARNING, "points.yml does not exist. Creating...");
            }
            pointsConfig.load(file);
        }
         catch (IOException e) {
            ParallelUtils.log(Level.SEVERE, "Failed to create or read points.yml\n" + e);
            return;
        } catch (Exception e) {
            ParallelUtils.log(Level.SEVERE, "Failed to load points.yml\n" + e);
            return;
        }
        for (String key : pointsConfig.getKeys(true)) {
            if (pointsConfig.isConfigurationSection(key))
                continue;
            int value = pointsConfig.getInt(key);
            String[] split = key.split("\\.");
            if (split.length != 2) {
                ParallelUtils.log(Level.WARNING, "Invalid advancement key: " + key);
                continue;
            }
            Advancement advancement = Bukkit.getAdvancement(new NamespacedKey(split[0], split[1]));
            if (advancement == null) {
                ParallelUtils.log(Level.WARNING, "Unknown advancement: " + key);
                continue;
            }
            advancementMap.put(advancement.getKey().asString(), value);
        }
        ParallelUtils.log(Level.WARNING, "Loaded " + advancementMap.size() + " advancement points.");
    }

    private void loadPlayerPoints() {
        Path path = Path.of(puPlugin.getDataFolder().getAbsolutePath() + "/player_points.json");
        if (!path.toFile().exists()) {
            ParallelUtils.log(Level.WARNING, "Points JSON file does not exist, skipping loading.");
            return;
        }
        String data;
        try {
            data = Files.readString(path);
            JSONParser parser = new JSONParser();
            JSONArray arr = (JSONArray)parser.parse(data);
            for (Object o : arr) {
                JSONObject json = (JSONObject) o;
                UUID uuid = UUID.fromString((String)json.get("uuid"));
                int points = ((Long)json.get("points")).intValue();
                playerPoints.put(uuid, points);
            }
            ParallelUtils.log(Level.INFO, "Loaded " + playerPoints.size() + " player points entries.");
        } catch (IOException | NullPointerException e) {
            ParallelUtils.log(Level.SEVERE, "Failed to load player points!\n" + e.getMessage());
        } catch (ParseException e) {
            ParallelUtils.log(Level.SEVERE, "Failed to parse player points data!\n" + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void savePlayerPoints() {
        Path path = Path.of(puPlugin.getDataFolder().getAbsolutePath() + "/player_points.json");
        JSONArray json = new JSONArray();
        for (Map.Entry<UUID, Integer> e : playerPoints.entrySet()) {
            JSONObject entry = new JSONObject();
            entry.put("uuid", e.getKey().toString());
            entry.put("points", e.getValue());
            json.add(entry);
        }
        try {
            Files.writeString(path, json.toJSONString());
            ParallelUtils.log(Level.INFO, "Saved " + playerPoints.size() + " player points entries.");
        } catch (IOException e) {
            ParallelUtils.log(Level.SEVERE, "Failed to save player points entries!\n" + e.getMessage());
        }
    }

    public int recalculatePlayerPoints() {
        playerPoints.clear();
        Path path;
        try {
            //path = Path.of(puPlugin.getServer().getWorldContainer().getCanonicalPath(), "world", "advancements");
            path = Path.of(puPlugin.getServer().getWorldContainer().getCanonicalPath(), Constants.DEFAULT_WORLD, "advancements");
            ParallelUtils.log(Level.INFO, "Using path: " + path);
        } catch (IOException e) {
            ParallelUtils.log(Level.SEVERE, "Failed to find path to advancements folder! \n" + e.getMessage());
            return -1;
        }
        File[] files = path.toFile().listFiles();
        if (files == null) {
            ParallelUtils.log(Level.SEVERE, "Failed to get files from advancements folder!");
            return -1;
        }
        for (File file : files) {
            // this is horrible practice but its pretty safe to assume nothing in here will have more than 1 period
            String[] split = file.getName().split("\\.");
            // check it anyway
            if (split.length != 2) {
                ParallelUtils.log(Level.SEVERE, "A file has more than one period...somehow. Skipping!");
                continue;
            }
            UUID uuid;
            try {
                uuid = UUID.fromString(split[0]);
            } catch (IllegalArgumentException e) {
                ParallelUtils.log(Level.WARNING, split[0] + " failed to parse into a UUID. Skipping!");
                continue;
            }

            String data;
            try {
                data = Files.readString(file.toPath());
                JSONParser parser = new JSONParser();
                JSONObject obj = (JSONObject) parser.parse(data);

                for (Object o : obj.entrySet()) {
                    // gee, who put this here
                    @SuppressWarnings("unchecked")
                    Map.Entry<String, JSONObject> entry = (Map.Entry<String, JSONObject>) o;

                    String advancement = entry.getKey();
                    // every advancement json file has the DataVersion at the very bottom
                    // so treat this as the "terminator" of sorts
                    if (advancement.equals("DataVersion"))
                        break;
                    JSONObject value = entry.getValue();

                    int points = advancementMap.getOrDefault(advancement, -1);

                    if (points > -1) {
                        if ((Boolean)value.get("done")) {
                            playerPoints.put(uuid, playerPoints.getOrDefault(uuid, 0) + points);
                        }
                    }
                }
            } catch (IOException | NullPointerException e) {
                ParallelUtils.log(Level.SEVERE, "Failed to load file " + file.getName() + "!\n" + e.getMessage());
            } catch (ParseException e) {
                ParallelUtils.log(Level.SEVERE, "Failed to parse " + file.getName() + "!\n" + e.getMessage());
            }
        }
        return playerPoints.size();
    }

    public void loadItems() {
        File itemFile = new File(puPlugin.getDataFolder(), "points_items.yml");
        FileConfiguration itemConfig = new YamlConfiguration;
        try {
            if (itemFile.createNewFile()) {
                ParallelUtils.log(Level.WARNING, "points_items.yml does not exist! Creating...");
            }
            itemConfig.load(itemFile);
        } catch (IOException e) {
            ParallelUtils.log(Level.SEVERE, "Failed to create or read points_items.yml\n" + e);
            return false;
        } catch (Exception e) {
            ParallelUtils.log(Level.SEVERE, "Failed to load points_items.yml\n" + e);
            return false;
        }
        for (String key : itemConfig.getKeys(false)) {
            Material material = Material.valueOf(itemConfig.getString(key + ".material"));
            int modelData = itemConfig.getInt(key + ".modeldata");
            int cost = itemConfig.getInt(key + ".cost");
            String permission = itemConfig.getString(key + ".permission");
            List<String> commands = (List<String>)itemConfig.getList(key + ".commands");
            redeemableItems.add(new RedeemableItem(material, cost, permission, modelData, commands));
        }
        ParallelUtils.log(Level.WARNING, "Loaded " + redeemableItems.size() + " redeemable items.");
    }

    public static Points get() { return Instance; }

}
