package parallelmc.parallelutils.modules.parallelquests.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelquests.ParallelQuests;
import parallelmc.parallelutils.modules.parallelquests.dialogue.Conversation;
import parallelmc.parallelutils.modules.parallelquests.quests.Quest;

import java.util.logging.Level;

public class OnCommand implements Listener {
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        Player player = event.getPlayer();
        if (message.startsWith("/dialogueoption")) {
            event.setCancelled(true);
            Conversation conversation = ParallelQuests.getConversationManager().getActiveConversation(player);
            if (conversation == null) return;
            String[] split = event.getMessage().split(" ");
            if (split.length != 2) return;
            int index;
            try {
                index = Integer.parseInt(split[1]);
            } catch (NumberFormatException e) {
                ParallelUtils.log(Level.SEVERE, "Failed to parse dialogue option index: " + split[1]);
                return;
            }
            conversation.choose(player, index);
            if (conversation.isFinished())
                ParallelQuests.getConversationManager().endConversation(player);
        }
        else if (message.startsWith("/startdialogue")) {
            event.setCancelled(true);
            String[] split = event.getMessage().split(" ");
            if (split.length != 3) return;
            Quest quest = ParallelQuests.get().getQuest(split[1]);
            if (quest == null) {
                ParallelUtils.log(Level.WARNING, "Got null quest when attempting to start dialogue with quest ID " + split[1]);
                return;
            }
            String npcName = split[2];
            quest.enter(player, npcName);
        }
    }
}
