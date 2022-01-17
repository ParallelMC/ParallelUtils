package parallelmc.parallelutils.modules.charms.handlers;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.charms.data.CharmOptions;

public abstract class ICharmRunnableHandler extends ICharmHandler<Event> {

	public ICharmRunnableHandler() {
		super(Event.class);
	}

	@Override
	public void handle(Event event, Player player, ItemStack item, CharmOptions options) {

	}

	@NotNull
	public abstract BukkitRunnable getRunnable(Player player, ItemStack item, CharmOptions options);
}
