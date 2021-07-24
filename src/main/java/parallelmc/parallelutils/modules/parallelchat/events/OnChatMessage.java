package parallelmc.parallelutils.modules.parallelchat.events;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import parallelmc.parallelutils.Parallelutils;

import java.util.logging.Level;

public class OnChatMessage implements Listener {
	/**
	 * This event handler allows players to link their held item in chat if they type [item]
	 */
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onChatMessage(AsyncChatEvent event) {
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();

		Component name = item.displayName().hoverEvent(item.asHoverEvent());
		Parallelutils.log(Level.INFO, item.asHoverEvent().toString());

		TextComponent component = Component.text()
				.append(name)
				.append(Component.text(" x" + item.getAmount(), item.displayName().color()))
				.build();

		Component incomingMessage = event.message();

		Component outgoingMessage = incomingMessage.replaceText(x -> x.once().match("\\[item\\]").replacement(component));

		// can't put nothing in chat
		if (item.getType() == Material.AIR) {
			if (incomingMessage.contains(outgoingMessage)) {
				// [item] was not found. No need to stop the message
				return;
			}
			player.sendMessage(Component.text("[", NamedTextColor.DARK_AQUA)
					.append(Component.text("P", NamedTextColor.WHITE, TextDecoration.BOLD))
					.append(Component.text("]", NamedTextColor.DARK_AQUA))
					.append(Component.text(" Cannot link Air into chat!", NamedTextColor.WHITE)));
			event.setCancelled(true);
		} else {
			event.renderer(ChatRenderer.defaultRenderer());
			event.message(outgoingMessage);
		}
	}
}
