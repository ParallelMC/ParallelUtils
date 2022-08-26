package parallelmc.parallelutils.modules.charms.handlers;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.charms.data.CharmOptions;

public abstract class ICharmApplyHandler extends ICharmHandler<Event> {
	public ICharmApplyHandler() {
		super(Event.class);
	}
	@Override
	public void handle(Event event, Player player, ItemStack item, CharmOptions options) {

	}

	public abstract void apply(Player player, @NotNull ItemStack item, @NotNull CharmOptions options);

	public abstract void remove(Player player, ItemStack item, CharmOptions options);
}
