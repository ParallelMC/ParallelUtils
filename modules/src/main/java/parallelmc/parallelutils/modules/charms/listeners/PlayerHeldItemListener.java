package parallelmc.parallelutils.modules.charms.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.charms.ParallelCharms;
import parallelmc.parallelutils.modules.charms.events.PlayerSlotChangedEvent;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.handlers.ICharmHandler;

public class PlayerHeldItemListener implements Listener {

	private final ParallelUtils puPlugin;
	private final ParallelCharms pCharms;

	public PlayerHeldItemListener(ParallelUtils puPlugin, ParallelCharms pCharms) {
		this.puPlugin = puPlugin;
		this.pCharms = pCharms;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerItemHeld(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		PlayerInventory inventory = player.getInventory();

		ItemStack removedItem = inventory.getItem(event.getPreviousSlot());
		ItemStack remainingItem = inventory.getItem(event.getNewSlot());

		int rawSlot = event.getNewSlot() + 36; // No idea if this will work, but hopefully

		new BukkitRunnable() {
			@Override
			public void run() {
				PlayerSlotChangedEvent slotChangedEvent = new PlayerSlotChangedEvent(player, removedItem, remainingItem, rawSlot);

				ICharmHandler<PlayerSlotChangedEvent> changedEvent = pCharms.getHandler(HandlerType.PLAYER_PARTICLE, PlayerSlotChangedEvent.class);

				if (changedEvent != null) {
					changedEvent.handle(slotChangedEvent, player, remainingItem, null);
				}
			}
		}.runTaskLater(puPlugin, 1);
	}
}
