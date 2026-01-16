package parallelmc.parallelutils.modules.parallelquests.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.HorseJumpEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import parallelmc.parallelutils.modules.parallelquests.ParallelQuests;

public class OnInput implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        // if they're not flying, we don't need to cancel the event
        // a player in a conversation should already have their speed set to 0
        if (!player.isFlying())
            return;

        if (event.hasChangedPosition() && ParallelQuests.getConversationManager().isInConversation(player))
            event.setCancelled(true);
    }

    @EventHandler
    public void onMountJump(HorseJumpEvent event) {
        for (Entity e : event.getEntity().getPassengers()) {
            if (e instanceof Player player && ParallelQuests.getConversationManager().isInConversation(player)) {
                // horse jumping was moved to the client
                // but, we can still cancel it on the server
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onExitVehicle(VehicleExitEvent event) {
        if (event.getExited() instanceof Player player && ParallelQuests.getConversationManager().isInConversation(player))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        // if a player in a conversation gets teleported for whatever reason,
        // just end the conversation
        if (ParallelQuests.getConversationManager().isInConversation(player)) {
            ParallelQuests.getConversationManager().endConversation(player);
        }
    }
}
