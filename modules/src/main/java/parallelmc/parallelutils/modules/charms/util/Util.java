package parallelmc.parallelutils.modules.charms.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import parallelmc.parallelutils.modules.charms.ParallelCharms;
import parallelmc.parallelutils.modules.charms.data.Charm;

import java.util.UUID;

public class Util {

	public static boolean isArmor(ItemStack item) {
		Material type = item.getType();

		return (type == Material.LEATHER_HELMET || type == Material.LEATHER_CHESTPLATE || type == Material.LEATHER_LEGGINGS || type == Material.LEATHER_BOOTS ||
				type == Material.IRON_HELMET || type == Material.IRON_CHESTPLATE || type == Material.IRON_LEGGINGS || type == Material.IRON_BOOTS ||
				type == Material.GOLDEN_HELMET || type == Material.GOLDEN_CHESTPLATE || type == Material.GOLDEN_LEGGINGS || type == Material.GOLDEN_BOOTS ||
				type == Material.DIAMOND_HELMET || type == Material.DIAMOND_CHESTPLATE || type == Material.DIAMOND_LEGGINGS || type == Material.DIAMOND_BOOTS ||
				type == Material.NETHERITE_HELMET || type == Material.NETHERITE_CHESTPLATE || type == Material.NETHERITE_LEGGINGS || type == Material.NETHERITE_BOOTS ||
				type == Material.CHAINMAIL_HELMET || type == Material.CHAINMAIL_CHESTPLATE || type == Material.CHAINMAIL_LEGGINGS || type == Material.CHAINMAIL_BOOTS ||
				type == Material.TURTLE_HELMET || type == Material.ELYTRA);
	}

	public static boolean canRun(ParallelCharms pCharms, Player player, ItemStack item, UUID uuid) {
		boolean isArmor = Util.isArmor(item);

		PlayerInventory inventory = player.getInventory();

		boolean ok = false;

		if (!isArmor) {
			ItemStack mainHand = inventory.getItemInMainHand();

			Charm mainCharm = Charm.parseCharm(pCharms, mainHand, player);
			
			if (mainCharm != null && mainCharm.getUUID().equals(uuid)) {
				ok = true;
			} else {
				ItemStack offHand = inventory.getItemInOffHand();

				Charm offCharm = Charm.parseCharm(pCharms, offHand, player);

				if (offCharm != null && offCharm.getUUID().equals(uuid)) {
					ok = true;
				}
			}
		} else {
			ItemStack helmet = inventory.getHelmet();
			Charm helmCharm = Charm.parseCharm(pCharms, helmet, player);

			if (helmCharm != null && helmCharm.getUUID().equals(uuid)) {
				ok = true;
			} else {
				ItemStack chestplate = inventory.getChestplate();
				Charm chestCharm = Charm.parseCharm(pCharms, chestplate, player);

				if (chestCharm != null && chestCharm.getUUID().equals(uuid)) {
					ok = true;
				} else {
					ItemStack leggings = inventory.getLeggings();
					Charm legCharm = Charm.parseCharm(pCharms, leggings, player);

					if (legCharm != null && legCharm.getUUID().equals(uuid)) {
						ok = true;
					} else {
						ItemStack boots = inventory.getBoots();
						Charm bootsCharm = Charm.parseCharm(pCharms, boots, player);

						if (bootsCharm != null && bootsCharm.getUUID().equals(uuid)) {
							ok = true;
						}
					}
				}
			}
		}

		return ok;
	}
}
