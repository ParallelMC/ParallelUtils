package parallelmc.parallelutils.commands.permissions;

import org.bukkit.command.CommandSender;
import parallelmc.parallelutils.commands.Commands;

/**
 * A basic ParallelPermission that checks if the user has a single Bukkit permission
 */
public class ParallelPermission {

	String permission;

	/**
	 * Create a Permission that requires a single Bukkit permission
	 * @param permission The Permission to require
	 */
	public ParallelPermission(String permission) {
		this.permission = permission;
	}

	/**
	 * Checks if the CommandSender has the associated Bukkit permission
	 * @param sender The CommandSender to check
	 * @return Returns true if the CommandSender has the associated permission with this object
	 */
	public boolean hasPermission(CommandSender sender) {
		return Commands.hasPermission(sender, permission);
	}

	/**
	 * Return the permission associated with this object
	 * @return The permission associated with this object
	 */
	public String getPermission() {
		return permission;
	}
}
