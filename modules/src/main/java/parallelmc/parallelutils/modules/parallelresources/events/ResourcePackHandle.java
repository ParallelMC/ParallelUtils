package parallelmc.parallelutils.modules.parallelresources.events;

import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelresources.ParallelResources;

import java.util.logging.Level;

public class ResourcePackHandle implements Listener {



	private ParallelUtils puPlugin;
	private ParallelResources resources;

	private Component warningMessage;

	public ResourcePackHandle(ParallelUtils puPlugin, ParallelResources resources, Component warningMessage) {
		this.puPlugin = puPlugin;
		this.resources = resources;
		this.warningMessage = warningMessage;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		if (!applyPack(player)) {
			ParallelUtils.log(Level.SEVERE, "UNABLE TO APPLY RESOURCE PACK!");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();

		if (!applyPack(player)) {
			ParallelUtils.log(Level.SEVERE, "UNABLE TO APPLY RESOURCE PACK!");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onResourcePackStatus(PlayerResourcePackStatusEvent event) {
		PlayerResourcePackStatusEvent.Status status = event.getStatus();

		switch (status) {
			case DECLINED -> {
				ParallelUtils.log(Level.WARNING, "Event " + status.toString() + " occurred! Declining join");
				event.getPlayer().kick(warningMessage);
			}
			case FAILED_DOWNLOAD -> {
				ParallelUtils.log(Level.WARNING, "Event " + status.toString() + " occurred! Declining join");
				event.getPlayer().kick(warningMessage.append(
						Component.text("If you believe this was an error, please contact staff on Discord.")));
			}
		}
	}

	public boolean applyPack(@NotNull Player player) {
		World world = player.getWorld();

		String worldName = world.getName();

		byte[] hash = resources.getHash(worldName);

		if (hash == null) {
			ParallelUtils.log(Level.INFO, "Tried to get pack for invalid world. Defaulting to base");
			worldName = "base";
			hash = resources.getHash("base");
		}

		String resourceUrl = resources.getResourceUrl(worldName);

		player.setResourcePack(resourceUrl, hash, warningMessage, true);

		return true;
	}

	public void disable() {
		PlayerJoinEvent.getHandlerList().unregister(this);
		PlayerChangedWorldEvent.getHandlerList().unregister(this);
		puPlugin = null;
		resources = null;
		warningMessage = null;
	}
}
