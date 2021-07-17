package parallelmc.parallelutils.modules.performanceTools;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.performanceTools.commands.FindLoadedChunksCommand;

import java.util.logging.Level;

/**
 * This module adds tools to help diagnose performance issues
 */
public class PerformanceTools implements ParallelModule {
	@Override
	public void onEnable() {
		PluginManager manager = Bukkit.getPluginManager();
		Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

		if (plugin == null) {
			Parallelutils.log(Level.SEVERE, "Unable to enable PerformanceTools. Plugin " + Constants.PLUGIN_NAME
					+ " does not exist!");
			return;
		}

		Parallelutils puPlugin = (Parallelutils) plugin;

		if (!puPlugin.registerModule("PerformanceTools", this)) {
			Parallelutils.log(Level.SEVERE, "Unable to register module PerformanceTools! " +
					"Module may already be registered. Quitting...");
			return;
		}

		puPlugin.addCommand("loadedChunks", new FindLoadedChunksCommand());
	}

	@Override
	public void onDisable() {

	}
}
