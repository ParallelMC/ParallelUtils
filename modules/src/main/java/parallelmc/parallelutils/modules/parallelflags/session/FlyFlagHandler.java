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
import parallelmc.parallelutils.modules.parallelflags.CustomFlagRegistry;

import java.util.Set;

public class FlyFlagHandler extends Handler {

	public static final FlyFlagHandler.Factory FACTORY = new FlyFlagHandler.Factory();

	public static class Factory extends Handler.Factory<FlyFlagHandler> {
		@Override
		public FlyFlagHandler create(Session session) {
			return new FlyFlagHandler(session);
		}

	}
	private final CustomFlagRegistry customFlagRegistry;
	private Boolean originalFly;

	public FlyFlagHandler(Session session) {
		super(session);
		customFlagRegistry = CustomFlagRegistry.getInstance();
	}

	@Override
	public void initialize(LocalPlayer player, Location current, ApplicableRegionSet set) {
		StateFlag.State state = set.queryState(player, customFlagRegistry.getStateFlag("parallel-fly"));
		this.handleValue(((BukkitPlayer) player).getPlayer(), state);
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

		StateFlag.State state = toSet.queryState(player, customFlagRegistry.getStateFlag("parallel-fly"));

		handleValue(bukkitPlayer.getPlayer(), state);

		return true;
	}

	public void handleValue(Player player, StateFlag.State state) {
		if (state != null)
		{
			boolean value = (state == StateFlag.State.ALLOW);

			if (player.getAllowFlight() != value)
			{
				if (this.originalFly == null)
				{
					this.originalFly = player.getAllowFlight();
				}

				player.setAllowFlight(value);
			}
		}
		else
		{
			if (this.originalFly != null)
			{
				player.setAllowFlight(this.originalFly);

				this.originalFly = null;
			}
		}
	}
}
