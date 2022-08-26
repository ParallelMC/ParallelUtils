package parallelmc.parallelutils.modules.charms.handlers.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import parallelmc.parallelutils.modules.charms.data.CharmOptions;
import parallelmc.parallelutils.modules.charms.data.IEffectSettings;
import parallelmc.parallelutils.modules.charms.data.impl.CommandEffectSettings;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.handlers.ICharmHandler;

import java.util.HashMap;
import java.util.List;

public class CharmCommandHitHandler extends ICharmHandler<EntityDamageByEntityEvent> {
	public CharmCommandHitHandler() {
		super(EntityDamageByEntityEvent.class);
	}

	@Override
	public HandlerType getHandlerType() {
		return HandlerType.COMMAND_HIT;
	}

	@Override
	public void handle(EntityDamageByEntityEvent event, Player player, ItemStack item, CharmOptions options) {
		Entity entity = event.getEntity();

		if (entity instanceof Player hitPlayer) {
			HashMap<HandlerType, IEffectSettings> effects = options.getEffects();

			IEffectSettings settings = effects.get(HandlerType.COMMAND_HIT);

			if (settings instanceof CommandEffectSettings cs) {
				TagResolver resolver = TagResolver.resolver(
						Placeholder.component("displayname", player.displayName()),
						Placeholder.component("hit", hitPlayer.displayName())
				);

				List<String> commands = cs.getCommands();

				for (String s : commands) {
					Component deserialized = MiniMessage.miniMessage().deserialize(s, resolver);

					String serialized = PlainTextComponentSerializer.plainText().serialize(deserialized);

					player.getServer().dispatchCommand(player.getServer().getConsoleSender(), serialized);
				}
			}
		}
	}
}
