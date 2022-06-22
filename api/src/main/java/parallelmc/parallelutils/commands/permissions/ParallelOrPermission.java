package parallelmc.parallelutils.commands.permissions;

import org.bukkit.command.CommandSender;

/**
 * A Permission that requires just one of the sub-permissions
 */
public class ParallelOrPermission extends ParallelPermission {

	protected ParallelPermission[] permissions;

	/**
	 * Create a Permission that requires one of the permissions given by {@code permissions}
	 * @param permissions All the sub permissions to check
	 */
	public ParallelOrPermission(ParallelPermission[] permissions) {
		super(permissions[0].getPermission());
		this.permissions = permissions;
	}

	/**
	 * Checks if the CommandSender has one of the sub-permissions
	 * @param sender The CommandSender to check
	 * @return Returns true if the CommandSender has at least one of the Permissions associated with this object
	 */
	@Override
	public boolean hasPermission(CommandSender sender) {
		for (ParallelPermission p : permissions) {
			if (p.hasPermission(sender)) {
				return true;
			}
		}
		return false;
	}
}
