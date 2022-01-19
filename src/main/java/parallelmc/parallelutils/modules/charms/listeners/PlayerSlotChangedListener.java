package parallelmc.parallelutils.modules.charms.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.charms.events.PlayerSlotChangedEvent;

import java.util.logging.Level;

public class PlayerSlotChangedListener implements Listener {

	@EventHandler
	public void handleSlotChanged(PlayerSlotChangedEvent event) {
		ItemStack remainingItem = event.getRemainingItem();
		ItemStack removedItem = event.getRemovedItem();

		Parallelutils.log(Level.INFO,"------");
		Parallelutils.log(Level.INFO, removedItem.toString());
		Parallelutils.log(Level.INFO, remainingItem.toString());
	}
}
