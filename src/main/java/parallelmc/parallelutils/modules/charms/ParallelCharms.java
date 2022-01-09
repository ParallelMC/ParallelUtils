package parallelmc.parallelutils.modules.charms;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.commands.ParallelCommand;
import parallelmc.parallelutils.modules.charms.commands.ApplyCharm;
import parallelmc.parallelutils.modules.charms.commands.RemoveCharm;
import parallelmc.parallelutils.modules.charms.data.Charm;
import parallelmc.parallelutils.modules.charms.data.CharmOptions;
import parallelmc.parallelutils.modules.charms.events.PlayerKillListener;
import parallelmc.parallelutils.modules.charms.handlers.CharmKillMessageHandler;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.handlers.ICharmHandler;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class ParallelCharms implements ParallelModule {

	private final HashMap<HandlerType, ICharmHandler<? extends Event>> handlers;

	public ParallelCharms() {
		handlers = new HashMap<>();
	}

	@Override
	public void onEnable() {
		PluginManager manager = Bukkit.getPluginManager();
		Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

		if (plugin == null) {
			Parallelutils.log(Level.SEVERE, "Unable to enable ParallelCharms. Plugin " + Constants.PLUGIN_NAME
					+ " does not exist!");
			return;
		}

		Parallelutils puPlugin = (Parallelutils) plugin;

		if (!puPlugin.registerModule("ParallelCharms", this)) {
			Parallelutils.log(Level.SEVERE, "Unable to register module ParallelCharms! " +
					"Module may already be registered. Quitting...");
			return;
		}

		// Register handlers
		if (!registerHandler(new CharmKillMessageHandler())) { Parallelutils.log(Level.WARNING, "Could not register event!"); }

		// Register events
		manager.registerEvents(new PlayerKillListener(this), puPlugin);

		// TODO: Remove before release
		CharmOptions testOptions = new CharmOptions(UUID.randomUUID(), null, new HashMap<>(), 123456);
		Charm testCharm = new Charm(testOptions);

		puPlugin.addCommand("applyCharm", new ApplyCharm(testCharm));
		puPlugin.addCommand("removeCharm", new RemoveCharm(testCharm));
	}

	@Override
	public void onDisable() {
	}

	/**
	 * Registers a charm handler
	 * @param handler The handler to register
	 * @return True when the handler type is new, false otherwise
	 */
	public boolean registerHandler(ICharmHandler<?> handler) {
		HandlerType type = handler.getHandlerType();

		if (handlers.containsKey(type)) {
			return false;
		}

		handlers.put(type, handler);
		return true;
	}

	/**
	 * Gets the handler associated with the type or null
	 * @return The handler or null
	 */
	@Nullable
	public <T extends Event> ICharmHandler<T> getHandler(HandlerType type, Class<T> event) {
		ICharmHandler<? extends Event> handler = handlers.get(type);
		try {
			if (handler.getEventType().equals(event)) {
				return (ICharmHandler<T>) handler;
			}
		} catch (Exception e) {
			Parallelutils.log(Level.SEVERE, "UNABLE TO CAST HANDLER!!!");
			e.printStackTrace();
		}
		return null;
	}
}
