package parallelmc.parallelutils.modules.parallelquests;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelClassLoader;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelquests.commands.OpenQuestBoard;
import parallelmc.parallelutils.modules.parallelquests.commands.ReloadQuests;
import parallelmc.parallelutils.modules.parallelquests.gui.QuestBoardInventory;
import parallelmc.parallelutils.util.GUIManager;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class ParallelQuests extends ParallelModule {
    public ParallelQuests(ParallelClassLoader classLoader, List<String> dependents) { super(classLoader, dependents); }

    private final HashMap<String, QuestEntry> quests = new HashMap<>();

    private ParallelUtils puPlugin;

    private static ParallelQuests Instance;

    public static ParallelQuests get() {
        return Instance;
    }

    @Override
    public void onLoad() { }

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

        if (plugin == null) {
            ParallelUtils.log(Level.SEVERE, "Unable to enable ParallelQuests. Plugin " + Constants.PLUGIN_NAME + " does not exist!");
            return;
        }

        puPlugin = (ParallelUtils) plugin;

        if (!puPlugin.registerModule(this)) {
            ParallelUtils.log(Level.SEVERE, "Unable to register module ParallelQuests! Module may already be registered. Quitting...");
            return;
        }

        puPlugin.getCommand("openquestboard").setExecutor(new OpenQuestBoard());
        puPlugin.getCommand("reloadquests").setExecutor(new ReloadQuests());

        loadQuests();

        Instance = this;
    }

    public void reloadQuests() {
        quests.clear();
        loadQuests();
    }

    private void loadQuests() {
        File open = new File(puPlugin.getDataFolder(), "quests.yml");
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(open);
        } catch (Exception e) {
            ParallelUtils.log(Level.SEVERE, "Failed to load quests.yml!");
            return;
        }
        for (String key : config.getKeys(false)) {
            String name = config.getString(key + ".name");
            List<String> description = config.getStringList(key + ".description");
            boolean available = config.getBoolean(key + ".available");
            quests.put(key, new QuestEntry("Parallel-" + key, name, description, available));
        }
        ParallelUtils.log(Level.WARNING, "Loaded " + quests.size() + " quest entries");
    }

    @Nullable
    public QuestEntry getQuestById(String id) {
        return quests.get(id);
    }

    public List<QuestEntry> getAllQuests() {
        return quests.values().stream().toList();
    }

    public void openQuestInventoryForPlayer(Player player) {
        GUIManager.get().openInventoryForPlayer(player, new QuestBoardInventory(puPlugin));
    }

    @Override
    public void onDisable() { }

    @Override
    public void onUnload() { }

    @NotNull
    @Override
    public String getName() { return "ParallelQuests"; }
}
