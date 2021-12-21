package parallelmc.parallelutils.modules.charms;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.commands.ParallelCommand;
import parallelmc.parallelutils.modules.charms.commands.ApplyCharm;
import parallelmc.parallelutils.modules.charms.commands.RemoveCharm;
import parallelmc.parallelutils.modules.charms.data.Charm;
import parallelmc.parallelutils.modules.charms.data.CharmOptions;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class ParallelCharms implements ParallelModule {


	@Override
	public void onEnable() {
		PluginManager manager = Bukkit.getPluginManager();
		Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

		if (plugin == null) {
			Parallelutils.log(Level.SEVERE, "Unable to enable ParallelCharms. Plugin " + Constants.PLUGIN_NAME
					+ " does not exist!");
			return;
		}

		Parallelutils puPlugin = (Parallelutils) plugin;

		if (!puPlugin.registerModule("ParallelCharms", this)) {
			Parallelutils.log(Level.SEVERE, "Unable to register module ParallelCharms! " +
					"Module may already be registered. Quitting...");
			return;
		}

		CharmOptions testOptions = new CharmOptions(UUID.randomUUID(), null, new HashMap<>(), 123456);

		Charm testCharm = new Charm(testOptions);

		puPlugin.addCommand("applyCharm", new ApplyCharm(testCharm));
		puPlugin.addCommand("removeCharm", new RemoveCharm(testCharm));
	}

	@Override
	public void onDisable() {
	}
}
