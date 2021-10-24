package parallelmc.parallelutils.modules.charms.handlers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ICharmHandler {

	HandlerType getHandlerType();

	void handle(Player player, ItemStack item);

}
