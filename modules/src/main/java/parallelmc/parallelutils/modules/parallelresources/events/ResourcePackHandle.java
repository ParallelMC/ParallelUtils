package parallelmc.parallelutils.modules.parallelresources.events;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelresources.ParallelResources;

public class ResourcePackHandle implements Listener {

	private final ParallelUtils puPlugin;
	private final ParallelResources resources;

	public ResourcePackHandle(ParallelUtils puPlugin, ParallelResources resources) {
		this.puPlugin = puPlugin;
		this.resources = resources;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		applyPack(player);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();

		applyPack(player);

	}

	public boolean applyPack(@NotNull Player player) {
		World world = player.getWorld();

		String worldName = world.getName();

		return true;
	}
}
