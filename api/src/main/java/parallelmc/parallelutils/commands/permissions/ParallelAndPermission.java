package parallelmc.parallelutils.commands.permissions;

import org.bukkit.command.CommandSender;

/**
 * A Permission that requires all of the sub-permissions
 */
public class ParallelAndPermission extends ParallelPermission {

	protected ParallelPermission[] permissions;

	/**
	 * Create a Permission that requires all of the permissions given by {@code permissions}
	 * @param permissions All the sub permissions to require
	 */
	public ParallelAndPermission(ParallelPermission[] permissions) {
		super(permissions[0].getPermission());
		this.permissions = permissions;
	}

	/**
	 * Checks if the CommandSender has all of the sub-permissions
	 * @param sender The CommandSender to check
	 * @return Returns true if the CommandSender has all of the Permissions associated with this object
	 */
	@Override
	public boolean hasPermission(CommandSender sender) {
		for (ParallelPermission p : permissions) {
			if (!p.hasPermission(sender)) {
				return false;
			}
		}
		return true;
	}
}
