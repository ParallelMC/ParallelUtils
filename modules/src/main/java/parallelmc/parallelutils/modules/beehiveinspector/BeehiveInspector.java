package parallelmc.parallelutils.modules.beehiveinspector;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelClassLoader;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.beehiveinspector.events.BeehiveBroken;

import java.util.List;
import java.util.logging.Level;

/**
 * A module to replicate the behavior of the Gamemode4 BeehiveInspector datapack
 */
public class BeehiveInspector extends ParallelModule {
    public BeehiveInspector(ParallelClassLoader classLoader, List<String> dependents) {
        super(classLoader, dependents);
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

        if (plugin == null) {
            ParallelUtils.log(Level.SEVERE, "Unable to enable BeehiveInspector. Plugin " + Constants.PLUGIN_NAME + " does not exist!");
            return;
        }

        ParallelUtils puPlugin = (ParallelUtils) plugin;

        if (!puPlugin.registerModule(this)) {
            ParallelUtils.log(Level.SEVERE, "Unable to register module BeehiveInspector! Module may already be registered. Quitting...");
            return;
        }

        manager.registerEvents(new BeehiveBroken(), plugin);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onUnload() {

    }

    @Override
    public @NotNull String getName() {
        return "BeehiveInspector";
    }
}
