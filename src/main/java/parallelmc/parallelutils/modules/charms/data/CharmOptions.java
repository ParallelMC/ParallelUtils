package parallelmc.parallelutils.modules.charms.data;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.util.BukkitTools;

import javax.naming.Name;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class CharmOptions {

	// This is just used to store in the database
	private UUID optionsUuid;

	// If empty, allowed on everything
	private Material[] allowedMaterials;

	// Settings
	// Name color scheme? Mini-message?
	// Custom lore?
	// Death message?
	// Kill counter?
	// Particles?
	private HashMap<HandlerType, IEffectSettings> effects;

	private Integer customModelData;
	private Integer prevCustomModelData;

	public ItemStack applyCharm(ItemStack item) {
		// Setup
		Plugin plugin = BukkitTools.getPlugin();

		if (plugin == null) {
			Parallelutils.log(Level.WARNING, "Plugin is null! Cannot apply charm");
			return null;
		}

		NamespacedKey key = new NamespacedKey(plugin, "ParallelCharm");

		// If meta is null, just return the original item
		ItemMeta meta = item.getItemMeta();

		if (meta == null) {
			return item;
		}

		// Set display name. Currently not in use
		// meta.displayName(Component.text("a"));

		// Store previous custom model data and set new custom model data
		if (meta.hasCustomModelData()) {
			prevCustomModelData = meta.getCustomModelData();
		}
		meta.setCustomModelData(customModelData);

		PersistentDataContainer pdc = meta.getPersistentDataContainer();

		// Create a sub container
		PersistentDataContainer charmsContainer = pdc.getAdapterContext().newPersistentDataContainer();

		charmsContainer.set(new NamespacedKey(plugin, "ParallelCharm.PrevModelData"),
				PersistentDataType.INTEGER, prevCustomModelData);

		charmsContainer.set(new NamespacedKey(plugin, "ParallelCharm.UUID"),
				PersistentDataType.STRING, optionsUuid.toString());


		PersistentDataContainer[] matsArr = new PersistentDataContainer[allowedMaterials.length];

		NamespacedKey matKey = new NamespacedKey(plugin, "ParallelCharm.AllowedMats.Mat");
		int index = 0;
		for (Material m : allowedMaterials) {
			PersistentDataContainer matContainer = pdc.getAdapterContext().newPersistentDataContainer();
			matContainer.set(matKey, PersistentDataType.STRING, m.getKey().asString());
			matsArr[index] = matContainer;

			index++;
		}

		charmsContainer.set(new NamespacedKey(plugin, "ParallelCharm.AllowedMats"),
				PersistentDataType.TAG_CONTAINER_ARRAY, matsArr);


		// TODO: Add effects storage

		pdc.set(key, PersistentDataType.TAG_CONTAINER, charmsContainer);

		item.setItemMeta(meta);

		return item;
	}
}
