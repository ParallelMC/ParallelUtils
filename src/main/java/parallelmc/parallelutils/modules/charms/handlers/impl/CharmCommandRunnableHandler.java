package parallelmc.parallelutils.modules.charms.handlers.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.charms.ParallelCharms;
import parallelmc.parallelutils.modules.charms.data.Charm;
import parallelmc.parallelutils.modules.charms.data.CharmOptions;
import parallelmc.parallelutils.modules.charms.data.IEffectSettings;
import parallelmc.parallelutils.modules.charms.data.impl.RunnableCommandSettings;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.handlers.ICharmRunnableHandler;
import parallelmc.parallelutils.modules.charms.util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class CharmCommandRunnableHandler extends ICharmRunnableHandler {

	private final ParallelCharms pCharms;

	public CharmCommandRunnableHandler(ParallelCharms pCharms) {
		this.pCharms = pCharms;
	}

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

			Charm charm = Charm.parseCharm(pCharms, item, player);

			if (charm == null) return null;

			UUID uuid = charm.getUUID();

			boolean isArmor = Util.isArmor(item);

			boolean canRun = false;

			if (isArmor) canRun = Util.canRun(pCharms, player, item, uuid);

			boolean finalCanRun = canRun;
			return new BukkitRunnable() {
				@Override
				public void run() {
					// This first part is a little optimization
					if ((isArmor && finalCanRun) || Util.canRun(pCharms, player, item, uuid)) {
						for (String c : commands) {
							Component deserialized = MiniMessage.miniMessage().deserialize(c, resolver);

							String serialized = PlainTextComponentSerializer.plainText().serialize(deserialized);

							player.getServer().dispatchCommand(player.getServer().getConsoleSender(), serialized);
						}
					}
				}
			};
		}

		return null;
	}
}
