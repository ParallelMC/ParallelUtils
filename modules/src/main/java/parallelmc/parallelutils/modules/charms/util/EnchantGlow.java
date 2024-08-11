package parallelmc.parallelutils.modules.charms.util;

import io.papermc.paper.enchantments.EnchantmentRarity;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.ParallelUtils;

import java.util.Set;

public class EnchantGlow extends Enchantment {

	private final NamespacedKey key;

	public EnchantGlow(@NotNull NamespacedKey key) {
		this.key = key;
	}

	@Override
	public @NotNull String getName() {
		return "";
	}

	@Override
	public int getMaxLevel() {
		return 0;
	}

	@Override
	public int getStartLevel() {
		return 0;
	}

	@Override
	public @NotNull EnchantmentTarget getItemTarget() {
		return null;
	}

	@Override
	public boolean isTreasure() {
		return false;
	}

	@Override
	public boolean isCursed() {
		return false;
	}

	@Override
	public boolean conflictsWith(@NotNull Enchantment other) {
		return false;
	}

	@Override
	public boolean canEnchantItem(@NotNull ItemStack item) {
		return false;
	}

	@Override
	public @NotNull Component displayName(int level) {
		return Component.empty();
	}

	@Override
	public boolean isTradeable() {
		return false;
	}

	@Override
	public boolean isDiscoverable() {
		return false;
	}

	@Override
	public int getMinModifiedCost(int level) {
		return 0;
	}

	@Override
	public int getMaxModifiedCost(int level) {
		return 0;
	}

	@Override
	public int getAnvilCost() {
		return 0;
	}

	@Override
	public @NotNull EnchantmentRarity getRarity() {
		return null;
	}

	@Override
	public float getDamageIncrease(int i, @NotNull EntityCategory entityCategory) {
		return 0;
	}

	@Override
	public float getDamageIncrease(int i, @NotNull EntityType entityType) {
		return 0;
	}

	@Override
	public @NotNull Set<EquipmentSlot> getActiveSlots() {
		return Set.of();
	}

	@Override
	public @NotNull Set<EquipmentSlotGroup> getActiveSlotGroups() {
		return Set.of();
	}

	@Override
	public @NotNull String translationKey() {
		return "";
	}

	public static EnchantGlow instance = null;

	public static void registerFakeGlow(ParallelUtils puPlugin) {
//		try {
//			Field f = Enchantment.class.getDeclaredField("acceptingNew");
//			f.setAccessible(true);
//			f.set(null, true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		try {
			instance = new EnchantGlow(new NamespacedKey(puPlugin, "glow"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public @NotNull NamespacedKey getKey() {
		return key;
	}

	@Override
	public @NotNull Key key() {
		return super.key();
	}

	@Override
	public @NotNull String getTranslationKey() {
		return null;
	}
}
