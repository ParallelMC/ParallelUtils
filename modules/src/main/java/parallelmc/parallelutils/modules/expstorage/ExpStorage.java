package parallelmc.parallelutils.modules.expstorage;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.expstorage.commands.DepositExperience;
import parallelmc.parallelutils.modules.expstorage.commands.WithdrawExperience;
import parallelmc.parallelutils.modules.expstorage.events.EnderChestRightClick;

import java.util.logging.Level;

public class ExpStorage implements ParallelModule {

	@Override
	public void onEnable() {
		PluginManager manager = Bukkit.getPluginManager();
		Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

		if (plugin == null) {
			Parallelutils.log(Level.SEVERE, "Unable to enable ExpStorage. Plugin " + Constants.PLUGIN_NAME + " does not exist!");
			return;
		}

		Parallelutils puPlugin = (Parallelutils) plugin;

		if (!puPlugin.registerModule("ExpStorage", this)) {
			Parallelutils.log(Level.SEVERE, "Unable to register module ExpStorage! Module may already be registered. Quitting...");
			return;
		}

		ExpDatabase database = new ExpDatabase(puPlugin);

		manager.registerEvents(new EnderChestRightClick(puPlugin, database), puPlugin);

		puPlugin.getCommand("depositexp").setExecutor(new DepositExperience(puPlugin, database));
		puPlugin.getCommand("withdrawexp").setExecutor(new WithdrawExperience(puPlugin, database));
	}

	@Override
	public void onDisable() {

	}
}
