package parallelmc.parallelutils.modules.custommobs.events;


import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelUtils;

import java.util.logging.Level;

/**
 * This class is a wrapper that registers events for the custommobs module.
 */
public class CustomMobsEventRegistrar {

	private static boolean hasRegistered = false;

	/**
	 * Register all events related to the custommobs module if they have not been registered.
	 */
	public static void registerEvents() {
		if (!hasRegistered) {
			PluginManager manager = Bukkit.getPluginManager();
			Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

			if (plugin == null) {
				ParallelUtils.log(Level.SEVERE, "Unable to register events. Plugin " + Constants.PLUGIN_NAME + " does not exist!");
				return;
			}

			manager.registerEvents(new CustomMobsDeathListener(), plugin);
			manager.registerEvents(new CustomMobsGeneralEntityListener(), plugin);

			hasRegistered = true;
		}
	}
}
