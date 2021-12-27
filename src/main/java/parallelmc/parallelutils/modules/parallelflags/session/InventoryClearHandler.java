package parallelmc.parallelutils.modules.parallelflags.session;

import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import parallelmc.parallelutils.modules.parallelflags.CustomFlagRegistry;

import java.util.Set;

public class InventoryClearHandler extends Handler {

	public static final InventoryClearHandler.Factory FACTORY = new InventoryClearHandler.Factory();

	public static class Factory extends Handler.Factory<InventoryClearHandler> {
		@Override
		public InventoryClearHandler create(Session session) {
			return new InventoryClearHandler(session);
		}

	}

	private final CustomFlagRegistry customFlagRegistry;

	public InventoryClearHandler(Session session) {
		super(session);
		customFlagRegistry = CustomFlagRegistry.getInstance();
	}

	@Override
	public void initialize(LocalPlayer player, Location current, ApplicableRegionSet set) {
	}

	@Override
	public boolean onCrossBoundary(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType) {
		// Don't trigger if this isn't ACTUALLY on a boundary
		if (entered.isEmpty() && exited.isEmpty() && from.getExtent().equals(to.getExtent()))
		{
			return true;
		}

		BukkitPlayer bukkitPlayer;

		if (!(player instanceof BukkitPlayer)) {
			return true;
		}

		if (getSession().getManager().hasBypass(player, (World) to.getExtent())) return true;

		bukkitPlayer = (BukkitPlayer) player;

		StateFlag.State state = toSet.queryState(player, customFlagRegistry.getStateFlag("empty-inventory"));

		if (state != null) {
			if (state == StateFlag.State.DENY) {
				PlayerInventory inventory = bukkitPlayer.getPlayer().getInventory();

				if (inventory.isEmpty()) {
					bukkitPlayer.getPlayer().sendMessage("Empty your inventory before entering!");
					return false;
				}

				return true;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}
}
