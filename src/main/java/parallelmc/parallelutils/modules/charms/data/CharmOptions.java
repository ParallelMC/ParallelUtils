package parallelmc.parallelutils.modules.charms.data;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.helper.EncapsulatedType;
import parallelmc.parallelutils.modules.charms.helper.Types;
import parallelmc.parallelutils.util.BukkitTools;

import javax.annotation.Nullable;
import java.util.ArrayList;
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

	// If empty, allowed on any player
	private final String[] allowedPlayers;

	// If empty, allowed on any permission
	private final String[] allowedPermissions;

	// Settings
	// Name color scheme? Mini-message?
	// Custom lore?
	// Death message?
	// Kill counter?
	// Particles?
	private final HashMap<HandlerType, IEffectSettings> effects;

	private final Integer customModelData;

	public CharmOptions(UUID uuid, Material[] allowedMaterials, String[] allowedPlayers, String[] allowedPermissions,
	                    HashMap<HandlerType, IEffectSettings> effects, Integer customModelData) {
		this.optionsUuid = uuid;
		this.allowedMaterials = allowedMaterials;
		this.allowedPlayers = allowedPlayers;
		this.allowedPermissions = allowedPermissions;
		this.effects = effects;
		this.customModelData = customModelData;
	}

	public ItemStack applyCharm(ItemStack item) {
		return applyCharm(item, null);
	}

	public ItemStack applyCharm(ItemStack item, Player player) {
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

		// TODO: Figure out how to do name formatting
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

		// Apply allowed players
		if (allowedPlayers != null) {
			PersistentDataContainer[] playersArr = new PersistentDataContainer[allowedPlayers.length];

			NamespacedKey playerKey = new NamespacedKey(plugin, "ParallelCharm.AllowedPlayers.Player");
			int index = 0;
			for (String p : allowedPlayers) {
				PersistentDataContainer playersContainer = charmsContainer.getAdapterContext().newPersistentDataContainer();
				playersContainer.set(playerKey, PersistentDataType.STRING, p);
				playersArr[index] = playersContainer;

				index++;
			}

			charmsContainer.set(new NamespacedKey(plugin, "ParallelCharm.AllowedPlayers"),
					PersistentDataType.TAG_CONTAINER_ARRAY, playersArr);
		}

		// Apply allowed permissions
		if (allowedPermissions != null) {
			PersistentDataContainer[] permissionsArr = new PersistentDataContainer[allowedPermissions.length];

			NamespacedKey permissionsKey = new NamespacedKey(plugin, "ParallelCharm.AllowedPermissions.Permission");
			int index = 0;
			for (String p : allowedPermissions) {
				PersistentDataContainer permissionsContainer = charmsContainer.getAdapterContext().newPersistentDataContainer();
				permissionsContainer.set(permissionsKey, PersistentDataType.STRING, p);
				permissionsArr[index] = permissionsContainer;

				index++;
			}

			charmsContainer.set(new NamespacedKey(plugin, "ParallelCharm.AllowedPermissions"),
					PersistentDataType.TAG_CONTAINER_ARRAY, permissionsArr);
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
				NamespacedKey typeKey = new NamespacedKey(plugin, "ParallelCharm.Effects.settings.type");

				setting.set(typeKey, PersistentDataType.STRING, encapsulatedType.getType().name());
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

	public boolean isPlayerAllowed(Player player) {
		if (allowedPlayers == null || allowedPlayers.length == 0) {
			if (allowedPermissions == null || allowedPermissions.length == 0) return true;
			// If player has any permission in array, return true
			if (player == null) return false;
			return Arrays.stream(allowedPermissions).anyMatch(player::hasPermission);
		}

		if (player == null) return false;
		boolean players = Arrays.asList(allowedPlayers).contains(player.getUniqueId().toString());
		if (players) {
			if (allowedPermissions == null || allowedPermissions.length == 0) return true;
			// If player has any permission in array, return true
			return Arrays.stream(allowedPermissions).anyMatch(player::hasPermission);
		}
		return false;
	}


	public HashMap<HandlerType, IEffectSettings> getEffects() {
		return effects;
	}

	/**
	 * Returns a partial CharmOptions or null if the player is not allowed to use this charm
	 * @param item The item to parse
	 * @param player The player to check permissions against
	 * @return The CharmOptions or null
	 */
	@Nullable
	public static CharmOptions parseOptions(ItemStack item, Player player) {
		CharmOptions options = parseOptions(item);

		if (options == null) return null;

		if (options.isPlayerAllowed(player)) {
			return options;
		}
		return null;
	}

	/**
	 * Returns a partial CharmOptions (because I'm lazy)
	 * @param item The item to parse
	 * @return The partial CharmOptions or null
	 */
	@Nullable
	public static CharmOptions parseOptions(ItemStack item) {
		// Setup
		Plugin plugin = BukkitTools.getPlugin();

		if (plugin == null) {
			return null;
		}

		if (item == null) {
			return null;
		}

		// If meta is null, just return null
		ItemMeta meta = item.getItemMeta();

		if (meta == null) {
			return null;
		}

		PersistentDataContainer pdc = meta.getPersistentDataContainer();

		NamespacedKey key = new NamespacedKey(plugin, "ParallelCharm");

		PersistentDataContainer charmsContainer = pdc.get(key, PersistentDataType.TAG_CONTAINER);

		if (charmsContainer == null) {
			Parallelutils.log(Level.WARNING, "Charms Container is null!");
			return null;
		}

		String uuidStr = charmsContainer.get(new NamespacedKey(plugin, "ParallelCharm.OptUUID"),
				PersistentDataType.STRING);
		if (uuidStr == null) {
			return null;
		}
		UUID uuid = UUID.fromString(uuidStr);

		// Parse allowed players and permissions
		ArrayList<String> allowedPlayersList = new ArrayList<>();
		ArrayList<String> allowedPermissionsList = new ArrayList<>();

		PersistentDataContainer[] playersContainer = charmsContainer.get(
				new NamespacedKey(plugin, "ParallelCharm.AllowedPlayers"), PersistentDataType.TAG_CONTAINER_ARRAY);
		if (playersContainer != null) {
			// Actually parse players
			NamespacedKey playerKey = new NamespacedKey(plugin, "ParallelCharm.AllowedPlayers.Player");
			for (PersistentDataContainer p : playersContainer) {
				String player = p.get(playerKey, PersistentDataType.STRING);
				if (player != null) {
					allowedPlayersList.add(player);
				}
			}
		}

		PersistentDataContainer[] permissionsContainer = charmsContainer.get(
				new NamespacedKey(plugin, "ParallelCharm.AllowedPermissions"), PersistentDataType.TAG_CONTAINER_ARRAY);
		if (permissionsContainer != null) {
			// Actually parse players
			NamespacedKey permissionsKey = new NamespacedKey(plugin, "ParallelCharm.AllowedPermissions.Permission");
			for (PersistentDataContainer p : permissionsContainer) {
				String permission = p.get(permissionsKey, PersistentDataType.STRING);
				if (permission != null) {
					allowedPermissionsList.add(permission);
				}
			}
		}


		PersistentDataContainer[] effectsContainer = charmsContainer.get(new NamespacedKey(plugin, "ParallelCharm.Effects"),
				PersistentDataType.TAG_CONTAINER_ARRAY);

		if (effectsContainer == null) {
			return null;
		}

		HashMap<HandlerType, IEffectSettings> effects = new HashMap<>();

		for (PersistentDataContainer effect : effectsContainer) {
			String handlerName = effect.get(new NamespacedKey(plugin, "ParallelCharm.Effects.handler"),
					PersistentDataType.STRING);
			if (handlerName == null) {
				continue;
			}

			HashMap<String, EncapsulatedType> settings = new HashMap<>();

			PersistentDataContainer[] settingsArr = effect.get(new NamespacedKey(plugin, "ParallelCharm.Effects.settings"),
					PersistentDataType.TAG_CONTAINER_ARRAY);

			if (settingsArr == null) {
				continue;
			}

			for (PersistentDataContainer s : settingsArr) {
				String sName = s.get(new NamespacedKey(plugin, "ParallelCharm.Effects.settings.name"),
						PersistentDataType.STRING);
				if (sName == null) {
					continue;
				}

				String typeStr = s.get(new NamespacedKey(plugin, "ParallelCharm.Effects.settings.type"),
						PersistentDataType.STRING);
				if (typeStr == null) {
					continue;
				}

				Types type = Types.valueOf(typeStr);
				EncapsulatedType eType = null;

				NamespacedKey valKey = new NamespacedKey(plugin, "ParallelCharm.Effects.settings.value");

				switch (type) {
					case BYTE -> eType = new EncapsulatedType(type, s.get(valKey, PersistentDataType.BYTE));
					case INT -> eType = new EncapsulatedType(type, s.get(valKey, PersistentDataType.INTEGER));
					case DOUBLE -> eType = new EncapsulatedType(type, s.get(valKey, PersistentDataType.DOUBLE));
					case LONG -> eType = new EncapsulatedType(type, s.get(valKey, PersistentDataType.LONG));
					case STRING -> eType = new EncapsulatedType(type, s.get(valKey, PersistentDataType.STRING));
					default -> {
						Parallelutils.log(Level.WARNING, "Invalid data type!");
						continue;
					}
				}

				settings.put(sName, eType);
			}

			effects.put(HandlerType.valueOf(handlerName), new GenericEffectSettings(settings));
		}

		return new CharmOptions(uuid, null, allowedPlayersList.toArray(new String[0]),
				allowedPermissionsList.toArray(new String[0]), effects, null);
	}
}