package parallelmc.parallelutils.modules.parallelparkour.events;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import parallelmc.parallelutils.modules.parallelparkour.ParallelParkour;
import parallelmc.parallelutils.modules.parallelparkour.ParkourPlayer;

public class EndParkourEvents implements Listener {
    // TODO: Implement: leaving, death, etc.
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ParkourPlayer p = ParallelParkour.get().getParkourPlayer(player);
        if (p != null) {
            p.cancel("You left the game.");
            ParallelParkour.get().endParkourFor(player);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        ParkourPlayer p = ParallelParkour.get().getParkourPlayer(player);
        if (p != null) {
            p.cancel("Died during the run.");
            ParallelParkour.get().endParkourFor(player);
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        ParkourPlayer p = ParallelParkour.get().getParkourPlayer(player);
        if (p != null) {
            p.cancel("Teleporting during a run is not allowed.");
            ParallelParkour.get().endParkourFor(player);
        }
    }

    @EventHandler
    public void onPlayerElytra(EntityToggleGlideEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            Player player = (Player)event.getEntity();
            ParkourPlayer p = ParallelParkour.get().getParkourPlayer(player);
            if (p != null) {
                p.cancel("Using an elytra during a run is not allowed.");
                ParallelParkour.get().endParkourFor(player);
            }
        }
    }
}
