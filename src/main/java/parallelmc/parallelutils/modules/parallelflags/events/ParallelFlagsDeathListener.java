package parallelmc.parallelutils.modules.parallelflags.events;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.LocationFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.parallelflags.CustomFlagRegistry;

import java.util.logging.Level;

public class ParallelFlagsDeathListener implements Listener {

	private RegionContainer container;

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();

		if (container == null) {
			container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		}

		RegionQuery query = container.createQuery();
		ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(event.getPlayer().getLocation()));

		if (set.isVirtual()) return;

		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(event.getPlayer());

		CustomFlagRegistry registry = CustomFlagRegistry.getInstance();

		StateFlag keepExp = registry.getStateFlag("keep-exp");
		StateFlag keepInventory = registry.getStateFlag("keep-inventory");

		if (keepExp != null) {
			StateFlag.State state = set.queryState(localPlayer, keepExp);

			if (state == StateFlag.State.ALLOW) {
				event.setKeepLevel(true);
				event.setShouldDropExperience(false);
			}
		}

		if (keepInventory != null) {
			StateFlag.State state = set.queryState(localPlayer, keepInventory);

			if (state == StateFlag.State.ALLOW) {
				event.setKeepInventory(true);
				event.getDrops().clear();
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerRespawnEvent(PlayerRespawnEvent event)
	{
		Player player = event.getPlayer();

		if (container == null) {
			container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		}

		RegionQuery query = container.createQuery();
		ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(event.getPlayer().getLocation()));

		if (set.isVirtual()) return;

		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(event.getPlayer());

		CustomFlagRegistry registry = CustomFlagRegistry.getInstance();

		LocationFlag respawnLocation = registry.getLocationFlag("respawn-location");

		if (respawnLocation != null) {
			Location location = set.queryValue(localPlayer, respawnLocation);

			if (location != null) {
				event.setRespawnLocation(new org.bukkit.Location(player.getWorld(), location.getX(), location.getY(), location.getZ()));
			}
		}
	}
}
