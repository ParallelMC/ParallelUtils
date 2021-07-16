package parallelmc.parallelutils.modules.parallelflags.events;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.*;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.TNT;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.parallelflags.CustomFlagRegistry;

import java.util.logging.Level;

public class ParallelFlagsInteractListener implements Listener {

	private RegionContainer container;

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		ItemStack item = event.getItem();

		if (block == null && item == null) return;

		if (container == null) {
			container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		}

		RegionQuery query = container.createQuery();
		ApplicableRegionSet set;
		if(block != null) {
			set = query.getApplicableRegions(BukkitAdapter.adapt(block.getLocation()));
		}
		else{
			set = query.getApplicableRegions(BukkitAdapter.adapt(event.getPlayer().getLocation()));
		}
		if (set.isVirtual()) return;

		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(event.getPlayer());

		CustomFlagRegistry registry = CustomFlagRegistry.getInstance();

		// allow-trapdoors
		StateFlag trapdoorsFlag = registry.getStateFlag("allow-trapdoors");

		if (trapdoorsFlag != null) {

			if (block != null && block.getBlockData() instanceof TrapDoor) {
				StateFlag.State state = set.queryState(localPlayer, trapdoorsFlag);

				if (state == StateFlag.State.DENY) {
					denyEvent(event, set, localPlayer, "do that");
				}
			}
		}

		// tnt-disallow-time
		IntegerFlag tntFlag = registry.getIntegerFlag("tnt-disallow-time");

		if (tntFlag != null) {
			if ((block != null && block.getBlockData() instanceof TNT) ||
					(item != null && item.getType().equals(Material.TNT_MINECART))) {
				Integer val = set.queryValue(localPlayer, tntFlag); // In hours

				if (val != null) {

					int playtime = event.getPlayer().getStatistic(Statistic.PLAY_ONE_MINUTE); // In ticks

					int playtimeHours = (int) (playtime / (20.0 * 60.0 * 60.0));

					if (val > playtimeHours) {
						// Deny interact
						denyEvent(event, set, localPlayer, "do that");
					}
				}
			}
		}
	}

	/**
	 * This is simply a helper method to extract common code from events to deny them
	 * @param event The event
	 * @param set The current region set
	 * @param localPlayer The player associated with the event
	 */
	private void denyEvent(PlayerInteractEvent event, ApplicableRegionSet set, LocalPlayer localPlayer, String action) {
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
		event.setUseInteractedBlock(Event.Result.DENY);
		event.setUseItemInHand(Event.Result.DENY);
	}
}
