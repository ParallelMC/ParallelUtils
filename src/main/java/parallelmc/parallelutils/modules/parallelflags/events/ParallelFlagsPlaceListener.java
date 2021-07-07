package parallelmc.parallelutils.modules.parallelflags.events;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.TNT;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.parallelflags.CustomFlagRegistry;

import java.util.logging.Level;

public class ParallelFlagsPlaceListener implements Listener {

	private RegionContainer container;

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlockPlaced();

		if (container == null) {
			container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		}

		RegionQuery query = container.createQuery();
		ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(block.getLocation()));
		if (set.isVirtual()) return;

		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(event.getPlayer());

		CustomFlagRegistry registry = CustomFlagRegistry.getInstance();

		// tnt-disallow-time
		IntegerFlag tntFlag = registry.getIntegerFlag("tnt-disallow-time");

		if (tntFlag != null) {
			if (block.getBlockData() instanceof TNT) {
				Integer val = set.queryValue(localPlayer, tntFlag); // In hours

				if (val != null) {
					int playtime = event.getPlayer().getStatistic(Statistic.PLAY_ONE_MINUTE); // In ticks

					int playtimeHours = (int) (playtime / (20.0 * 60.0 * 60.0));

					if (val > playtimeHours) {
						// Deny interact
						denyEvent(event, set, localPlayer, "place that");
					}
				}
			}
		}
	}

	// TODO: Abstract out further
	private void denyEvent(BlockPlaceEvent event, ApplicableRegionSet set, LocalPlayer localPlayer, String action) {
		Flag<String> denyFlag = Flags.DENY_MESSAGE;

		if (denyFlag instanceof StringFlag strFlag) {
			String message = set.queryValue(localPlayer, strFlag);

			if (message != null) {
				event.getPlayer().sendMessage(message.replace("%what%", action));
			}
		} else {
			Parallelutils.log(Level.WARNING, "WorldGuard updated! DENY_MESSAGE no longer StringFlag");
		}

		Parallelutils.log(Level.INFO, "Denied");
		event.setCancelled(true);
	}
}
