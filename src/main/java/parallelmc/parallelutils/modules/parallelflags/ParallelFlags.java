package parallelmc.parallelutils.modules.parallelflags;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.session.SessionManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.parallelflags.events.ParallelFlagsDeathListener;
import parallelmc.parallelutils.modules.parallelflags.events.ParallelFlagsInteractListener;
import parallelmc.parallelutils.modules.parallelflags.events.ParallelFlagsPlaceListener;
import parallelmc.parallelutils.modules.parallelflags.session.CustomArmorDeny;
import parallelmc.parallelutils.modules.parallelflags.session.CustomArmorHealth;
import parallelmc.parallelutils.modules.parallelflags.session.ElytraFlagHandler;
import parallelmc.parallelutils.modules.parallelflags.session.FlyFlagHandler;

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

			// Griefing protection
			if (!registry.addIntegerFlag("tnt-disallow-time")) {
				Parallelutils.log(Level.WARNING, "Unable to create tnt-disallow-time flag. Will not use");
			}

			if (!registry.addIntegerFlag("wither-skull-disallow-time")) {
				Parallelutils.log(Level.WARNING, "Unable to create wither-skull-disallow-time flag. Will not use");
			}


			// Custom armor deny
			if (!registry.addIntegerFlag("wearing-custom-helm-deny")) {
				Parallelutils.log(Level.WARNING, "Unable to create wearing-custom-helm-deny flag. Will not use");
			}

			if (!registry.addIntegerFlag("wearing-custom-chestplate-deny")) {
				Parallelutils.log(Level.WARNING, "Unable to create wearing-custom-chestplate-deny flag. Will not use");
			}

			if (!registry.addIntegerFlag("wearing-custom-leggings-deny")) {
				Parallelutils.log(Level.WARNING, "Unable to create wearing-custom-leggings-deny flag. Will not use");
			}

			if (!registry.addIntegerFlag("wearing-custom-boots-deny")) {
				Parallelutils.log(Level.WARNING, "Unable to create wearing-custom-boots-deny flag. Will not use");
			}

			if (!registry.addStringFlag("custom-armor-deny-message")) {
				Parallelutils.log(Level.WARNING, "Unable to create custom-armor-deny-message flag. Will not use");
			}

			// Custom armor damage
			if (!registry.addIntegerFlag("wearing-custom-helm-damage")) {
				Parallelutils.log(Level.WARNING, "Unable to create wearing-custom-helm-damage flag. Will not use");
			}

			if (!registry.addIntegerFlag("wearing-custom-chestplate-damage")) {
				Parallelutils.log(Level.WARNING, "Unable to create wearing-custom-chestplate-damage flag. Will not use");
			}

			if (!registry.addIntegerFlag("wearing-custom-leggings-damage")) {
				Parallelutils.log(Level.WARNING, "Unable to create wearing-custom-leggings-damage flag. Will not use");
			}

			if (!registry.addIntegerFlag("wearing-custom-boots-damage")) {
				Parallelutils.log(Level.WARNING, "Unable to create wearing-custom-boots-damage flag. Will not use");
			}

			if (!registry.addStringFlag("custom-armor-damage-message")) {
				Parallelutils.log(Level.WARNING, "Unable to create custom-armor-damage-message flag. Will not use");
			}

			if (!registry.addIntegerFlag("custom-armor-damage-amount")) {
				Parallelutils.log(Level.WARNING, "Unable to create custom-armor-damage-amount flag. Will not use");
			}

			if (!registry.addIntegerFlag("custom-armor-damage-delay")) {
				Parallelutils.log(Level.WARNING, "Unable to create custom-armor-damage-delay flag. Will not use");
			}

			if (!registry.addStringFlag("custom-armor-damage-death")) {
				Parallelutils.log(Level.WARNING, "Unable to create custom-armor-damage-death flag. Will not use");
			}


			if (!registry.addStateflag("parallel-fly", false)) {
				Parallelutils.log(Level.WARNING, "Unable to create parallel-fly flag. Will not use");
			}
			if (!registry.addStateflag("parallel-glide", false)) {
				Parallelutils.log(Level.WARNING, "Unable to create parallel-glide flag. Will not use");
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

		SessionManager sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();

		sessionManager.registerHandler(CustomArmorDeny.FACTORY, null);
		sessionManager.registerHandler(CustomArmorHealth.FACTORY, null);
		sessionManager.registerHandler(FlyFlagHandler.FACTORY, null);
		sessionManager.registerHandler(ElytraFlagHandler.FACTORY, null);

		manager.registerEvents(new ParallelFlagsInteractListener(), plugin);
		manager.registerEvents(new ParallelFlagsPlaceListener(), plugin);
		manager.registerEvents(new ParallelFlagsDeathListener(), plugin);
	}

	@Override
	public void onDisable() {
		SessionManager sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();
		sessionManager.unregisterHandler(CustomArmorDeny.FACTORY);
	}
}
