package parallelmc.parallelutils.custommobs.events;


import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.Parallelutils;

import java.util.logging.Level;

public class CustomMobsEventRegistrar {

	private static boolean hasRegistered = false;

	public static void registerEvents() {
		if (!hasRegistered) {
			PluginManager manager = Bukkit.getPluginManager();
			Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

			if (plugin == null) {
				Parallelutils.log(Level.SEVERE, "Unable to register events. Plugin " + Constants.PLUGIN_NAME + " does not exist!");
				return;
			}

			manager.registerEvents(new CustomMobsDeathListener(), plugin);
			manager.registerEvents(new CustomMobsGeneralEntityListener(), plugin);

			hasRegistered = true;
		}
	}
}
