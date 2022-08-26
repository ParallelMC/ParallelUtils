package parallelmc.parallelutils.modules.paralleltutorial.handlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import parallelmc.parallelutils.modules.paralleltutorial.ParallelTutorial;


public class OnJoinAfterOnLeave implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerLeaveDuringTutorial(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (ParallelTutorial.get().startPoints.containsKey(player)) {
            ParallelTutorial.get().handleReconnectedPlayer(player);
        }
    }
}
