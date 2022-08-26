package parallelmc.parallelutils.modules.parallelitems;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelUtils;

import java.util.logging.Level;

public class ParallelItemsEventRegistrar {
    private static boolean hasRegistered = false;

    /**
     * When registerEvents is called, all events relevant to the discord integration module are registered
     */
    public static void registerEvents() {
        if (!hasRegistered) {
            PluginManager manager = Bukkit.getPluginManager();
            Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

            if (plugin == null) {
                ParallelUtils.log(Level.SEVERE, "Unable to register events. Plugin "
                        + Constants.PLUGIN_NAME + " does not exist!");
                return;
            }

            manager.registerEvents(new PlayerInteractListener(), plugin);

            hasRegistered = true;
        }
    }
}
