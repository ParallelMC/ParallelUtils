package parallelmc.parallelutils.modules.discordintegration;

import me.clip.voteparty.plugin.VotePartyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.Parallelutils;

import java.util.logging.Level;

/**
 * The class responsible for registering events pertaining to the discord integration module
 */
public class DiscordIntegrationEventRegistrar {

	private static boolean hasRegistered = false;

	/**
	 * When registerEvents is called, all events relevant to the discord integration module are registered
	 */
	public static void registerEvents() {
		if (!hasRegistered) {
			PluginManager manager = Bukkit.getPluginManager();
			Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

			if (plugin == null) {
				Parallelutils.log(Level.SEVERE, "Unable to register events. Plugin " + Constants.PLUGIN_NAME + " does not exist!");
				return;
			}

			manager.registerEvents(new AdvancementListener(), plugin);

			manager.registerEvents(new JoinQuitSuppressorListener(), plugin);

			Plugin vp = manager.getPlugin("VoteParty");

			if (vp == null) {
				Parallelutils.log(Level.WARNING, "VoteParty not found. Skipping integration...");
			} else {
				manager.registerEvents(new VotePartyListener(((VotePartyPlugin) vp).getVoteParty()), plugin);
			}


			hasRegistered = true;
		}
	}

}
