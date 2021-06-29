package parallelmc.parallelutils.modules.gamemode4.sunkenTreasure;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.gamemode4.sunkenTreasure.events.TreasureChecker;

import java.util.logging.Level;

public class SunkenTreasure implements ParallelModule {
	@Override
	public void onEnable() {
		PluginManager manager = Bukkit.getPluginManager();
		Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

		if (plugin == null) {
			Parallelutils.log(Level.SEVERE, "Unable to enable CustomMobs. Plugin " + Constants.PLUGIN_NAME + " does not exist!");
			return;
		}

		Parallelutils puPlugin = (Parallelutils) plugin;

		if (!puPlugin.registerModule("SunkenTreasure", this)) {
			Parallelutils.log(Level.SEVERE, "Unable to register module SunkenTreasure! Module may already be registered. Quitting...");
			return;
		}

		FileConfiguration config = puPlugin.getConfig();

		String treasureLoot = config.getString("treasure_loot", "minecraft:loot_tables/blocks/sand");

		Parallelutils.log(Level.INFO, treasureLoot);

		manager.registerEvents(new TreasureChecker(treasureLoot), plugin);
	}

	@Override
	public void onDisable() {

	}
}
