package parallelmc.parallelutils.modules.discordintegration;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FrozenJoinQuitSuppressorListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST) // This is event priority abuse
	public void onFrozenJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		synchronized (JoinQuitSuppressorListener.hiddenUsersLock) { // NOTE: This MIGHT cause lag problems. It shouldn't, but beware
			if (JoinQuitSuppressorListener.hiddenUsers.contains(player.getName().strip())) {
				event.setJoinMessage("");

				if (Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
					Server server = Bukkit.getServer();
					server.dispatchCommand(server.getConsoleSender(), "v " + player.getName().strip());
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onFrozenQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		synchronized (JoinQuitSuppressorListener.hiddenUsersLock) { // NOTE: This MIGHT cause lag problems. It shouldn't, but beware
			if (JoinQuitSuppressorListener.hiddenUsers.contains(player.getName().strip())) {
				event.setQuitMessage("");
			}
		}
	}
}
