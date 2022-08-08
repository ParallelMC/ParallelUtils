package parallelmc.parallelutils.modules.parallelflags;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.MapFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.session.SessionManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelClassLoader;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelflags.events.*;
import parallelmc.parallelutils.modules.parallelflags.session.*;

import java.net.URLClassLoader;
import java.util.logging.Level;

/**
 * This module implements custom flags for WorldGuard
 */
public class ParallelFlags extends ParallelModule {

	public ParallelFlags(ParallelClassLoader classLoader) {
		super(classLoader);
	}

	@Override
	public void onLoad() {
		PluginManager manager = Bukkit.getPluginManager();
		Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

		if (plugin == null) {
			ParallelUtils.log(Level.SEVERE, "Unable to enable ParallelFlags. Plugin " + Constants.PLUGIN_NAME
					+ " does not exist!");
			return;
		}

		Plugin worldGuard = manager.getPlugin("WorldGuard");

		if (worldGuard == null) {
			ParallelUtils.log(Level.WARNING, "WorldGuard not found. Will not enable.");
			return;
		}

		ParallelUtils puPlugin = (ParallelUtils) plugin;

		if (!puPlugin.registerModule(this)) {
			ParallelUtils.log(Level.SEVERE, "Unable to register module ParallelFlags! " +
					"Module may already be registered. Quitting...");
			return;
		}

		try {
			CustomFlagRegistry registry = CustomFlagRegistry.getInstance();

			if (!registry.addStateflag("allow-trapdoors", true)) {
				ParallelUtils.log(Level.WARNING, "Unable to create trapdoors flag. Will not use");
			}

			// Griefing protection
			if (!registry.addIntegerFlag("tnt-disallow-time")) {
				ParallelUtils.log(Level.WARNING, "Unable to create tnt-disallow-time flag. Will not use");
			}

			if (!registry.addIntegerFlag("wither-skull-disallow-time")) {
				ParallelUtils.log(Level.WARNING, "Unable to create wither-skull-disallow-time flag. Will not use");
			}


			// Custom armor deny
			if (!registry.addIntegerFlag("wearing-custom-helm-deny")) {
				ParallelUtils.log(Level.WARNING, "Unable to create wearing-custom-helm-deny flag. Will not use");
			}

			if (!registry.addIntegerFlag("wearing-custom-chestplate-deny")) {
				ParallelUtils.log(Level.WARNING, "Unable to create wearing-custom-chestplate-deny flag. Will not use");
			}

			if (!registry.addIntegerFlag("wearing-custom-leggings-deny")) {
				ParallelUtils.log(Level.WARNING, "Unable to create wearing-custom-leggings-deny flag. Will not use");
			}

			if (!registry.addIntegerFlag("wearing-custom-boots-deny")) {
				ParallelUtils.log(Level.WARNING, "Unable to create wearing-custom-boots-deny flag. Will not use");
			}

			if (!registry.addStringFlag("custom-armor-deny-message")) {
				ParallelUtils.log(Level.WARNING, "Unable to create custom-armor-deny-message flag. Will not use");
			}

			// Custom armor damage
			if (!registry.addIntegerFlag("wearing-custom-helm-damage")) {
				ParallelUtils.log(Level.WARNING, "Unable to create wearing-custom-helm-damage flag. Will not use");
			}

			if (!registry.addIntegerFlag("wearing-custom-chestplate-damage")) {
				ParallelUtils.log(Level.WARNING, "Unable to create wearing-custom-chestplate-damage flag. Will not use");
			}

			if (!registry.addIntegerFlag("wearing-custom-leggings-damage")) {
				ParallelUtils.log(Level.WARNING, "Unable to create wearing-custom-leggings-damage flag. Will not use");
			}

			if (!registry.addIntegerFlag("wearing-custom-boots-damage")) {
				ParallelUtils.log(Level.WARNING, "Unable to create wearing-custom-boots-damage flag. Will not use");
			}

			if (!registry.addStringFlag("custom-armor-damage-message")) {
				ParallelUtils.log(Level.WARNING, "Unable to create custom-armor-damage-message flag. Will not use");
			}

			if (!registry.addIntegerFlag("custom-armor-damage-amount")) {
				ParallelUtils.log(Level.WARNING, "Unable to create custom-armor-damage-amount flag. Will not use");
			}

			if (!registry.addIntegerFlag("custom-armor-damage-delay")) {
				ParallelUtils.log(Level.WARNING, "Unable to create custom-armor-damage-delay flag. Will not use");
			}

			if (!registry.addStringFlag("custom-armor-damage-death")) {
				ParallelUtils.log(Level.WARNING, "Unable to create custom-armor-damage-death flag. Will not use");
			}


			if (!registry.addStateflag("parallel-fly", false)) {
				ParallelUtils.log(Level.WARNING, "Unable to create parallel-fly flag. Will not use");
			}
			if (!registry.addStateflag("parallel-glide", false)) {
				ParallelUtils.log(Level.WARNING, "Unable to create parallel-glide flag. Will not use");
			}

			if (!registry.addStateflag("empty-inventory", false)) {
				ParallelUtils.log(Level.WARNING, "Unable to create empty-inventory flag. Will not use");
			}


			if (!registry.addStateflag("keep-exp", false)) {
				ParallelUtils.log(Level.WARNING, "Unable to create keep-exp flag. Will not use");
			}

			if (!registry.addStateflag("keep-inventory", false)) {
				ParallelUtils.log(Level.WARNING, "Unable to create keep-inventory flag. Will not use");
			}

			if (!registry.addLocationFlag("respawn-location")) {
				ParallelUtils.log(Level.WARNING, "Unable to create respawn-location flag. Will not use");
			}

			if (!registry.addStateflag("prevent-item-damage", false)) {
				ParallelUtils.log(Level.WARNING, "Unable to create prevent-item-damage flag. Will not use");
			}

			// These are flags that are too annoying to add to the custom flag registry




			if (!registry.addMiscFlag("effect", new MapFlag<>("effect", new StringFlag("type"), new IntegerFlag("strength")))) {
				ParallelUtils.log(Level.WARNING, "Unable to create effect flag. Will not use");
			}


		} catch (NoClassDefFoundError e) {
			ParallelUtils.log(Level.SEVERE, "Unable to load WorldGuard! Something is wrong!");
		}
	}

	@Override
	public void onEnable() {
		PluginManager manager = Bukkit.getPluginManager();
		Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

		if (plugin == null) {
			ParallelUtils.log(Level.SEVERE, "Unable to enable ParallelFlags. Plugin " + Constants.PLUGIN_NAME
					+ " does not exist!");
			return;
		}

		Plugin worldGuard = manager.getPlugin("WorldGuard");

		if (worldGuard == null) {
			ParallelUtils.log(Level.WARNING, "WorldGuard not found. Will not enable.");
			return;
		}

		FlagRegistry flagRegistry = WorldGuard.getInstance().getFlagRegistry();

		SessionManager sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();

		sessionManager.registerHandler(CustomArmorDeny.FACTORY, null);
		sessionManager.registerHandler(CustomArmorHealth.FACTORY, null);
		sessionManager.registerHandler(FlyFlagHandler.FACTORY, null);
		sessionManager.registerHandler(ElytraFlagHandler.FACTORY, null);
		sessionManager.registerHandler(InventoryClearHandler.FACTORY, null);

		if (flagRegistry.get("effect") != null) sessionManager.registerHandler(EffectFlagSession.FACTORY, null);

		manager.registerEvents(new ParallelFlagsInteractListener(), plugin);
		manager.registerEvents(new ParallelFlagsPlaceListener(), plugin);
		manager.registerEvents(new ParallelFlagsDeathMsgListener(), plugin);
		manager.registerEvents(new ParallelFlagsDeathListener(), plugin);
		manager.registerEvents(new ParallelFlagsItemDamageListener(), plugin);
	}

	@Override
	public void onDisable() {
		SessionManager sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();
		sessionManager.unregisterHandler(CustomArmorDeny.FACTORY);
	}

	@Override
	public void onUnload() {

	}

	@Override
	public @NotNull String getName() {
		return "ParallelFlags";
	}
}
