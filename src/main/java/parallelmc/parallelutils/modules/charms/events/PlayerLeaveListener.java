package parallelmc.parallelutils.modules.charms.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.charms.ParallelCharms;
import parallelmc.parallelutils.modules.charms.data.Charm;

import java.util.ArrayList;
import java.util.List;

public class PlayerLeaveListener implements Listener {

	private final Parallelutils puPlugin;
	private final ParallelCharms pCharms;

	public PlayerLeaveListener(Parallelutils puPlugin, ParallelCharms pCharms) {
		this.puPlugin = puPlugin;
		this.pCharms = pCharms;
	}

	// VERY important to be monitor so we don't have ghost runnables
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		ArrayList<Charm> charms = pCharms.removeAllCharms(player);

		if (charms == null) {
			return;
		}

		for (Charm c : charms) {
			List<BukkitRunnable> runnables = c.getRunnables();

			for (BukkitRunnable r : runnables) {
				r.cancel();
			}
		}
	}
}
