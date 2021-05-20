package parallelmc.parallelutils.commands.permissions;

import org.bukkit.command.CommandSender;

/**
 * A Permission that requires zero sub-permissions
 */
public class ParallelNullPermission extends ParallelPermission {
	public ParallelNullPermission() {
		super("");
	}

	/**
	 * Always returns true
	 * @param sender The sender to check
	 * @return Returns true
	 */
	@Override
	public boolean hasPermission(CommandSender sender) {
		return true;
	}
}
