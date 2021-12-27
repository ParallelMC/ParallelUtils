package parallelmc.parallelutils.modules.parallelflags.events;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import parallelmc.parallelutils.modules.parallelflags.CustomFlagRegistry;

public class ParallelFlagsItemDamageListener implements Listener {

	private RegionContainer container;

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onItemDamage(PlayerItemDamageEvent event) {
		if (container == null) {
			container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		}

		RegionQuery query = container.createQuery();
		ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(event.getPlayer().getLocation()));

		if (set.isVirtual()) return;

		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(event.getPlayer());

		CustomFlagRegistry registry = CustomFlagRegistry.getInstance();

		StateFlag itemDamage = registry.getStateFlag("prevent-item-damage");

		if (itemDamage != null) {
			StateFlag.State state = set.queryState(localPlayer, itemDamage);

			if (state == StateFlag.State.ALLOW) {
				event.setCancelled(true);
			}
		}
	}

}
