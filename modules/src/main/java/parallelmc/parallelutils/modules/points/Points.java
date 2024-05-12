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
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelClassLoader;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.points.commands.ViewPoints;
import parallelmc.parallelutils.modules.points.events.OnAdvancementDone;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class Points extends ParallelModule {
    public Points(ParallelClassLoader classLoader, List<String> dependents) { super(classLoader, dependents); }

    private final HashMap<String, Integer> advancementMap = new HashMap<>();

    private final HashMap<UUID, Integer> playerPoints = new HashMap<>();

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

        loadAdvancements();

        Instance = this;
    }

    @Override
    public void onDisable() { }

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

    public static Points get() { return Instance; }

}
