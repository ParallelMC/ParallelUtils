package parallelmc.parallelutils.modules.charms.data;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.helper.EncapsulatedType;
import parallelmc.parallelutils.util.BukkitTools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

/**
 * This class is used to store charm types and apply them to items
 */
public class CharmOptions {

	// This is just used to store in the database. Specifies _type_ of charm, not the specific charm
	private final UUID optionsUuid;

	// If empty, allowed on everything
	private final Material[] allowedMaterials;

	// Settings
	// Name color scheme? Mini-message?
	// Custom lore?
	// Death message?
	// Kill counter?
	// Particles?
	private final HashMap<HandlerType, IEffectSettings> effects;

	private final Integer customModelData;

	public CharmOptions(UUID uuid, Material[] allowedMaterials, HashMap<HandlerType, IEffectSettings> effects, Integer customModelData) {
		this.optionsUuid = uuid;
		this.allowedMaterials = allowedMaterials;
		this.effects = effects;
		this.customModelData = customModelData;
	}

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

		if (customModelData != null) {
			meta.setCustomModelData(customModelData);
		}

		PersistentDataContainer pdc = meta.getPersistentDataContainer();

		// Create a sub container
		PersistentDataContainer charmsContainer = pdc.getAdapterContext().newPersistentDataContainer();


		// Apply charm options UUID
		charmsContainer.set(new NamespacedKey(plugin, "ParallelCharm.OptUUID"),
				PersistentDataType.STRING, optionsUuid.toString());

		// Apply allowed materials. This doesn't _really_ need to be put on the item, but meh
		if (allowedMaterials != null) {
			PersistentDataContainer[] matsArr = new PersistentDataContainer[allowedMaterials.length];

			NamespacedKey matKey = new NamespacedKey(plugin, "ParallelCharm.AllowedMats.Mat");
			int index = 0;
			for (Material m : allowedMaterials) {
				PersistentDataContainer matContainer = charmsContainer.getAdapterContext().newPersistentDataContainer();
				matContainer.set(matKey, PersistentDataType.STRING, m.getKey().asString());
				matsArr[index] = matContainer;

				index++;
			}

			charmsContainer.set(new NamespacedKey(plugin, "ParallelCharm.AllowedMats"),
					PersistentDataType.TAG_CONTAINER_ARRAY, matsArr);
		}

		// Apply effects. Sorry for anyone who has to read this code...
		int effectsIndex = 0;
		PersistentDataContainer[] effectsArr = new PersistentDataContainer[effects.size()];

		// TODO: Make this less of a steaming pile of spaghetti
		NamespacedKey effectsKey = new NamespacedKey(plugin, "ParallelCharm.Effects");
		for (HandlerType h : effects.keySet()) {
			PersistentDataContainer effectContainer = charmsContainer.getAdapterContext().newPersistentDataContainer();
			String handlerName = h.name();

			// Hopefully these keys are unique locally and not globally
			effectContainer.set(new NamespacedKey(plugin, "ParallelCharm.Effects.handler"),
					PersistentDataType.STRING, handlerName);

			HashMap<String, EncapsulatedType> settings = effects.get(h).getSettings();

			int settingsIndex = 0;
			PersistentDataContainer[] settingsArr = new PersistentDataContainer[settings.size()];

			for (String sName : settings.keySet()) {
				PersistentDataContainer setting = effectContainer.getAdapterContext().newPersistentDataContainer();

				setting.set(new NamespacedKey(plugin, "ParallelCharm.Effects.settings.name"),
						PersistentDataType.STRING, sName);

				EncapsulatedType encapsulatedType = settings.get(sName);

				NamespacedKey valKey = new NamespacedKey(plugin, "ParallelCharm.Effects.settings.value");
				switch (encapsulatedType.getType()) {
					case BYTE -> setting.set(valKey, PersistentDataType.BYTE, (Byte) encapsulatedType.getVal());
					case INT -> setting.set(valKey, PersistentDataType.INTEGER, (Integer) encapsulatedType.getVal());
					case DOUBLE -> setting.set(valKey, PersistentDataType.DOUBLE, (Double) encapsulatedType.getVal());
					case LONG -> setting.set(valKey, PersistentDataType.LONG, (Long) encapsulatedType.getVal());
					case STRING -> setting.set(valKey, PersistentDataType.STRING, (String) encapsulatedType.getVal());
					default -> {
						Parallelutils.log(Level.WARNING, "Invalid data type! Defaulting to string");
						setting.set(valKey, PersistentDataType.STRING, (String) encapsulatedType.getVal());
					}
				}
				settingsArr[settingsIndex++] = setting;
			}

			effectContainer.set(new NamespacedKey(plugin, "ParallelCharm.Effects.settings"),
					PersistentDataType.TAG_CONTAINER_ARRAY, settingsArr);

			effectsArr[effectsIndex++] = effectContainer;
		}

		charmsContainer.set(effectsKey, PersistentDataType.TAG_CONTAINER_ARRAY, effectsArr);

		pdc.set(key, PersistentDataType.TAG_CONTAINER, charmsContainer);

		item.setItemMeta(meta);

		return item;
	}

	public boolean isMaterialAllowed(Material mat) {
		if (allowedMaterials == null) return true;
		return Arrays.asList(allowedMaterials).contains(mat);
	}
}
