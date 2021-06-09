package parallelmc.parallelutils.discordintegration;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;

public class JoinSuppressorListener implements Listener {

	public static ArrayList<String> hiddenUsers = new ArrayList<>();

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		if (hiddenUsers.contains(player.getName().strip())) {
			event.setJoinMessage(""); // This might need to change, but it needs to be tested
		}
	}
}
