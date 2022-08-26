package parallelmc.parallelutils.modules.expstorage;

import org.bukkit.entity.Player;

/**
 * A helper class for converting levels into experience
 * https://www.spigotmc.org/threads/how-to-get-players-exp-points.239171/
 */
public class ExpConverter
{
	private static int getExpToLevelUp(int level){
		if(level <= 15){
			return 2 * level + 7;
		} else if(level <= 30){
			return 5 * level - 38;
		} else {
			return 9 * level - 158;
		}
	}

	private static int getExpAtLevel(int level){
		if(level <= 16){
			return (int)(Math.pow(level,2) + 6 * level);
		} else if(level <= 31){
			return (int)(2.5*Math.pow(level,2) - 40.5 * level + 360.0);
		} else {
			return (int)(4.5*Math.pow(level,2) - 162.5 * level + 2220.0);
		}
	}

	/**
	 * Gets the player's current total experience
	 * @param player The player to get experience for
	 * @return The player's current total experience
	 */
	public static int getPlayerCurrentExp(Player player){
		int exp = 0;
		int level = player.getLevel();

		// Get the amount of XP in past levels
		exp += getExpAtLevel(level);

		// Get amount of XP towards next level
		exp += Math.round(getExpToLevelUp(level) * player.getExp());

		return exp;
	}

}
