package parallelmc.parallelutils.modules.parallelflags.events;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import parallelmc.parallelutils.ParallelUtils;

import java.util.HashMap;
import java.util.logging.Level;

public class ParallelFlagsDeathMsgListener implements Listener {

	private static final HashMap<String, String> playerDeathMessages = new HashMap<>();

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		EntityDamageEvent lastDamage = player.getLastDamageCause();

		if (lastDamage != null && lastDamage.getCause() == EntityDamageEvent.DamageCause.CUSTOM) {
			ParallelUtils.log(Level.INFO, "Custom Damage");
			String message = playerDeathMessages.get(player.getName());
			if (message == null) return;

			playerDeathMessages.remove(player.getName());

			event.deathMessage(Component.text(message));
		}

	}

	public static void setPlayerDeathMessage(String playerName, String message) {
		playerDeathMessages.put(playerName, message);
	}
}
