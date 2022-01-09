package parallelmc.parallelutils.modules.paralleltutorial.handlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.paralleltutorial.ParallelTutorial;

import java.util.logging.Level;

public class OnLeaveDuringTutorial implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerLeaveDuringTutorial(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        BukkitTask tutorial = ParallelTutorial.runningTutorials.get(player);
        if (tutorial != null) {
            Parallelutils.log(Level.WARNING, player.getName() + " left during a tutorial! Attempting to fix...");
            tutorial.cancel();
            ParallelTutorial.get().endTutorialFor(player);
            Parallelutils.log(Level.WARNING, player.getName() + " was successfully taken out of the tutorial.");
        }
    }
}
