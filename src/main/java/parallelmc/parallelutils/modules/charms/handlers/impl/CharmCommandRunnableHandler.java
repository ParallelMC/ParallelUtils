package parallelmc.parallelutils.modules.charms.handlers.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;
import parallelmc.parallelutils.modules.charms.data.CharmOptions;
import parallelmc.parallelutils.modules.charms.data.IEffectSettings;
import parallelmc.parallelutils.modules.charms.data.impl.RunnableCommandSettings;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.handlers.ICharmRunnableHandler;

import java.util.HashMap;
import java.util.List;

public class CharmCommandRunnableHandler extends ICharmRunnableHandler {
	@Override
	public HandlerType getHandlerType() {
		return HandlerType.COMMAND_RUNNABLE;
	}

	@Nullable
	@Override
	public BukkitRunnable getRunnable(Player player, ItemStack item, CharmOptions options) {
		HashMap<HandlerType, IEffectSettings> effects = options.getEffects();

		IEffectSettings settings = effects.get(HandlerType.COMMAND_RUNNABLE);

		if (settings instanceof RunnableCommandSettings runnableCommandSettings) {
			TagResolver resolver = TagResolver.resolver(
					Placeholder.component("displayname", player.displayName())
			);

			List<String> commands = runnableCommandSettings.getCommands();

			if (commands == null) return null;

			return new BukkitRunnable() {
				@Override
				public void run() {
					for (String c : commands) {
						Component deserialized = MiniMessage.miniMessage().deserialize(c, resolver);

						String serialized = PlainTextComponentSerializer.plainText().serialize(deserialized);

						player.getServer().dispatchCommand(player.getServer().getConsoleSender(), serialized);
					}
				}
			};
		}

		return null;
	}
}
