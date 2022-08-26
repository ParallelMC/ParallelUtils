package parallelmc.parallelutils.util;

import org.bukkit.Location;

/**
 * A helper class for functions related to measuring the distance between two Locations
 */
public class DistanceTools {

	/**
	 * Calculates the 3-D Euclidean distance between two Locations
	 * @param loc1 The first Location
	 * @param loc2 The second Location
	 * @return The 3-D Euclidean distance between the two Locations
	 */
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

	/**
	 * Calculates the 2-D Euclidean distance between two Locations in the X-Z plane
	 * @param loc1 The first Location
	 * @param loc2 The second Location
	 * @return The 2-D Euclidean distance between the two Locations in the X-Z plane
	 */
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

	/**
	 * Calculates the 1-D Euclidean distance between two Locations in the vertical direction
	 * @param loc1 The first Location
	 * @param loc2 The second Location
	 * @return The 1-D Euclidean distance between the two Locations
	 */
	public static double distanceVertical(Location loc1, Location loc2) {
		if (loc1.getWorld().equals(loc2.getWorld())) {
			return loc1.getY() - loc2.getY();
		}
		return Integer.MAX_VALUE;
	}
}
