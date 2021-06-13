package parallelmc.parallelutils.modules.discordintegration;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.Parallelutils;

import javax.security.auth.login.LoginException;
import java.util.logging.Level;

public class DiscordIntegration implements ParallelModule {

	private BotManager botManager;

	@Override
	public void onEnable() {
		PluginManager manager = Bukkit.getPluginManager();
		Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

		if (plugin == null) {
			Parallelutils.log(Level.SEVERE, "Unable to enable CustomMobs. Plugin " + Constants.PLUGIN_NAME + " does not exist!");
			return;
		}

		Parallelutils puPlugin = (Parallelutils) plugin;

		FileConfiguration config = puPlugin.getConfig();

		if (!puPlugin.registerModule("DiscordIntegration", this)) {
			Parallelutils.log(Level.SEVERE, "Unable to register module DiscordIntegration! Module may already be registered. Quitting...");
			return;
		}

		String token = config.getString("token");
		String serverId = config.getString("server_id");
		String staffId = config.getString("staff_id");

		String staffChannel = config.getString("staff_channel_id");

		// Register Events for the DiscordIntegration Module
		try {
			botManager = new BotManager(token, serverId, staffId);
			botManager.addChannel("staff", staffChannel);
			DiscordIntegrationEventRegistrar.registerEvents();
		} catch (LoginException e) {
			Parallelutils.log(Level.SEVERE, "Unable to initialize BotManager. Is the token valid?");
		}
	}

	@Override
	public void onDisable() {
		if (botManager != null) {
			botManager.disable();
		}
	}
}
