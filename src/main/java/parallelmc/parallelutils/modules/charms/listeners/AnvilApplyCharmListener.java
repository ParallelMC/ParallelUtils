package parallelmc.parallelutils.modules.charms.listeners;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.charms.ParallelCharms;
import parallelmc.parallelutils.modules.charms.data.Charm;

import java.util.List;
import java.util.logging.Level;

public class AnvilApplyCharmListener implements Listener {

	private final Parallelutils puPlugin;
	private final ParallelCharms pCharms;

	public AnvilApplyCharmListener(Parallelutils puPlugin, ParallelCharms pCharms) {
		this.puPlugin = puPlugin;
		this.pCharms = pCharms;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onAnvilPrep(PrepareAnvilEvent event) {
		AnvilInventory inventory = event.getInventory();

		ItemStack item1 = inventory.getFirstItem();
		ItemStack item2 = inventory.getSecondItem();

		if (item1 == null || item2 == null) {
			//event.setResult(null);
			return;
		}

		Player player = null;
		List<HumanEntity> viewers = event.getViewers();

		// Get the player associated with the event
		// If there's more than one, just set the result to null (if there's a charm associated with this) and return
		for (HumanEntity e : viewers) {
			if (e instanceof Player pl) {
				if (player == null) {
					player = pl;
				} else {
					if (Charm.hasCharm(item1)) {
						event.setResult(null);
					}
					if (Charm.hasCharm(item2)) {
						event.setResult(null);
					}
					return;
				}
			}
		}

		// Check if item1 has a charm. If it does, do not craft.
		if (Charm.hasCharm(item1)) {
			event.setResult(null);
			return;
		}

		// Check if item2 is a charm applicator. If it is not, clear result and do nothing
		Charm charm = Charm.getCharmAppl(puPlugin, pCharms, item2);
		if (charm == null) {
			return;
		}

		// Check that item2 only has one item
		if (item2.getAmount() > 1) {
			return;
		}

		// If both item1 does not have a charm and item2 is a charm, copy item1 and apply charm as result
		ItemStack result = new ItemStack(item1);


		boolean applyResult = charm.apply(result, player, false, false);
		if (!applyResult) {
			event.setResult(null);
			return;
		}

		event.setResult(result);
		inventory.setRepairCost(0);
	}
}
