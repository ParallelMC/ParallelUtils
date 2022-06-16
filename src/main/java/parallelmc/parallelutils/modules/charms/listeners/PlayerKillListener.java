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
		ICharmHandler<PlayerDeathEvent> commandKiller = pCharms.getHandler(HandlerType.COMMAND_KILL, PlayerDeathEvent.class);

		runHandler(event, killer, mainHand, mainOptions, offHand, offOptions, helmet, helmOptions, chestplate, chestOptions, leggings, legOptions, boots, bootsOptions, killMessage);
		runHandler(event, killer, mainHand, mainOptions, offHand, offOptions, helmet, helmOptions, chestplate, chestOptions, leggings, legOptions, boots, bootsOptions, commandKiller);
	}

	private void runHandler(PlayerDeathEvent event, Player player, ItemStack mainHand, CharmOptions mainOptions, ItemStack offHand, CharmOptions offOptions, ItemStack helmet, CharmOptions helmOptions, ItemStack chestplate, CharmOptions chestOptions, ItemStack leggings, CharmOptions legOptions, ItemStack boots, CharmOptions bootsOptions, ICharmHandler<PlayerDeathEvent> handler) {
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
