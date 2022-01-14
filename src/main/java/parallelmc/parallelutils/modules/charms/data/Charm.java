package parallelmc.parallelutils.modules.charms.data;

import org.bukkit.Bukkit;
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
import parallelmc.parallelutils.util.BukkitTools;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class Charm {

	private final Parallelutils puPlugin;

	private final ParallelCharms pCharms;

	// Each charm is unique, mostly to track things for counters
	// but can also be used for player-specific text
	// This is also needed to bind a charm to an item. UUID will be in NBT
	private UUID charmId;

	// Has this charm been applied to an item?
	private boolean applied;

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
		applied = false;
	}

	public Charm(ParallelCharms pCharms, CharmOptions options, boolean applied) {
		this(pCharms, options);

		this.applied = applied;
	}

	public Charm(ParallelCharms pCharms, CharmOptions options, boolean applied, UUID uuid) {
		this(pCharms, options, applied);

		this.charmId = uuid;
	}

	public boolean apply(ItemStack item) {
		return apply(item, null);
	}
	public boolean apply(ItemStack item, Player player) {
		try {
			if (applied) {
				return false;
			}

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
					ICharmHandler<Event> handler = pCharms.getHandler(t, Event.class);

					if (handler instanceof ICharmRunnableHandler runnableHandler) {
						BukkitRunnable runnable = runnableHandler.getRunnable(player, item, this.options);

						runnable.runTask(puPlugin);

						runnables.add(runnable);
					}
				} else if (t.getCategory() == HandlerCategory.APPLY) {
					ICharmHandler<Event> handler = pCharms.getHandler(t, Event.class);

					if (handler instanceof ICharmApplyHandler applyHandler) {
						applyHandler.apply(player, item, this.options);
					}
				}
			}

			pCharms.applyCharm(player, this);
			applied = true;
			return true;
		} catch (Exception e) {
			Parallelutils.log(Level.SEVERE, "Unable to apply charm!");
			e.printStackTrace();
			return false;
		}
	}

	public boolean takeOff(ItemStack item, Player player) {
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

			for (BukkitRunnable runnable : runnables) {
				runnable.cancel();
			}

			HashMap<HandlerType, IEffectSettings> effects = options.getEffects();
			for (HandlerType t : effects.keySet()) {
				if (t.getCategory() == HandlerCategory.APPLY) {
					ICharmHandler<Event> handler = pCharms.getHandler(t, Event.class);

					if (handler instanceof ICharmApplyHandler applyHandler) {
						applyHandler.remove(player, item, this.options);
					}
				}
			}

			item.setItemMeta(meta);


			pCharms.removeCharm(player, this);
			applied = false;
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

		return new Charm(pCharms, options, true, uuid);
	}

	@Nullable
	public static Charm parseCharm(ParallelCharms pCharms,ItemStack item) {
		return parseCharm(pCharms, item, null);
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
}
