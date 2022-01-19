package parallelmc.parallelutils.modules.charms.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import parallelmc.parallelutils.modules.charms.ParallelCharms;
import parallelmc.parallelutils.modules.charms.data.CharmOptions;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.handlers.ICharmHandler;

public class PlayerKillListener implements Listener {

	private final ParallelCharms pCharms;

	public PlayerKillListener(ParallelCharms pCharms) {
		this.pCharms = pCharms;
	}

	@EventHandler
	public void playerDeathEvent(PlayerDeathEvent event) {
		Player deadPlayer = event.getPlayer();

		Player killer = deadPlayer.getKiller();

		if (killer == null) {
			return; // This could eventually handle this on a player's death
		}

		// Player killed player. Handle killer's charms events

		// Get possible items
		PlayerInventory killerInventory = killer.getInventory();

		ItemStack mainHand = killerInventory.getItemInMainHand();
		CharmOptions mainOptions = CharmOptions.parseOptions(mainHand, killer);
		ItemStack offHand = killerInventory.getItemInOffHand();
		CharmOptions offOptions = CharmOptions.parseOptions(offHand, killer);
		ItemStack helmet = killerInventory.getHelmet();
		CharmOptions helmOptions = CharmOptions.parseOptions(helmet, killer);
		ItemStack chestplate = killerInventory.getChestplate();
		CharmOptions chestOptions = CharmOptions.parseOptions(chestplate, killer);
		ItemStack leggings = killerInventory.getLeggings();
		CharmOptions legOptions = CharmOptions.parseOptions(leggings, killer);
		ItemStack boots = killerInventory.getBoots();
		CharmOptions bootsOptions = CharmOptions.parseOptions(boots, killer);


		ICharmHandler<PlayerDeathEvent> killMessage = pCharms.getHandler(HandlerType.MESSAGE_KILL, PlayerDeathEvent.class);

		if (killMessage != null) {
			// This order makes it so main hand takes the highest precedence
			if (helmet != null && helmOptions != null) { killMessage.handle(event, killer, helmet, helmOptions); }
			if (chestplate != null && chestOptions != null) { killMessage.handle(event, killer, chestplate, chestOptions); }
			if (leggings != null && legOptions != null) { killMessage.handle(event, killer, leggings, legOptions); }
			if (boots != null && bootsOptions != null) { killMessage.handle(event, killer, boots, bootsOptions); }
			if (offOptions != null)  { killMessage.handle(event, killer, offHand, offOptions); }
			if (mainOptions != null) { killMessage.handle(event, killer, mainHand, mainOptions); }
		}

	}
}
