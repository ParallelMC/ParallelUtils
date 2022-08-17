package parallelmc.parallelutils.modules.parallelchat.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

public class OnSignTextSet implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onSignTextSet(SignChangeEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("parallelutils.bypass.antislur")) {
            // fuck u paper im not iterating through components
            String text = String.join("\n", event.getLines());
            text = text.toLowerCase().replace(" ", "");
            if (ParallelChat.get().bannedWords.stream().anyMatch(text::contains)) {
                event.setCancelled(true);
                ParallelChat.sendParallelMessageTo(player, "Please do not say that on signs.");
            }
        }
        // foreach was acting weird so regular for loop tally-ho
        // also rip running these checks for every line
        for (int i = 0; i < event.lines().size(); i++) {
            Component line = event.line(i);
            if (line != null) {
                // check sign colors permission
                if (!player.hasPermission("parallelutils.sign.colors")) {
                    line = line.replaceText(x -> x.match("&[[0-9][a-f]]").replacement(""));
                }

                // check sign hex permission
                if (!player.hasPermission("parallelutils.sign.hex")) {
                    line = line.replaceText(x -> x.match("&#(.{6})").replacement(""));
                }

                // check sign formats permission
                if (!player.hasPermission("parallelutils.sign.formats")) {
                    line = line.replaceText(x -> x.match("&[[l-o]r]").replacement(""));
                }

                // check sign magic permission
                if (!player.hasPermission("parallelutils.sign.magic")) {
                    line = line.replaceText(x -> x.match("&k").replacement(""));
                }

                event.line(i, LegacyComponentSerializer.legacyAmpersand().deserialize(LegacyComponentSerializer.legacyAmpersand().serialize(line)));
            }
        }
    }
}
