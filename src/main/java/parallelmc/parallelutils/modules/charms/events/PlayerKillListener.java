package parallelmc.parallelutils.modules.charms.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import parallelmc.parallelutils.modules.charms.ParallelCharms;
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
		ItemStack offHand = killerInventory.getItemInOffHand();
		ItemStack helmet = killerInventory.getHelmet();
		ItemStack chestplate = killerInventory.getChestplate();
		ItemStack leggings = killerInventory.getLeggings();
		ItemStack boots = killerInventory.getBoots();


		ICharmHandler killMessage = pCharms.getHandler(HandlerType.MESSAGE_KILL);

		if (killMessage != null) {
			killMessage.handle(killer, mainHand);
			killMessage.handle(killer, offHand);
			killMessage.handle(killer, helmet);
			killMessage.handle(killer, chestplate);
			killMessage.handle(killer, leggings);
			killMessage.handle(killer, boots);
		}
		// Handle main hand

	}
}
