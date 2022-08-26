package parallelmc.parallelutils.modules.parallelchat.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.meta.BookMeta;

public class OnBookEdit implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBookEdit(PlayerEditBookEvent event) {
        Player player = event.getPlayer();
        BookMeta bookMeta = event.getNewBookMeta();
        // TODO: Add antiswear stuff once it's not jank
        if (bookMeta.hasPages()) {
            for (int i = 1; i <= bookMeta.getPageCount(); i++) {
                Component newText = bookMeta.page(i); // Book pages are 1-indexed
                // Intellij tells me that newText is never null so /shrug
                // Check sign colors permission
                if (!player.hasPermission("parallelutils.book.colors")) {
                    newText = newText.replaceText(x -> x.match("&[[0-9][a-f]]").replacement(""));
                }

                // Check sign hex permission
                if (!player.hasPermission("parallelutils.book.hex")) {
                    newText = newText.replaceText(x -> x.match("&#(.{6})").replacement(""));
                }

                // Check sign formats permission
                if (!player.hasPermission("parallelutils.book.formats")) {
                    newText = newText.replaceText(x -> x.match("&[[l-o]r]").replacement(""));
                }

                // Check sign magic permission
                if (!player.hasPermission("parallelutils.book.magic")) {
                    newText = newText.replaceText(x -> x.match("&k").replacement(""));
                }
                //Update bookmeta
                bookMeta.page(i, LegacyComponentSerializer.legacyAmpersand().deserialize(LegacyComponentSerializer.legacyAmpersand().serialize(newText)));
            }
            // Set the new bookmeta onto the book
            event.setNewBookMeta(bookMeta);
        }
    }
}