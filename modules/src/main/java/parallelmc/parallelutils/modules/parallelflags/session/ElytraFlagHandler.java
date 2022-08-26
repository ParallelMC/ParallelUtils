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

public class ElytraFlagHandler extends Handler {
	public static final ElytraFlagHandler.Factory FACTORY = new ElytraFlagHandler.Factory();

	public static class Factory extends Handler.Factory<ElytraFlagHandler> {
		@Override
		public ElytraFlagHandler create(Session session) {
			return new ElytraFlagHandler(session);
		}

	}
	private final CustomFlagRegistry customFlagRegistry;

	public ElytraFlagHandler(Session session) {
		super(session);
		customFlagRegistry = CustomFlagRegistry.getInstance();
	}

	@Override
	public void initialize(LocalPlayer player, Location current, ApplicableRegionSet set) {
		StateFlag.State state = set.queryState(player, customFlagRegistry.getStateFlag("parallel-glide"));
		this.handleValue(((BukkitPlayer) player).getPlayer(), state);
	}

	@Override
	public boolean onCrossBoundary(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType) {
		BukkitPlayer bukkitPlayer;

		if (!(player instanceof BukkitPlayer)) {
			return false;
		}

		bukkitPlayer = (BukkitPlayer) player;

		if (getSession().getManager().hasBypass(player, (World) to.getExtent())) return true;

		StateFlag.State state = toSet.queryState(player, customFlagRegistry.getStateFlag("parallel-glide"));

		handleValue(bukkitPlayer.getPlayer(), state);

		return true;
	}

	public void handleValue(Player player, StateFlag.State state) {
		if (state != null)
		{
			boolean value = (state == StateFlag.State.ALLOW);

			if (!value && player.isGliding())
			{
				player.setGliding(false);
			}
		}
	}
}
