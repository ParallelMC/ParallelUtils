package parallelmc.parallelutils.modules.parallelchat.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import parallelmc.parallelutils.Parallelutils;

import java.util.HashMap;
import java.util.logging.Level;

import static io.papermc.paper.chat.ChatRenderer.viewerUnaware;

/**
 * This listener handles ItemChat. It's TERRIBLE right now because of mismatched chat APIs.
 * Paper wants everything to use the new API (AsyncChatEvent and Kyori Adventure), while non-paper plugins
 * use the legacy API. The problem is, they aren't compatible, like, at all. Many other plugins such as
 * Essentials X Chat use the legacy API for formatting messages. This means that we cannot get the chat format
 * through the new API. To combat this, we actually handle both the legacy and new events and message between
 * them to handle the format. After getting the legacy format, we have to do some parsing and placeholder replacement
 * then we can create custom TextRenderer and finally send the message.
 */
public class OnChatMessage implements Listener {
	
	private final HashMap<String, String> nameformats = new HashMap<>();

	/**
	 * This event handler allows players to link their held item in chat if they type [item]
	 */
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onChatMessage(AsyncChatEvent event) {
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();

		Component name = item.displayName().hoverEvent(item.asHoverEvent());

		TextComponent component = Component.text()
				.append(name)
				.append(Component.text(" x" + item.getAmount(), item.displayName().color()))
				.build();

		Component incomingMessage = event.message();

		String format = nameformats.get(player.getName());

		if (format == null) {
			format = player.getName() + " > ";
			Parallelutils.log(Level.INFO, format);
		} else {
			nameformats.remove(player.getName());
		}

		String formatted = PlaceholderAPI.setBracketPlaceholders(player, String.format(format, player.getName(), "")
				.replace("&r", "").replace("§r", "")); // Clear the reset characters

		Component outgoingMessage = incomingMessage.replaceText(x -> x.once().match("\\[item\\]").replacement(component));

		if (incomingMessage.contains(outgoingMessage)) {
			// [item] was not found. No need to stop the message
			return;
		}

		// can't put nothing in chat
		if (item.getType() == Material.AIR) {
			player.sendMessage(Component.text("[", NamedTextColor.DARK_AQUA)
					.append(Component.text("P", NamedTextColor.WHITE, TextDecoration.BOLD))
					.append(Component.text("]", NamedTextColor.DARK_AQUA))
					.append(Component.text(" Cannot link Air into chat!", NamedTextColor.WHITE)));
			event.setCancelled(true);
		} else {
			event.renderer(viewerUnaware((source, sourceDisplayName, message) ->
					LegacyComponentSerializer.legacyAmpersand().deserialize(formatted).append(message)));

			event.message(outgoingMessage);
		}
	}

	/**
	 * This event is purely to capture the format
	 */
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onDeprecatedChatMessage(AsyncPlayerChatEvent event) {
		String format = event.getFormat();
		String playerName = event.getPlayer().getName();

		Parallelutils.log(Level.INFO, format);

		nameformats.put(playerName, format);
	}
}
