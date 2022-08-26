package parallelmc.parallelutils.modules.charms.handlers.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import parallelmc.parallelutils.modules.charms.data.CharmOptions;
import parallelmc.parallelutils.modules.charms.data.IEffectSettings;
import parallelmc.parallelutils.modules.charms.data.impl.CommandEffectSettings;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.handlers.ICharmHandler;

import java.util.List;

public class CharmCommandKillHandler extends ICharmHandler<PlayerDeathEvent> {
	public CharmCommandKillHandler() {
		super(PlayerDeathEvent.class);
	}

	@Override
	public HandlerType getHandlerType() {
		return HandlerType.COMMAND_KILL;
	}

	@Override
	public void handle(PlayerDeathEvent event, Player killer, ItemStack item, CharmOptions options) {
		if (options == null) return;

		IEffectSettings settings = options.getEffects().get(HandlerType.COMMAND_KILL);

		if (settings instanceof CommandEffectSettings commandEffectSettings) {
			List<String> commands = commandEffectSettings.getCommands();

			if (commands == null) return;

			Component deadName = event.getEntity().customName();

			if (deadName == null) deadName = event.getEntity().name();

			TagResolver resolver = TagResolver.resolver(
					Placeholder.component("displayname", killer.displayName()),
					Placeholder.component("died", deadName)
			);

			for (String s : commands) {
				Component deserialized = MiniMessage.miniMessage().deserialize(s, resolver);

				String serialized = PlainTextComponentSerializer.plainText().serialize(deserialized);

				killer.getServer().dispatchCommand(killer.getServer().getConsoleSender(), serialized);
			}
		}
	}
}
