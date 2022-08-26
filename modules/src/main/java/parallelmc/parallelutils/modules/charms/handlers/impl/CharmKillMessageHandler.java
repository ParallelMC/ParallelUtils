package parallelmc.parallelutils.modules.charms.handlers.impl;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.charms.data.CharmOptions;
import parallelmc.parallelutils.modules.charms.data.IEffectSettings;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.handlers.ICharmHandler;
import parallelmc.parallelutils.modules.charms.helper.EncapsulatedType;
import parallelmc.parallelutils.modules.charms.helper.Types;

import java.util.HashMap;

public class CharmKillMessageHandler extends ICharmHandler<PlayerDeathEvent> {

	public CharmKillMessageHandler() {
		super(PlayerDeathEvent.class);
	}

	@Override
	public HandlerType getHandlerType() {
		return HandlerType.MESSAGE_KILL;
	}

	@Override
	public void handle(PlayerDeathEvent event, Player player, @NotNull ItemStack item, @NotNull CharmOptions options) { // Need event as well

		HashMap<HandlerType, IEffectSettings> effects = options.getEffects();

		IEffectSettings killMessageSettings = effects.get(HandlerType.MESSAGE_KILL);

		if (killMessageSettings == null) {
			return;
		}

		HashMap<String, EncapsulatedType> settings = killMessageSettings.getSettings();

		EncapsulatedType message = settings.get("message");

		if (message == null) {
			return;
		}

		if (message.getType() != Types.STRING) {
			return;
		}

		TagResolver placeholders = TagResolver.resolver(
				Placeholder.component("killer", player.displayName()),
				Placeholder.component("dead", event.getPlayer().displayName())
		);

		String miniMsg = (String) message.getVal();

		String replaced = PlaceholderAPI.setPlaceholders(player, miniMsg);

		Component cmp = MiniMessage.builder().build().deserialize(replaced, placeholders);

		event.deathMessage(cmp);
	}


}
