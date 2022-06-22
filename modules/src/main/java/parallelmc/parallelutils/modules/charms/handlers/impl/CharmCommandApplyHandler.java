package parallelmc.parallelutils.modules.charms.handlers.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.charms.data.CharmOptions;
import parallelmc.parallelutils.modules.charms.data.IEffectSettings;
import parallelmc.parallelutils.modules.charms.data.impl.CommandEffectSettings;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.handlers.ICharmApplyHandler;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class CharmCommandApplyHandler extends ICharmApplyHandler {

	@Override
	public void apply(Player player, @NotNull ItemStack item, @NotNull CharmOptions options) {
		HashMap<HandlerType, IEffectSettings> effects = options.getEffects();

		IEffectSettings settings = effects.get(HandlerType.COMMAND_APPLY);

		if (settings == null) {
			return;
		}

		if (settings instanceof CommandEffectSettings commandEffectSettings) {
			List<String> commands = commandEffectSettings.getCommands();

			TagResolver resolver = TagResolver.resolver(
					Placeholder.component("displayname", player.displayName())
			);

			for (String s : commands) {
				Component serialized = MiniMessage.miniMessage().deserialize(s, resolver);
				String finalComm = PlainTextComponentSerializer.plainText().serialize(serialized);

				player.getServer().dispatchCommand(player.getServer().getConsoleSender(), finalComm);
			}
		}
	}

	@Override
	public void remove(Player player, ItemStack item, CharmOptions options) {

	}

	@Override
	public HandlerType getHandlerType() {
		return HandlerType.COMMAND_APPLY;
	}
}
