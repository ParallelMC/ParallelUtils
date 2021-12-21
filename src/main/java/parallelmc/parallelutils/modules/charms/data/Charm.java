package parallelmc.parallelutils.modules.charms.data;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.util.BukkitTools;

import java.util.UUID;
import java.util.logging.Level;

public class Charm {

	// Each charm is unique, mostly to track things for counters
	// but can also be used for player-specific text
	// This is also needed to bind a charm to an item. UUID will be in NBT
	private UUID charmId;

	// Has this charm been applied to an item?
	private boolean applied;

	private CharmOptions options;

	public Charm(CharmOptions options) {
		charmId = UUID.randomUUID();

		this.options = options;
		applied = false;
	}

	public Charm(CharmOptions options, boolean applied) {
		this(options);

		this.applied = applied;
	}

	public Charm(CharmOptions options, boolean applied, UUID uuid) {
		this(options, applied);

		this.charmId = uuid;
	}

	public boolean apply(ItemStack item) {
		try {
			if (applied) {
				return false;
			}

			// Check allowed materials
			if (!options.isMaterialAllowed(item.getType())) {
				return false;
			}

			// Setup
			Plugin plugin = BukkitTools.getPlugin();

			if (plugin == null) {
				Parallelutils.log(Level.WARNING, "Plugin is null! Cannot apply charm");
				return false;
			}

			// If meta is null, just return false
			ItemMeta meta = item.getItemMeta();

			if (meta == null) {
				return false;
			}

			PersistentDataContainer pdc = meta.getPersistentDataContainer();

			// If model has custom model data, save it to another tag
			if (meta.hasCustomModelData()) {
				int prevModelData = meta.getCustomModelData();

				pdc.set(new NamespacedKey(plugin, "ParallelCharm.PrevModelData"), PersistentDataType.INTEGER, prevModelData);
			}

			// Save the charm's UUID to the item. This is used for identification of charm types and such
			pdc.set(new NamespacedKey(plugin, "ParallelCharm.CharmUUID"), PersistentDataType.STRING, charmId.toString());

			item.setItemMeta(meta);

			// Apply charm options
			ItemStack appliedItem = options.applyCharm(item);

			if (appliedItem == null) {
				return false;
			}

			// TODO: Do we want runnables for each charm or overall runnables?
			// Start runnables as needed

			applied = true;
			return true;
		} catch (Exception e) {
			Parallelutils.log(Level.SEVERE, "Unable to apply charm!");
			e.printStackTrace();
			return false;
		}
	}

	public boolean takeOff(ItemStack item) {
		try {
			if (!applied) {
				return false;
			}

			// Setup
			Plugin plugin = BukkitTools.getPlugin();

			if (plugin == null) {
				Parallelutils.log(Level.WARNING, "Plugin is null! Cannot apply charm");
				return false;
			}

			// If meta is null, just return false
			ItemMeta meta = item.getItemMeta();

			if (meta == null) {
				return false;
			}

			PersistentDataContainer pdc = meta.getPersistentDataContainer();

			Integer prevModelData = pdc.get(new NamespacedKey(plugin, "ParallelCharm.PrevModelData"), PersistentDataType.INTEGER);

			meta.setCustomModelData(prevModelData); // Null to clear

			pdc.remove(new NamespacedKey(plugin, "ParallelCharm.PrevModelData"));

			pdc.remove(new NamespacedKey(plugin, "ParallelCharm.CharmUUID"));

			pdc.remove(new NamespacedKey(plugin, "ParallelCharm"));

			// Cancel runnables if needed

			item.setItemMeta(meta);

			applied = false;
			return true;
		} catch (Exception e) {
			Parallelutils.log(Level.SEVERE, "Unable to take off charm!");
			e.printStackTrace();
			return false;
		}
	}
}
