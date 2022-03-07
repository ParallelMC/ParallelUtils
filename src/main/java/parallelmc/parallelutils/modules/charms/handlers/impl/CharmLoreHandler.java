package parallelmc.parallelutils.modules.charms.handlers.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.charms.data.CharmOptions;
import parallelmc.parallelutils.modules.charms.data.IEffectSettings;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.handlers.ICharmApplyHandler;
import parallelmc.parallelutils.modules.charms.helper.EncapsulatedType;
import parallelmc.parallelutils.modules.charms.helper.Types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CharmLoreHandler extends ICharmApplyHandler {

	@Override
	public void apply(Player player, @NotNull ItemStack item, @NotNull CharmOptions options) {
		ItemMeta meta = item.getItemMeta();

		if (meta == null) return;

		HashMap<HandlerType, IEffectSettings> effects = options.getEffects();

		IEffectSettings settings = effects.get(HandlerType.LORE);

		if (settings == null) {
			return;
		}

		HashMap<String, EncapsulatedType> settingMap = settings.getSettings();

		EncapsulatedType loreSetting = settingMap.get("lore");

		if (loreSetting.getType() != Types.STRING) return;

		String loreTotal = (String) loreSetting.getVal();

		String[] parts = loreTotal.split("\n");

		List<Component> lore = new ArrayList<>();

		Component displayName;

		if (player == null) {
			displayName = Component.text("displayname");
		} else {
			displayName = player.displayName();
		}

		TagResolver resolver = TagResolver.resolver(
				Placeholder.component("displayname", displayName)
		);

		for (String s : parts) {
			lore.add(MiniMessage.miniMessage().deserialize(s, resolver));
		}

		meta.lore(lore);

		item.setItemMeta(meta);
	}

	@Override
	public void remove(Player player, ItemStack item, CharmOptions options) {
		ItemMeta meta = item.getItemMeta();

		if (meta == null) return;

		meta.lore(new ArrayList<>());

		item.setItemMeta(meta);
	}

	@Override
	public HandlerType getHandlerType() {
		return HandlerType.LORE;
	}
}
