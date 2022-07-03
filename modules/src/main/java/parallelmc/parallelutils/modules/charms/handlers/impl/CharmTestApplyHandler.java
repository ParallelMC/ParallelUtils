package parallelmc.parallelutils.modules.charms.handlers.impl;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.charms.data.CharmOptions;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.handlers.ICharmApplyHandler;

import java.util.logging.Level;

public class CharmTestApplyHandler extends ICharmApplyHandler {
	@Override
	public void apply(Player player, @NotNull ItemStack item, @NotNull CharmOptions options) {
		ParallelUtils.log(Level.WARNING, "Player " + player.getName() + " applied charm");
	}

	@Override
	public void remove(Player player, ItemStack item, CharmOptions options) {
		ParallelUtils.log(Level.WARNING, "Player " + player.getName() + " removed charm");
	}

	@Override
	public HandlerType getHandlerType() {
		return HandlerType.TEST_APPLY;
	}
}
