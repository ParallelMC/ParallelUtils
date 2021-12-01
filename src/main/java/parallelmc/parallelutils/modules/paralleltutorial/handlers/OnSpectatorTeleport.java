package parallelmc.parallelutils.modules.paralleltutorial.handlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import parallelmc.parallelutils.modules.paralleltutorial.ParallelTutorial;

public class OnSpectatorTeleport implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSpectatorTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE) {
            if (ParallelTutorial.playersInTutorial.containsKey(event.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }
}
