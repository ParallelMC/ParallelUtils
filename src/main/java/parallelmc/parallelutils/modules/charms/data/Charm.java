package parallelmc.parallelutils.modules.charms.data;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.charms.ParallelCharms;
import parallelmc.parallelutils.modules.charms.handlers.*;
import parallelmc.parallelutils.modules.charms.helper.EncapsulatedType;
import parallelmc.parallelutils.modules.charms.helper.Types;
import parallelmc.parallelutils.util.BukkitTools;

import javax.annotation.Nullable;
import java.util.*;
import java.util.logging.Level;

public class Charm {

	private final Parallelutils puPlugin;

	private final ParallelCharms pCharms;

	// Each charm is unique, mostly to track things for counters
	// but can also be used for player-specific text
	// This is also needed to bind a charm to an item. UUID will be in NBT
	private UUID charmId;


	private CharmOptions options;

	private final ArrayList<BukkitRunnable> runnables;

	public Charm(ParallelCharms pCharms, CharmOptions options) {
		this.pCharms = pCharms;
		this.runnables = new ArrayList<>();

		PluginManager manager = Bukkit.getPluginManager();
		Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

		if (plugin == null) {
			Parallelutils.log(Level.SEVERE, "Unable to construct charm. Plugin " + Constants.PLUGIN_NAME
					+ " does not exist!");
			puPlugin = null;
			return;
		}

		puPlugin = (Parallelutils) plugin;

		charmId = UUID.randomUUID();

		this.options = options;
	}

	public Charm(ParallelCharms pCharms, CharmOptions options, UUID uuid) {
		this(pCharms, options);

		this.charmId = uuid;
	}

	public boolean apply(ItemStack item) {
		return apply(item, null, true, true);
	}
	public boolean apply(ItemStack item, Player player, boolean startRunnables, boolean addCharms) {
		try {
			// Check allowed materials
			if (!options.isMaterialAllowed(item.getType())) {
				Parallelutils.log(Level.INFO, "Material not allowed!");
				return false;
			}
			if (!options.isPlayerAllowed(player)) {
				Parallelutils.log(Level.INFO, "Player/Permission not allowed!");
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

			List<Component> lore = meta.lore();
			if (lore == null) lore = new ArrayList<>();

			lore.add(MiniMessage.miniMessage().deserialize("<italic:false><aqua>Charm: " + options.getName()));
			meta.lore(lore);

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

			// Start runnables as needed
			HashMap<HandlerType, IEffectSettings> effects = options.getEffects();
			for (HandlerType t : effects.keySet()) {
				if (t.getCategory() == HandlerCategory.RUNNABLE) {
					if (startRunnables) {
						ICharmHandler<Event> handler = pCharms.getHandler(t, Event.class);

						if (handler instanceof ICharmRunnableHandler runnableHandler) {
							BukkitRunnable runnable = runnableHandler.getRunnable(player, item, this.options);

							if (runnable == null) continue;

							IEffectSettings settings = effects.get(t);

							HashMap<String, EncapsulatedType> settingsMap = settings.getSettings();

							EncapsulatedType delayObj = settingsMap.get("delay");
							EncapsulatedType periodObj = settingsMap.get("period");

							if (delayObj == null || delayObj.getType() != Types.LONG) continue;
							if (periodObj == null || periodObj.getType() != Types.LONG) continue;

							Long delay = (Long) delayObj.getVal();
							Long period = (Long) periodObj.getVal();

							runnable.runTaskTimer(puPlugin, delay, period);

							Parallelutils.log(Level.INFO, "Started runnable");

							runnables.add(runnable);
						} else {
							Parallelutils.log(Level.WARNING, "Non-runnable handler on Runnable effect! " + t.name());
						}
					}
				} else if (t.getCategory() == HandlerCategory.APPLY) {
					ICharmHandler<Event> handler = pCharms.getHandler(t, Event.class);

					if (handler instanceof ICharmApplyHandler applyHandler) {
						applyHandler.apply(player, item, this.options);
					} else {
						Parallelutils.log(Level.WARNING, "Non-apply handler on Apply effect! " + t.name());
					}
				}
			}

			if (addCharms) {
				pCharms.addCharm(player, this);
			}
			return true;
		} catch (Exception e) {
			Parallelutils.log(Level.SEVERE, "Unable to apply charm!");
			e.printStackTrace();
			return false;
		}
	}

	public boolean takeOff(ItemStack item, Player player) {
		try {

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

			item.setItemMeta(meta);

			// Cancel runnables if needed

			cancelRunnables();

			HashMap<HandlerType, IEffectSettings> effects = options.getEffects();
			for (HandlerType t : effects.keySet()) {
				if (t.getCategory() == HandlerCategory.APPLY) {
					ICharmHandler<Event> handler = pCharms.getHandler(t, Event.class);

					if (handler instanceof ICharmApplyHandler applyHandler) {
						applyHandler.remove(player, item, this.options);
					}
				}
			}

			pCharms.removeCharm(player, this);
			return true;
		} catch (Exception e) {
			Parallelutils.log(Level.SEVERE, "Unable to take off charm!");
			e.printStackTrace();
			return false;
		}
	}

	@Nullable
	public static Charm parseCharm(ParallelCharms pCharms, ItemStack item, Player player) {
		CharmOptions options = CharmOptions.parseOptions(item, player);

		if (options == null) {
			return null;
		}

		Plugin plugin = BukkitTools.getPlugin();

		if (plugin == null) {
			Parallelutils.log(Level.WARNING, "Plugin is null! Cannot apply charm");
			return null;
		}

		// If meta is null, just return the original item
		ItemMeta meta = item.getItemMeta();

		if (meta == null) {
			return null;
		}

		String uuidStr = meta.getPersistentDataContainer().get(
				new NamespacedKey(plugin, "ParallelCharm.CharmUUID"), PersistentDataType.STRING);

		if (uuidStr == null) {
			return null;
		}

		UUID uuid = UUID.fromString(uuidStr);

		return new Charm(pCharms, options, uuid);
	}

	@Nullable
	public static Charm parseCharm(ParallelCharms pCharms,ItemStack item) {
		return parseCharm(pCharms, item, null);
	}

	public static boolean hasCharm(ItemStack item) {
		Plugin plugin = BukkitTools.getPlugin();

		if (plugin == null) {
			Parallelutils.log(Level.WARNING, "Plugin is null! Cannot check charm");
			return false;
		}

		// If meta is null, just return the original item
		ItemMeta meta = item.getItemMeta();

		if (meta == null) {
			return false;
		}

		String uuidStr = meta.getPersistentDataContainer().get(
				new NamespacedKey(plugin, "ParallelCharm.CharmUUID"), PersistentDataType.STRING);

		if (uuidStr == null) {
			return false;
		}

		try {
			UUID uuid = UUID.fromString(uuidStr);
		} catch (IllegalArgumentException e) {
			return false;
		}

		return true;
	}

	public boolean setCharmAppl(ItemStack item) {
		ItemMeta meta = item.getItemMeta();

		if (meta == null) return false;

		meta.displayName(MiniMessage.miniMessage().deserialize("<italic:false><yellow>Charm Applicator"));

		item.setItemMeta(meta);

		options.applyCharm(item);

		HashMap<HandlerType, IEffectSettings> effects = options.getEffects();

		meta = item.getItemMeta();

		List<Component> lore = new ArrayList<>();
		lore.add(MiniMessage.miniMessage().deserialize("<italic:false><gray>" + this.options.getName()));

		IEffectSettings appLoreSettings = effects.get(HandlerType.APP_LORE);

		IEffectSettings loreSettings = effects.get(HandlerType.LORE);

		if (appLoreSettings != null) {
			HashMap<String, EncapsulatedType> settingMap = appLoreSettings.getSettings();

			EncapsulatedType loreSetting = settingMap.get("lore");

			if (loreSetting.getType() == Types.STRING) {

				String loreTotal = (String) loreSetting.getVal();

				String[] parts = loreTotal.split("\n");

				for (String s : parts) {
					String part = PlaceholderAPI.setPlaceholders(null, s);
					lore.add(MiniMessage.miniMessage().deserialize(part));
				}
			}

			if (loreSettings != null) {
				lore.add(Component.text("------------------"));
			}
		}

		if (loreSettings != null) {
			HashMap<String, EncapsulatedType> settingMap = loreSettings.getSettings();

			EncapsulatedType loreSetting = settingMap.get("lore");

			if (loreSetting.getType() == Types.STRING) {

				String loreTotal = (String) loreSetting.getVal();

				String[] parts = loreTotal.split("\n");

				for (String s : parts) {
					String part = PlaceholderAPI.setPlaceholders(null, s);
					lore.add(MiniMessage.miniMessage().deserialize(part));
				}
			}
		}

		meta.lore(lore);

		PersistentDataContainer pdc = meta.getPersistentDataContainer();

		// Mark this as a charm applicator
		pdc.set(new NamespacedKey(puPlugin, "ParallelCharm.CharmAppl"), PersistentDataType.INTEGER, 1);

		meta.setCustomModelData(options.getApplicatorModelData());

		item.setItemMeta(meta);

		return true;
	}

	public static Charm getCharmAppl(Parallelutils puPlugin, ParallelCharms pCharms, ItemStack item) {
		ItemMeta meta = item.getItemMeta();

		if (meta == null) return null;

		PersistentDataContainer pdc = meta.getPersistentDataContainer();

		Integer val = pdc.get(new NamespacedKey(puPlugin, "ParallelCharm.CharmAppl"), PersistentDataType.INTEGER);

		if (val == null || val != 1) {
			return null;
		}

		CharmOptions options = CharmOptions.parseOptions(item);

		return new Charm(pCharms, options);
	}

	public CharmOptions getOptions() {
		return options;
	}

	public void addRunnable(BukkitRunnable runnable) {
		this.runnables.add(runnable);
	}

	public UUID getUUID() {
		return charmId;
	}

	public List<BukkitRunnable> getRunnables() {
		return runnables;
	}

	public void cancelRunnables() {
		for (BukkitRunnable r : runnables) {
			Parallelutils.log(Level.INFO, "Cancelled Runnable");
			r.cancel();
		}
		runnables.clear();
	}
}
