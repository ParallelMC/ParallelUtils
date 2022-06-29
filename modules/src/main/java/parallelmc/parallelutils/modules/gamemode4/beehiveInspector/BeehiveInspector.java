package parallelmc.parallelutils.modules.gamemode4.beehiveInspector;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.gamemode4.beehiveInspector.events.BeehiveBroken;

import java.util.logging.Level;

/**
 * A module to replicate the behavior of the Gamemode4 BeehiveInspector datapack
 */
public class BeehiveInspector implements ParallelModule {
    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

        if (plugin == null) {
            Parallelutils.log(Level.SEVERE, "Unable to enable BeehiveInspector. Plugin " + Constants.PLUGIN_NAME + " does not exist!");
            return;
        }

        Parallelutils puPlugin = (Parallelutils) plugin;

        if (!puPlugin.registerModule(this)) {
            Parallelutils.log(Level.SEVERE, "Unable to register module BeehiveInspector! Module may already be registered. Quitting...");
            return;
        }

        manager.registerEvents(new BeehiveBroken(), plugin);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public @NotNull String getName() {
        return "BeehiveInspector";
    }
}
