package parallelmc.parallelutils.modules.charms.handlers.impl;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.charms.data.CharmOptions;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.handlers.ICharmRunnableHandler;

import java.util.logging.Level;

public class CharmParticleHandler extends ICharmRunnableHandler {
	@Override
	public HandlerType getHandlerType() {
		return HandlerType.PARTICLE;
	}

	@NotNull
	@Override
	public BukkitRunnable getRunnable(Player player, ItemStack item, CharmOptions options) {

		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {
				Parallelutils.log(Level.INFO, "Ran runnable!");
				Parallelutils.log(Level.INFO, "Player: " + player.getUniqueId());
				Parallelutils.log(Level.INFO, "" + this.getTaskId());
			}
		};
		return runnable;
	}
}
