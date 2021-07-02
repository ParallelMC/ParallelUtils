package parallelmc.parallelutils.modules.discordintegration;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

/**
 * A Listener to listen for player joins and quits and suppress them if they are in the hidden users array
 */
public class JoinQuitSuppressorListener implements Listener {

	public static ArrayList<String> hiddenUsers = new ArrayList<>();
	public static final Object hiddenUsersLock = new Object();

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		synchronized (hiddenUsersLock) { // NOTE: This MIGHT cause lag problems. It shouldn't, but beware
			if (hiddenUsers.contains(player.getName().strip())) {
				event.joinMessage(Component.text("")); // This might need to change, but it needs to be tested

				if (Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
					Server server = Bukkit.getServer();
					server.dispatchCommand(server.getConsoleSender(), "v " + player.getName().strip());
				}
			}
		}
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		synchronized (hiddenUsersLock) { // NOTE: This MIGHT cause lag problems. It shouldn't, but beware
			if (hiddenUsers.contains(player.getName().strip())) {
				event.quitMessage(Component.text((""))); // This might need to change, but it needs to be tested
			}
		}
	}
}
