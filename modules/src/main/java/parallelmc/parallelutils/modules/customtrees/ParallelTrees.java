package parallelmc.parallelutils.modules.customtrees;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.ParallelUtils;

import java.util.logging.Level;

public class ParallelTrees implements ParallelModule {
	@Override
	public void onLoad() {

	}

	@Override
	public void onEnable() {
		PluginManager manager = Bukkit.getPluginManager();
		Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

		if (plugin == null) {
			ParallelUtils.log(Level.SEVERE, "Unable to enable ParallelTrees. Plugin " + Constants.PLUGIN_NAME
					+ " does not exist!");
			return;
		}

		ParallelUtils puPlugin = (ParallelUtils) plugin;

		if (!puPlugin.registerModule(this)) {
			ParallelUtils.log(Level.SEVERE, "Unable to register module ParallelTrees! " +
					"Module may already be registered. Quitting...");
			return;
		}

		if (manager.getPlugin("FractalForest") == null) {
			ParallelUtils.log(Level.WARNING, "Could not find FractalForest. Will not continue initialization");
			return;
		}

		TreeInitializer initializer = new TreeInitializer();
		initializer.initialize();
	}

	@Override
	public void onDisable() {

	}

	@Override
	public @NotNull String getName() {
		return "ParallelTrees";
	}
}
