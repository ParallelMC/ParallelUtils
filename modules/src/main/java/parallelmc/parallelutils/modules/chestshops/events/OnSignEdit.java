package parallelmc.parallelutils.modules.chestshops.events;

import io.papermc.paper.event.player.PlayerOpenSignEvent;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import parallelmc.parallelutils.modules.chestshops.ChestShops;
import parallelmc.parallelutils.modules.chestshops.Shop;

public class OnSignEdit implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSignEdit(PlayerOpenSignEvent event) {
		Block block = event.getSign().getBlock();

		Shop shop = ChestShops.get().getShopFromSignPos(block.getLocation());
		if (shop != null) {
			// If the sign clicked is a chest shop, cancel the edit
			event.setCancelled(true);
		}
	}
}
