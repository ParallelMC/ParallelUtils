package parallelmc.parallelutils.modules.parallelquests.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import parallelmc.parallelutils.modules.parallelquests.ParallelQuests;

import java.sql.*;

public class OnJoinLeave implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        ParallelQuests.get().loadPlayerQuestStatus(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ParallelQuests.get().savePlayerQuestStatus(player, true);
        ParallelQuests.getConversationManager().endConversation(player);
    }
}
