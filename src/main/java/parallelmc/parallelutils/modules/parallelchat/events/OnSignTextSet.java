package parallelmc.parallelutils.modules.parallelchat.events;

import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

public class OnSignTextSet implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onSignTextSet(SignChangeEvent event) {
        if (!event.getPlayer().hasPermission("parallelutils.bypass.antislur")) {
            Sign sign = (Sign)event.getBlock().getState();
            // fuck u paper im not iterating through components
            String text = String.join("\n", event.getLines());
            text = text.toLowerCase().replace(" ", "");
            if (ParallelChat.get().bannedWords.stream().anyMatch(text::contains)) {
                event.setCancelled(true);
                ParallelChat.sendParallelMessageTo(event.getPlayer(), "Please do not say that on signs.");
            }
        }
    }
}
