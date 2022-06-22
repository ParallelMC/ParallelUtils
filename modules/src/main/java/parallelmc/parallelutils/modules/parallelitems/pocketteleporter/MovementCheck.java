package parallelmc.parallelutils.modules.parallelitems.pocketteleporter;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import parallelmc.parallelutils.modules.parallelitems.ParallelItems;


public class MovementCheck extends BukkitRunnable {

    private final Vector position;
    private final Player player;
    private final int maxRuns;
    private int curRuns = 0;

    public MovementCheck(Player player, Location current, int maxRuns) {
        this.player = player;
        // use blockpos to give a bit of leeway
        this.position = new Vector(current.getBlockX(), current.getBlockY(), current.getBlockZ());
        this.maxRuns = maxRuns;
    }

    @Override
    public void run() {
        Location loc = player.getLocation();
        if (loc.getBlockX() != position.getX() ||
            loc.getBlockZ() != position.getZ() ||
            loc.getBlockY() != position.getY()) {
            ParallelItems.posManager.cancelTeleport(player, "move");
            this.cancel();
        }
        curRuns++;
        if (curRuns == maxRuns) {
            this.cancel();
        }
    }
}
