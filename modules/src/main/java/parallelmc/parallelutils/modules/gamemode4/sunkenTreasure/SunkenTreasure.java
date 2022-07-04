package parallelmc.parallelutils.modules.gamemode4.sunkenTreasure;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.gamemode4.sunkenTreasure.events.TreasureChecker;

import java.net.URLClassLoader;
import java.util.logging.Level;

public class SunkenTreasure extends ParallelModule {
	public SunkenTreasure(URLClassLoader classLoader) {
		super(classLoader);
	}

	@Override
	public void onLoad() {

	}

	@Override
	public void onEnable() {
		PluginManager manager = Bukkit.getPluginManager();
		Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

		if (plugin == null) {
			ParallelUtils.log(Level.SEVERE, "Unable to enable SunkenTreasure. Plugin " + Constants.PLUGIN_NAME + " does not exist!");
			return;
		}

		ParallelUtils puPlugin = (ParallelUtils) plugin;

		if (!puPlugin.registerModule(this)) {
			ParallelUtils.log(Level.SEVERE, "Unable to register module SunkenTreasure! Module may already be registered. Quitting...");
			return;
		}

		FileConfiguration config = puPlugin.getConfig();

		String treasureLoot = config.getString("treasure_loot", "minecraft:loot_tables/blocks/sand");

		ParallelUtils.log(Level.INFO, treasureLoot);

		manager.registerEvents(new TreasureChecker(treasureLoot), plugin);
	}

	@Override
	public void onDisable() {

	}

	@Override
	public void onUnload() {

	}

	@Override
	public @NotNull String getName() {
		return "SunkenTreasure";
	}
}
