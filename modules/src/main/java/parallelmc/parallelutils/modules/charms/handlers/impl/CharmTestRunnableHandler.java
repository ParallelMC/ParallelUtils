package parallelmc.parallelutils.modules.charms.handlers.impl;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.charms.data.CharmOptions;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.handlers.ICharmRunnableHandler;

import java.util.logging.Level;

public class CharmTestRunnableHandler extends ICharmRunnableHandler {
	@Override
	public HandlerType getHandlerType() {
		return HandlerType.TEST_RUNNABLE;
	}

	@Nullable
	@Override
	public BukkitRunnable getRunnable(Player player, ItemStack item, CharmOptions options) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				Parallelutils.log(Level.WARNING, "Charm test functioning");
			}
		};
	}
}
