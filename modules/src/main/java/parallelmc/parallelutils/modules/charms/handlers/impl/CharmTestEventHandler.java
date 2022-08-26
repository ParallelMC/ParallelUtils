package parallelmc.parallelutils.modules.charms.handlers.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.charms.data.CharmOptions;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.handlers.ICharmHandler;

import java.util.logging.Level;

public class CharmTestEventHandler extends ICharmHandler<PlayerDeathEvent> {
	public CharmTestEventHandler() {
		super(PlayerDeathEvent.class);
	}

	@Override
	public HandlerType getHandlerType() {
		return HandlerType.TEST_EVENT;
	}

	@Override
	public void handle(PlayerDeathEvent event, Player player, ItemStack item, CharmOptions options) {
		ParallelUtils.log(Level.WARNING, "Player " + player.getName() + " died. Test charm activated");
	}
}
