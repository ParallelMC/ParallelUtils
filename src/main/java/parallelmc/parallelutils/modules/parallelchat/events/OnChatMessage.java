package parallelmc.parallelutils.modules.parallelchat.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import parallelmc.parallelutils.Parallelutils;

import java.util.logging.Level;

public class OnChatMessage implements Listener {
    /**
     * This event handler allows players to link their held item in chat if they type [item]
     */
    // receive the message after all other plugins
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onChatMessage(AsyncPlayerChatEvent event) {
        if (event.getMessage().equals("[item]")) {
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();
            // can't put nothing in chat
            if (item.getType() == Material.AIR) {
                event.setCancelled(true);
                player.sendMessage("§3[§f§lP§3] §fCannot link Air into chat!");
            }
            else {
                // cancel original message
                event.setCancelled(true);
                TextComponent component = Component.text("§3[§f§lP§3] ")
                        .append(player.displayName())
                        .append(Component.text("§f linked an item: "))
                        .append(item.displayName())
                        .append(Component.text(" x" + item.getAmount(), item.displayName().color()))
                        .build();

                // ik this sucks but its the best way to do it
                for (Player p : event.getRecipients()) {
                    p.sendMessage(component);
                }

                // since message is cancelled log it ourselves
                Parallelutils.log(Level.INFO, player.getName() + " linked a " + item.getI18NDisplayName() + " in chat.");

            }

        }
    }
}
