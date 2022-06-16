package parallelmc.parallelutils.modules.charms.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import parallelmc.parallelutils.modules.charms.ParallelCharms;
import parallelmc.parallelutils.modules.charms.data.CharmOptions;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.handlers.ICharmHandler;

public class EntityDamageListener implements Listener {

	private final ParallelCharms pCharms;

	public EntityDamageListener(ParallelCharms pCharms) {
		this.pCharms = pCharms;
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();

		if (damager instanceof Player player) {
			PlayerInventory playerInventory = player.getInventory();

			ItemStack mainHand = playerInventory.getItemInMainHand();
			CharmOptions mainOptions = CharmOptions.parseOptions(mainHand, player);
			ItemStack offHand = playerInventory.getItemInOffHand();
			CharmOptions offOptions = CharmOptions.parseOptions(offHand, player);
			ItemStack helmet = playerInventory.getHelmet();
			CharmOptions helmOptions = CharmOptions.parseOptions(helmet, player);
			ItemStack chestplate = playerInventory.getChestplate();
			CharmOptions chestOptions = CharmOptions.parseOptions(chestplate, player);
			ItemStack leggings = playerInventory.getLeggings();
			CharmOptions legOptions = CharmOptions.parseOptions(leggings, player);
			ItemStack boots = playerInventory.getBoots();
			CharmOptions bootsOptions = CharmOptions.parseOptions(boots, player);

			ICharmHandler<EntityDamageByEntityEvent> commandHandler = pCharms.getHandler(HandlerType.COMMAND_HIT, EntityDamageByEntityEvent.class);

			runHandler(event, player, mainHand, mainOptions,  offHand, offOptions, helmet, helmOptions, chestplate, chestOptions, leggings, legOptions, boots, bootsOptions, commandHandler);
		}
	}


	private void runHandler(EntityDamageByEntityEvent event, Player player, ItemStack mainHand, CharmOptions mainOptions, ItemStack offHand, CharmOptions offOptions, ItemStack helmet, CharmOptions helmOptions, ItemStack chestplate, CharmOptions chestOptions, ItemStack leggings, CharmOptions legOptions, ItemStack boots, CharmOptions bootsOptions, ICharmHandler<EntityDamageByEntityEvent> handler) {
		if (handler != null) {
			// This order makes it so main hand takes the highest precedence
			if (helmet != null && helmOptions != null) { handler.handle(event, player, helmet, helmOptions); }
			if (chestplate != null && chestOptions != null) { handler.handle(event, player, chestplate, chestOptions); }
			if (leggings != null && legOptions != null) { handler.handle(event, player, leggings, legOptions); }
			if (boots != null && bootsOptions != null) { handler.handle(event, player, boots, bootsOptions); }
			if (offOptions != null)  { handler.handle(event, player, offHand, offOptions); }
			if (mainOptions != null) { handler.handle(event, player, mainHand, mainOptions); }
		}
	}
}
