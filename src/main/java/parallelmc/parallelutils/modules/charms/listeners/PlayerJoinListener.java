package parallelmc.parallelutils.modules.charms.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.charms.ParallelCharms;
import parallelmc.parallelutils.modules.charms.data.Charm;
import parallelmc.parallelutils.modules.charms.data.CharmOptions;
import parallelmc.parallelutils.modules.charms.data.IEffectSettings;
import parallelmc.parallelutils.modules.charms.handlers.HandlerCategory;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.handlers.ICharmHandler;
import parallelmc.parallelutils.modules.charms.handlers.ICharmRunnableHandler;
import parallelmc.parallelutils.modules.charms.helper.EncapsulatedType;
import parallelmc.parallelutils.modules.charms.helper.Types;

import java.util.HashMap;
import java.util.logging.Level;

public class PlayerJoinListener implements Listener {

	private final Parallelutils puPlugin;
	private final ParallelCharms pCharms;

	public PlayerJoinListener(Parallelutils puPlugin, ParallelCharms pCharms) {
		this.puPlugin = puPlugin;
		this.pCharms = pCharms;
	}


	// VERY important to be monitor so we don't have ghost runnables
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		// Search inventory for charms, start runnables on each relevant charm

		PlayerInventory inventory = player.getInventory();

		ItemStack[] contents = inventory.getContents();

		for (ItemStack item : contents) {
			Charm charm = Charm.parseCharm(pCharms, item, player);

			if (charm == null) {
				continue;
			}

			CharmOptions options = charm.getOptions();

			if (options != null) {
				HashMap<HandlerType, IEffectSettings> effects = options.getEffects();

				for (HandlerType t : effects.keySet()) {
					if (t.getCategory() == HandlerCategory.RUNNABLE) {
						ICharmHandler<Event> handler = pCharms.getHandler(t, Event.class);

						if (handler instanceof ICharmRunnableHandler runnableHandler) {
							BukkitRunnable runnable = runnableHandler.getRunnable(player, item, options);

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

							charm.addRunnable(runnable);
						}
					}
				}
			}
			pCharms.addCharm(player, charm);
		}
	}
}
