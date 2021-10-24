package parallelmc.parallelutils.modules.charms.handlers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CharmKillMessageHandler implements ICharmHandler {

	@Override
	public HandlerType getHandlerType() {
		return HandlerType.MESSAGE_KILL;
	}

	@Override
	public void handle(Player player, ItemStack item) {

	}


}
