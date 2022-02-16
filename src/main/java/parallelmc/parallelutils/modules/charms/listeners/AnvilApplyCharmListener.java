package parallelmc.parallelutils.modules.charms.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

public class AnvilApplyCharmListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onAnvilPrep(PrepareAnvilEvent event) {
		AnvilInventory inventory = event.getInventory();

		ItemStack item1 = inventory.getFirstItem();
		ItemStack item2 = inventory.getSecondItem();

		// Check if item1 has a charm. If it does, do not craft.

		// Check if item2 is a charm. If it is not, clear result and do nothing

		// If both item1 does not have a charm and item2 is a charm, copy item1 and apply charm as result
	}
}
