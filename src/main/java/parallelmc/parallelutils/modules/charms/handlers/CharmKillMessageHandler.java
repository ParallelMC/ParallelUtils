package parallelmc.parallelutils.modules.charms.handlers;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.charms.data.CharmOptions;
import parallelmc.parallelutils.modules.charms.data.IEffectSettings;
import parallelmc.parallelutils.modules.charms.helper.EncapsulatedType;
import parallelmc.parallelutils.modules.charms.helper.Types;

import java.util.HashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

		Function<String, ComponentLike> resolver = (placeholder) -> {
			switch (placeholder.toLowerCase()) {
				case "killer" -> {
					return player.displayName();
				}
				case "dead" -> {
					return event.getPlayer().displayName();
				}
				default -> {
					return null;
				}
			}
		};

		String miniMsg = (String) message.getVal();

		Component cmp = MiniMessage.builder().placeholderResolver(resolver).build().parse(miniMsg);

		event.deathMessage(cmp);
	}


}
