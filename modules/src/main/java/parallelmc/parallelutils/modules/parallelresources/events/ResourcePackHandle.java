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
import org.jetbrains.annotations.Nullable;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelresources.ParallelResources;

import java.util.Arrays;
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

		if (!resources.doneLoading()) {
			if (player.hasPermission("parallelutils.resources.unenforced")) {
				return;
			} else {
				player.kick(Component.text("Server is still starting! Please check back in a minute."));
			}
		}

		if (!applyPack(player, null)) {
			ParallelUtils.log(Level.SEVERE, "UNABLE TO APPLY RESOURCE PACK!");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();

		if (!applyPack(player, event.getFrom())) {
			ParallelUtils.log(Level.SEVERE, "UNABLE TO APPLY RESOURCE PACK!");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onResourcePackStatus(PlayerResourcePackStatusEvent event) {
		PlayerResourcePackStatusEvent.Status status = event.getStatus();

		switch (status) {
			case DECLINED -> {
				if (event.getPlayer().hasPermission("parallelutils.resources.unenforced")) return;
				ParallelUtils.log(Level.WARNING, "Event " + status.toString() + " occurred! Declining join");
				event.getPlayer().kick(warningMessage);
			}
			case FAILED_DOWNLOAD -> {
				if (event.getPlayer().hasPermission("parallelutils.resources.unenforced")) return;
				ParallelUtils.log(Level.WARNING, "Event " + status.toString() + " occurred! Declining join");
				event.getPlayer().kick(warningMessage.append(
						Component.text("If you believe this was an error, please contact staff on Discord.")));
			}
		}
	}

	public boolean applyPack(@NotNull Player player, @Nullable World previousWorld) {
		World world = player.getWorld();

		String worldName = world.getName();

		byte[] hash = resources.getHash(worldName);

		ParallelUtils.log(Level.INFO, "Applying pack for world " + worldName);

		if (hash == null) {
			ParallelUtils.log(Level.INFO, "Tried to get pack for invalid world. Defaulting to base");
			worldName = "base";
			hash = resources.getHash("base");
		}

		ParallelUtils.log(Level.INFO, "Found hash for world " + worldName);


		if (previousWorld != null) {
			String previousName = previousWorld.getName();
			byte[] previousHash = resources.getHash(previousName);

			if (previousHash == null) {
				previousHash = resources.getHash("base");
			}

			// If the packs for each world are the same, just don't even apply the pack
			if (Arrays.equals(previousHash, hash)) {
				return true;
			}
		}

		String resourceUrl = resources.getResourceUrl(worldName);

		ParallelUtils.log(Level.INFO, "Got resource URL  " + resourceUrl);

        player.setResourcePack(resourceUrl, hash, warningMessage, !player.hasPermission("parallelutils.resources.unenforced"));

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
