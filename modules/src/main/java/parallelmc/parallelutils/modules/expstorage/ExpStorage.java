package parallelmc.parallelutils.modules.expstorage;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelClassLoader;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.expstorage.commands.DepositExperience;
import parallelmc.parallelutils.modules.expstorage.commands.WithdrawExperience;
import parallelmc.parallelutils.modules.expstorage.events.EnderChestRightClick;

import java.net.URLClassLoader;
import java.util.List;
import java.util.logging.Level;

public class ExpStorage extends ParallelModule {

	public ExpStorage(ParallelClassLoader classLoader, List<String> dependents) {
		super(classLoader, dependents);
	}

	@Override
	public void onLoad() {

	}

	@Override
	public void onEnable() {
		PluginManager manager = Bukkit.getPluginManager();
		Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

		if (plugin == null) {
			ParallelUtils.log(Level.SEVERE, "Unable to enable ExpStorage. Plugin " + Constants.PLUGIN_NAME + " does not exist!");
			return;
		}

		ParallelUtils puPlugin = (ParallelUtils) plugin;

		if (!puPlugin.registerModule(this)) {
			ParallelUtils.log(Level.SEVERE, "Unable to register module ExpStorage! Module may already be registered. Quitting...");
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

	@Override
	public void onUnload() {

	}

	@Override
	public @NotNull String getName() {
		return "ExpStorage";
	}
}
