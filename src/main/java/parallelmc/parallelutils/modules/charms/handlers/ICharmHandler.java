package parallelmc.parallelutils.modules.charms.handlers;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import parallelmc.parallelutils.modules.charms.data.CharmOptions;

import java.lang.reflect.Type;

public abstract class ICharmHandler<T extends Event> {

	private final Class<T> typeParameterClass;

	public ICharmHandler(Class<T> typeParameterClass) {
		this.typeParameterClass = typeParameterClass;
	}

	public abstract HandlerType getHandlerType();

	public Class<T> getEventType() {
		return typeParameterClass;
	}

	public abstract void handle(T event, Player player, ItemStack item, CharmOptions options);

}
