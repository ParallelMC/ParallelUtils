package parallelmc.parallelutils.modules.charms.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerSlotChangedEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final Player player;
	private final ItemStack removedItem;
	private final ItemStack remainingItem;

	public PlayerSlotChangedEvent(@NotNull final Player player, ItemStack removedItem, ItemStack remainingItem) {
		this.player = player;
		this.removedItem = removedItem;
		this.remainingItem = remainingItem;
	}

	public Player getPlayer() {
		return player;
	}

	/**
	 * Note: Quantities may not be accurate for removed item
	 * @return The removed item stack, quantities may not be accurate
	 */
	public ItemStack getRemovedItem() {
		return removedItem;
	}

	public ItemStack getRemainingItem() {
		return remainingItem;
	}

	@NotNull
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	@NotNull
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
