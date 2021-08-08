package parallelmc.parallelutils.modules.parallelchat;

import io.papermc.paper.chat.ChatRenderer;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ParallelRenderer implements ChatRenderer {

	private final ChatOptions options;

	public ParallelRenderer(ChatOptions options) {
		this.options = options;
	}

	@Override
	public @NotNull Component render(@NotNull Player player, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience audience) {
		String format = options.getFormat();

		String formatted = PlaceholderAPI.setBracketPlaceholders(player, String.format(format, player.getName(), ""));
		formatted = PlaceholderAPI.setPlaceholders(player, formatted);

		Component formattedComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(formatted);

		formattedComponent = formattedComponent.replaceText(x -> x.match("{DISPLAYNAME}").replacement(sourceDisplayName));

		formattedComponent = formattedComponent.replaceText(x -> x.match("{MESSAGE}").replacement(message));

		return formattedComponent;
	}
}
