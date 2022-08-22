package parallelmc.parallelutils.modules.bitsandbobs.minimodules;

import net.minecraft.world.entity.animal.Chicken;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class ChickenFeatherDrops implements Listener {


	@EventHandler
	public void onEntityItemDrop(EntityDropItemEvent event) {
		// Handles chicken item drops
		if (event.getEntity() instanceof Chicken) {
			// Check if chicken dropped egg
			Item drop = event.getItemDrop();
			if (drop.getItemStack().getType() == Material.EGG) {
				// Drop 1 feather with 25% chance
				Random rand = new Random();
				if (rand.nextInt(4) < 1) {
					ItemStack feather = new ItemStack(Material.FEATHER, 1);
					event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), feather);
				}
			}
		}
	}
}
