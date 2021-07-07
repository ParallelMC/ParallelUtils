package parallelmc.parallelutils.modules.parallelflags;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.parallelflags.events.ParallelFlagsInteractListener;
import parallelmc.parallelutils.modules.parallelflags.events.ParallelFlagsPlaceListener;

import java.util.logging.Level;

/**
 * This module implements custom flags for WorldGuard
 */
public class ParallelFlags implements ParallelModule {

	public void onLoad() {
		PluginManager manager = Bukkit.getPluginManager();
		Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

		if (plugin == null) {
			Parallelutils.log(Level.SEVERE, "Unable to enable ParallelFlags. Plugin " + Constants.PLUGIN_NAME
					+ " does not exist!");
			return;
		}

		Plugin worldGuard = manager.getPlugin("WorldGuard");

		if (worldGuard == null) {
			Parallelutils.log(Level.WARNING, "WorldGuard not found. Will not enable.");
			return;
		}

		Parallelutils puPlugin = (Parallelutils) plugin;

		if (!puPlugin.registerModule("ParallelFlags", this)) {
			Parallelutils.log(Level.SEVERE, "Unable to register module ParallelFlags! " +
					"Module may already be registered. Quitting...");
			return;
		}

		try {
			CustomFlagRegistry registry = CustomFlagRegistry.getInstance();

			if (!registry.addStateflag("allow-trapdoors", true)) {
				Parallelutils.log(Level.WARNING, "Unable to create trapdoors flag. Will not use");
			}

			if (!registry.addIntegerFlag("tnt-disallow-time")) {
				Parallelutils.log(Level.WARNING, "Unable to create tnt-disallow-time flag. Will not use");
			}

			if (!registry.addIntegerFlag("wither-skull-disallow-time")) {
				Parallelutils.log(Level.WARNING, "Unable to create wither-skull-disallow-time flag. Will not use");
			}
		} catch (NoClassDefFoundError e) {
			Parallelutils.log(Level.SEVERE, "Unable to load WorldGuard! Something is wrong!");
		}
	}

	@Override
	public void onEnable() {
		PluginManager manager = Bukkit.getPluginManager();
		Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

		if (plugin == null) {
			Parallelutils.log(Level.SEVERE, "Unable to enable ParallelFlags. Plugin " + Constants.PLUGIN_NAME
					+ " does not exist!");
			return;
		}

		Plugin worldGuard = manager.getPlugin("WorldGuard");

		if (worldGuard == null) {
			Parallelutils.log(Level.WARNING, "WorldGuard not found. Will not enable.");
			return;
		}

		manager.registerEvents(new ParallelFlagsInteractListener(), plugin);
		manager.registerEvents(new ParallelFlagsPlaceListener(), plugin);
	}

	@Override
	public void onDisable() {

	}
}
