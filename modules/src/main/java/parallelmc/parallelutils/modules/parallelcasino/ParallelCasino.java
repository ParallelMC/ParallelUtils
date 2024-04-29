package parallelmc.parallelutils.modules.parallelcasino;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelClassLoader;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelcasino.commands.OpenBlackjack;
import parallelmc.parallelutils.modules.parallelcasino.commands.OpenDeucesWild;
import parallelmc.parallelutils.modules.parallelcasino.events.OnCloseInventory;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class ParallelCasino extends ParallelModule {
    private ParallelUtils puPlugin;

    private final HashSet<UUID> activeGames = new HashSet<>();

    public ParallelCasino(ParallelClassLoader classLoader, List<String> dependents) { super(classLoader, dependents); }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

        if (plugin == null) {
            ParallelUtils.log(Level.SEVERE, "Unable to enable ParallelCasino. Plugin " + Constants.PLUGIN_NAME
                    + " does not exist!");
            return;
        }

        this.puPlugin = (ParallelUtils) plugin;

        if (!puPlugin.registerModule(this)) {
            ParallelUtils.log(Level.SEVERE, "Unable to register module ParallelCasino! " +
                    "Module may already be registered. Quitting...");
            return;
        }

        manager.registerEvents(new OnCloseInventory(), puPlugin);

        puPlugin.getCommand("blackjack").setExecutor(new OpenBlackjack());
        puPlugin.getCommand("deuceswild").setExecutor(new OpenDeucesWild());

        Instance = this;
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onUnload() {

    }

    @Override
    public @NotNull String getName() {
        return "ParallelCasino";
    }

    public ParallelUtils getPlugin() {
        return puPlugin;
    }

    public boolean isPlayerInGame(Player player) { return activeGames.contains(player.getUniqueId()); }

    public void addPlayerToGame(Player player) { activeGames.add(player.getUniqueId()); }

    public void removePlayerFromGame(Player player) { activeGames.remove(player.getUniqueId()); }

    public static ParallelCasino get() { return Instance; }

    private static ParallelCasino Instance;


}
