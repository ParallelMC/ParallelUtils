package parallelmc.parallelutils.modules.charms.handlers.impl;

import org.bukkit.enchantments.Enchantment;
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
import parallelmc.parallelutils.modules.charms.util.EnchantGlow;

import java.util.HashMap;

public class CharmShineHandler extends ICharmApplyHandler {

	@Override
	public void apply(Player player, @NotNull ItemStack item, @NotNull CharmOptions options) {
		ItemMeta meta = item.getItemMeta();

		if (meta == null) {
			return;
		}

		HashMap<HandlerType, IEffectSettings> effects = options.getEffects();

		IEffectSettings settings = effects.get(HandlerType.SHINE);

		if (settings == null) {
			return;
		}

		HashMap<String, EncapsulatedType> settingMap = settings.getSettings();

		EncapsulatedType type = settingMap.get("shine");

		if (type.getType() != Types.INT) return;

		int val = (Integer) type.getVal();

		if (val == 1) {
			meta.addEnchant(EnchantGlow.instance, 1, true);
			item.setItemMeta(meta);
		}
	}

	@Override
	public void remove(Player player, ItemStack item, CharmOptions options) {
		item.removeEnchantment(EnchantGlow.instance);
	}

	@Override
	public HandlerType getHandlerType() {
		return HandlerType.SHINE;
	}
}
