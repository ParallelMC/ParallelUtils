package parallelmc.parallelutils.modules.charms.listeners;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.charms.ParallelCharms;
import parallelmc.parallelutils.modules.charms.data.Charm;

import java.util.logging.Level;

public class CraftingRemoveCharmListener implements Listener {

	private final ParallelUtils puPlugin;
	private final ParallelCharms pCharms;

	public CraftingRemoveCharmListener(ParallelUtils puPlugin, ParallelCharms pCharms) {
		this.puPlugin = puPlugin;
		this.pCharms = pCharms;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onCraftingPrep(PrepareItemCraftEvent event) {
		CraftingInventory inventory = event.getInventory();

		ItemStack[] items = inventory.getMatrix();

		int count = 0;

		ItemStack item1 = null;
		ItemStack item2 = null;

		for (ItemStack i : items) {
			if (i != null) {
				count++;
				if (item1 == null) {
					item1 = i;
				} else if (item2 == null) {
					item2 = i;
				}
			}
		}

		if (count != 2 || item2 == null) {
			return; // Let minecraft deal with it. We don't care
		}

		boolean charm1 = false;
		boolean charm2 = false;

		if (Charm.hasCharm(item1)) {
			charm1 = true;
		}
		if (Charm.hasCharm(item2)) {
			charm2 = true;
		}

		if (charm1 == charm2) { // There MUST be only one charm
			return;
		}

		Player player = null;

		// Only one viewer allowed
		for (HumanEntity e : event.getViewers()) {
			if (e instanceof Player pl) {
				if (player == null) {
					player = pl;
				} else {
					event.getInventory().setResult(null);
					return;
				}
			}
		}

		ItemStack charm;
		ItemStack remover;

		if (charm1) {
			// item1 has charm, item2 should be remover
			charm = item1;
			remover = item2;
		} else {
			// item2 has charm, item1 should be remover
			charm = item2;
			remover = item1;
		}

		if (remover.getAmount() != 1) {
			event.getInventory().setResult(null);
			return;
		}


		if (remover.getType() == Material.PAPER) {
			ItemMeta meta = remover.getItemMeta();

			PersistentDataContainer pdc = meta.getPersistentDataContainer();

			Integer val = pdc.get(new NamespacedKey(puPlugin, "ParallelUtils.CharmRemover"), PersistentDataType.INTEGER);

			if (val == null) {
				event.getInventory().setResult(null);
				return;
			}

			if (val == 1) {
				// At this point, charm and remover are in place. Set result

				ItemStack result = new ItemStack(charm);

				Charm charmObj = Charm.parseCharm(pCharms, result, player);

				if (charmObj == null) {
					ParallelUtils.log(Level.WARNING,  "Something went wrong while taking off a charm!");
					return;
				}

				boolean res = charmObj.takeOff(result, player);

				if (!res) {
					ParallelUtils.log(Level.WARNING,  "Something went wrong while taking off a charm!");
					return;
				}

				event.getInventory().setResult(result);
			}
		}
	}
}
