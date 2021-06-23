package parallelmc.parallelutils.modules.custommobs.bukkitmobs;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftZombie;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.Parallelutils;

import java.util.List;
import java.util.logging.Level;

import static org.bukkit.attribute.Attribute.*;

/**
 * A wrapper class to handle setting up NBT for Wisps and to handle death events.
 */
public class CraftWisp {

	/**
	 * Setup the NBT data for a custom Wisp, represented with a Zombie entity
	 * @param entity The entity to setup the NBT for
	 */
	public static void setupNBT(CraftZombie entity) {
		// Attributes
		// Health = 20
		entity.getAttribute(GENERIC_MAX_HEALTH).setBaseValue(20.0);
		entity.setHealth(entity.getAttribute(GENERIC_MAX_HEALTH).getBaseValue());

		// Damage = 8
		entity.getAttribute(GENERIC_ATTACK_DAMAGE).setBaseValue(8.0);

		// Follow range = 16
		entity.getAttribute(GENERIC_FOLLOW_RANGE).setBaseValue(16.0);

		// Movement speed = 0.25
		entity.getAttribute(GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);

		// No zombie reinforcements
		entity.getAttribute(ZOMBIE_SPAWN_REINFORCEMENTS).setBaseValue(0);

		// Other stuff

		// Invisible
		entity.setInvisible(true);

		// Can't pickup items
		entity.setCanPickupItems(false);

		// No random equipment
		entity.getEquipment().clear();

		// Don't burn in sunlight
		entity.setShouldBurnInDay(false);

		// Silent
		entity.setSilent(true);
	}

	private static ItemStack shard;

	/**
	 * Handle the EntityDeathEvent for a Wisp.
	 * Drop a Soul Shard
	 * @param event The EntityDeathEvent to handle
	 */
	public static void deathLoot(EntityDeathEvent event) {
		// The shard only needs to be made once
		if (shard == null) {
			shard = new ItemStack(Material.PRISMARINE_SHARD, 1);

			try {
				ItemMeta shardMeta = shard.getItemMeta();
				TextComponent name = Component.text("Soul Shard", NamedTextColor.WHITE);
				shardMeta.displayName(name);
				shardMeta.setCustomModelData(1000001); // Show custom texture
				shardMeta.addEnchant(Enchantment.DURABILITY, 1, true); // Make it look enchanted
				shardMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

				PluginManager manager = Bukkit.getPluginManager();
				JavaPlugin plugin = (JavaPlugin) manager.getPlugin(Constants.PLUGIN_NAME);
				if (plugin == null) {
					Parallelutils.log(Level.SEVERE, "PLUGIN NOT FOUND. THIS IS A PROBLEM");
					return;
				}

				// A special tag to keep track of custom items
				NamespacedKey key = new NamespacedKey(plugin, "CustomItem");
				shardMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
				shard.setItemMeta(shardMeta);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}

		List<ItemStack> drops = event.getDrops();
		drops.clear();
		drops.add(shard);
		event.setDroppedExp(0);
	}
}
