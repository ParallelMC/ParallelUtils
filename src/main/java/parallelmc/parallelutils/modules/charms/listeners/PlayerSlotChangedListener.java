package parallelmc.parallelutils.modules.charms.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.charms.ParallelCharms;
import parallelmc.parallelutils.modules.charms.data.Charm;
import parallelmc.parallelutils.modules.charms.data.CharmOptions;
import parallelmc.parallelutils.modules.charms.data.IEffectSettings;
import parallelmc.parallelutils.modules.charms.events.PlayerSlotChangedEvent;
import parallelmc.parallelutils.modules.charms.handlers.HandlerCategory;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.handlers.ICharmHandler;
import parallelmc.parallelutils.modules.charms.handlers.ICharmRunnableHandler;
import parallelmc.parallelutils.modules.charms.helper.EncapsulatedType;
import parallelmc.parallelutils.modules.charms.helper.Types;

import java.util.HashMap;
import java.util.logging.Level;

public class PlayerSlotChangedListener implements Listener {

	private final Parallelutils puPlugin;
	private final ParallelCharms pCharms;

	public PlayerSlotChangedListener(Parallelutils puPlugin, ParallelCharms pCharms) {
		this.puPlugin = puPlugin;
		this.pCharms = pCharms;
	}

	@EventHandler
	public void handleSlotChanged(PlayerSlotChangedEvent event) {
		ItemStack remainingItem = event.getRemainingItem();
		ItemStack removedItem = event.getRemovedItem();

		Player player = event.getPlayer();

		if (remainingItem.getType() != Material.AIR) {
			Charm remainingCharm = Charm.parseCharm(pCharms, remainingItem, player);

			if (remainingCharm != null) {
				CharmOptions options = remainingCharm.getOptions();

				if (options != null) {
					HashMap<HandlerType, IEffectSettings> effects = options.getEffects();

					for (HandlerType t : effects.keySet()) {
						if (t.getCategory() == HandlerCategory.RUNNABLE) {
							ICharmHandler<Event> handler = pCharms.getHandler(t, Event.class);

							if (handler instanceof ICharmRunnableHandler runnableHandler) {
								BukkitRunnable runnable = runnableHandler.getRunnable(player, remainingItem, options);

								IEffectSettings settings = effects.get(t);

								HashMap<String, EncapsulatedType> settingsMap = settings.getSettings();

								EncapsulatedType delayObj = settingsMap.get("delay");
								EncapsulatedType periodObj = settingsMap.get("period");

								if (delayObj == null || delayObj.getType() != Types.LONG) continue;
								if (periodObj == null || periodObj.getType() != Types.LONG) continue;

								Long delay = (Long) delayObj.getVal();
								Long period = (Long) periodObj.getVal();

								// Get options and get delay and period
								runnable.runTaskTimer(puPlugin, delay, period);

								remainingCharm.addRunnable(runnable);
							}
						}
					}
				}

				pCharms.addCharm(player, remainingCharm);
			}
		}

		if (removedItem.getType() != Material.AIR) {

			Charm removedCharm = Charm.parseCharm(pCharms, removedItem, player);

			if (removedCharm != null) {

				Charm removedC = pCharms.removeCharm(player, removedCharm);

				removedC.cancelRunnables();
			}
		}
	}
}
