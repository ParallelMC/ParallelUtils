package parallelmc.parallelutils.modules.customtrees;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.Parallelutils;

import java.util.logging.Level;

public class ParallelTrees implements ParallelModule {
	@Override
	public void onEnable() {
		PluginManager manager = Bukkit.getPluginManager();
		Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

		if (plugin == null) {
			Parallelutils.log(Level.SEVERE, "Unable to enable ParallelTrees. Plugin " + Constants.PLUGIN_NAME
					+ " does not exist!");
			return;
		}

		Parallelutils puPlugin = (Parallelutils) plugin;

		if (!puPlugin.registerModule("ParallelTrees", this)) {
			Parallelutils.log(Level.SEVERE, "Unable to register module ParallelTrees! " +
					"Module may already be registered. Quitting...");
			return;
		}

		if (manager.getPlugin("FractalForest") == null) {
			Parallelutils.log(Level.WARNING, "Could not find FractalForest. Will not continue initialization");
			return;
		}

		TreeInitializer initializer = new TreeInitializer();
		initializer.initialize();
	}

	@Override
	public void onDisable() {

	}
}
