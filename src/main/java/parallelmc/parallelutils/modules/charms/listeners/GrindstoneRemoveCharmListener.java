package parallelmc.parallelutils.modules.charms.listeners;

import com.destroystokyo.paper.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.charms.ParallelCharms;
import parallelmc.parallelutils.modules.charms.data.Charm;

import java.util.logging.Level;

public class GrindstoneRemoveCharmListener implements Listener {

	private final Parallelutils puPlugin;
	private final ParallelCharms pCharms;

	public GrindstoneRemoveCharmListener(Parallelutils puPlugin, ParallelCharms pCharms) {
		this.puPlugin = puPlugin;
		this.pCharms = pCharms;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onGrindstonePrep(PrepareGrindstoneEvent event) { // Ignore that this is deprecated. It shouldn't be
		GrindstoneInventory inventory = event.getInventory();

		ItemStack upper = inventory.getUpperItem();
		ItemStack lower = inventory.getLowerItem();

		if (upper == null || lower == null) {
			return; // Let minecraft deal with it. We don't care
		}

		// If upper item does not have a charm, ignore. Let minecraft deal with it
		if (!Charm.hasCharm(upper)) {
			return;
		}

		Player player = null;

		// Only one viewer allowed
		for (HumanEntity e : event.getViewers()) {
			if (e instanceof Player pl) {
				if (player == null) {
					player = pl;
				} else {
					event.setResult(null);
					return;
				}
			}
		}


		if (lower.getType() == Material.PAPER) {
			ItemMeta meta = lower.getItemMeta();

			PersistentDataContainer pdc = meta.getPersistentDataContainer();

			Integer val = pdc.get(new NamespacedKey(puPlugin, "ParallelUtils.CharmRemover"), PersistentDataType.INTEGER);

			if (val == null) {
				return;
			}

			if (val == 1) {
				// At this point, the upper item has a charm and the lower item is a remover. Set result

				ItemStack result = new ItemStack(upper);

				Charm charm = Charm.parseCharm(pCharms, result, player);

				if (charm == null) {
					Parallelutils.log(Level.WARNING,  "Something went wrong while taking off a charm!");
					return;
				}

				boolean res = charm.takeOff(result, player);

				if (!res) {
					Parallelutils.log(Level.WARNING,  "Something went wrong while taking off a charm!");
					return;
				}

				event.setResult(result);
			}
		}
	}
}
