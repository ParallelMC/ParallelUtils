package parallelmc.parallelutils.modules.parallelquests.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import parallelmc.parallelutils.modules.parallelquests.ParallelQuests;
import parallelmc.parallelutils.modules.parallelquests.dialogue.Conversation;

import java.util.UUID;

public class OnSlotUpdated implements Listener {
    @EventHandler
    public void onSlotUpdated(PlayerItemHeldEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Conversation conversation = ParallelQuests.get().getActiveConversation(uuid);
        if (conversation != null) {
            conversation.choose(event.getPlayer(), event.getNewSlot());
            if (conversation.isFinished())
                ParallelQuests.get().endConversation(uuid);
        }
    }
}
