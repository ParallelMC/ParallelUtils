package parallelmc.parallelutils.util;

import org.bukkit.Location;

public class DistanceTools {
	public static double distance(Location loc1, Location loc2) {
		if (loc1.getWorld().equals(loc2.getWorld())) {
			double distancex = loc1.getX() - loc2.getX();
			double distancey = loc1.getY() - loc2.getY();
			double distancez = loc1.getZ() - loc2.getZ();
			distancex *= distancex;
			distancey *= distancey;
			distancez *= distancez;
			return Math.sqrt(distancex + distancey + distancez);
		}
		return Integer.MAX_VALUE;
	}

	public static double distanceHorizontal(Location loc1, Location loc2) {
		if (loc1.getWorld().equals(loc2.getWorld())) {
			double distancex = loc1.getX() - loc2.getX();
			double distancez = loc1.getZ() - loc2.getZ();
			distancex *= distancex;
			distancez *= distancez;
			return Math.sqrt(distancex + distancez);
		}
		return Integer.MAX_VALUE;
	}

	public static double distanceVertical(Location loc1, Location loc2) {
		if (loc1.getWorld().equals(loc2.getWorld())) {
			return loc1.getY() - loc2.getY();
		}
		return Integer.MAX_VALUE;
	}
}