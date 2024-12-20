package parallelmc.parallelutils.modules.points.events;

import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.points.Points;

import java.util.logging.Level;

public class OnAdvancementDone implements Listener {
    @EventHandler
    public void onAdvancementDone(PlayerAdvancementDoneEvent event) {
        Advancement advancement = event.getAdvancement();
        Player player = event.getPlayer();

        int points = Points.get().getPointsForAdvancement(advancement);
        if (points == -1) {
            // If the advancement is a root advancement or recipes advancement, don't print an error message.
            // We can assume that advancements without displays are recipe advancements
            // ParallelUtils.log(Level.WARNING, advancementTitle.toString());
            if (advancement.getRoot() != advancement && advancement.getDisplay() != null) {
                ParallelUtils.log(Level.WARNING, "Advancement " + advancement.getKey().asString() + " has no associated point value! Skipping...");
            }
            return;
        }

        Points.get().awardPoints(player, points);
        // wait 1 tick to send the message so it shows after the advancement
        ParallelChat.sendDelayedParallelMessageTo(player, 1,
                "You've received " + points + " advancement " + ((points == 1) ? "point!" : "points!"));
    }
}
