package parallelmc.parallelutils.modules.performanceTools;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.performanceTools.commands.FindLoadedChunksCommand;

import java.util.logging.Level;

/**
 * This module adds tools to help diagnose performance issues
 */
public class PerformanceTools implements ParallelModule {

	private BukkitTask loaderDetector;
	@Override
	public void onEnable() {
		PluginManager manager = Bukkit.getPluginManager();
		Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);
		int CLDhours = 1;

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

		//start searching for chunk loaders every [time]
		loaderDetector = new LoaderDetectorTask().runTaskTimer(plugin,0,20*60*60*CLDhours);
	}

	@Override
	public void onDisable() {
		loaderDetector.cancel();
	}
}
