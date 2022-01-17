package parallelmc.parallelutils.modules.charms.handlers.impl;

import io.papermc.paper.text.PaperComponents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.placeholder.PlaceholderResolver;
import net.kyori.adventure.text.minimessage.placeholder.Replacement;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.charms.data.CharmOptions;
import parallelmc.parallelutils.modules.charms.data.IEffectSettings;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.handlers.ICharmApplyHandler;
import parallelmc.parallelutils.modules.charms.helper.EncapsulatedType;
import parallelmc.parallelutils.modules.charms.helper.Types;

import java.util.HashMap;
import java.util.function.Function;
import java.util.logging.Level;

public class CharmStyleNameHandler extends ICharmApplyHandler {

	@Override
	public void apply(Player player, @NotNull ItemStack item, @NotNull CharmOptions options) {

		ItemMeta meta = item.getItemMeta();

		if (meta == null) {
			return;
		}

		HashMap<HandlerType, IEffectSettings> effects = options.getEffects();

		IEffectSettings settings = effects.get(HandlerType.STYLE_NAME);

		if (settings == null) {
			return;
		}

		HashMap<String, EncapsulatedType> settingMap = settings.getSettings();

		EncapsulatedType type = settingMap.get("style");

		if (type == null || type.getType() != Types.STRING) {
			return;
		}

		String val = (String) type.getVal();

		Component currName = meta.displayName();

		if (currName == null) {
			String dispName = PaperComponents.plainTextSerializer().serialize(Component.translatable(item.translationKey()));;
			currName = Component.text(dispName);
		}

		Component finalCurrName = currName;
		Function<String, Replacement<?>> resolver = (placeholder) -> {
			switch (placeholder.toLowerCase()) {
				case "name" -> {
					return Replacement.component(finalCurrName);
				}
				default -> {
					return null;
				}
			}
		};

		Component result = MiniMessage.builder().placeholderResolver(PlaceholderResolver.dynamic(resolver)).build().deserialize(val);

		meta.displayName(result);

		meta.displayName();

		item.setItemMeta(meta);
	}

	@Override
	public void remove(Player player, ItemStack item, CharmOptions options) {
		ItemMeta meta = item.getItemMeta();

		if (meta == null) {
			return;
		}

		meta.displayName(null);

		item.setItemMeta(meta);
	}

	@Override
	public HandlerType getHandlerType() {
		return HandlerType.STYLE_NAME;
	}
}
