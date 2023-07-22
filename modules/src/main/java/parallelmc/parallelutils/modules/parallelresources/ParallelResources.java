package parallelmc.parallelutils.modules.parallelresources;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelClassLoader;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.ParallelUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;

public class ParallelResources extends ParallelModule {

	public static ParallelUtils puPlugin;

	public ParallelResources(ParallelClassLoader classLoader, List<String> dependents) {
		super(classLoader, dependents);
	}

	@Override
	public void onLoad() {
		PluginManager manager = Bukkit.getPluginManager();
		Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

		if (plugin == null) {
			ParallelUtils.log(Level.SEVERE, "Unable to enable ParallelResources. Plugin " + Constants.PLUGIN_NAME + " does not exist!");
			return;
		}

		puPlugin = (ParallelUtils) plugin;

		if (!puPlugin.registerModule(this)) {
			ParallelUtils.log(Level.SEVERE, "Unable to register module ParallelResources! Module may already be registered. Quitting...");
			return;
		}

		ResourceServer server = new ResourceServer();

		try {
			// Create resources directory if it does not exist
			File resourcesDir = new File(puPlugin.getDataFolder(), "resources/");

			if (!resourcesDir.exists()) {
				Files.createDirectory(resourcesDir.toPath());
			}

		} catch (IOException e) {
			e.printStackTrace();
			ParallelUtils.log(Level.SEVERE, "IOException while loading ParallelResources! Quitting...");
		}

		Thread serverThread = new Thread(server);
		serverThread.start();
	}

	@Override
	public void onEnable() {

	}

	@Override
	public void onDisable() {

	}

	@Override
	public void onUnload() {

	}

	@Override
	public @NotNull String getName() {
		return "ParallelResources";
	}
}
