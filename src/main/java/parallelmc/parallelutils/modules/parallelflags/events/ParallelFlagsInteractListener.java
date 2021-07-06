package parallelmc.parallelutils.modules.parallelflags.events;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.parallelflags.CustomFlagRegistry;

import java.util.logging.Level;

public class ParallelFlagsInteractListener implements Listener {

	private RegionContainer container;

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();

		if (block == null) return;

		if (container == null) {
			container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		}

		RegionQuery query = container.createQuery();
		ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(block.getLocation()));
		if (set.isVirtual()) return;

		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(event.getPlayer());

		CustomFlagRegistry registry = CustomFlagRegistry.getInstance();
		StateFlag flag = registry.getStateFlag("allow-trapdoors");

		if (flag != null) {

			if (block.getBlockData() instanceof TrapDoor) {
				StateFlag.State state = set.queryState(localPlayer, flag);

				if (state == StateFlag.State.DENY) {
					Flag<String> denyFlag = Flags.DENY_MESSAGE;

					if (denyFlag instanceof StringFlag strFlag) {
						String message = set.queryValue(localPlayer, strFlag);

						if (message != null) {
							event.getPlayer().sendMessage(message);
						}
					} else {
						Parallelutils.log(Level.WARNING, "WorldGuard updated! DENY_MESSAGE no longer StringFlag");
					}

					Parallelutils.log(Level.INFO, "Denied");
					event.setUseInteractedBlock(Event.Result.DENY);
				}
			}
		}
	}
}
