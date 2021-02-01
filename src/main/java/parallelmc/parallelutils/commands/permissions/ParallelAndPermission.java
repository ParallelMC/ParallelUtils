package parallelmc.parallelutils.commands.permissions;

import org.bukkit.command.CommandSender;
import parallelmc.parallelutils.commands.ParallelPermission;

public class ParallelAndPermission extends ParallelPermission {

	protected ParallelPermission[] permissions;

	public ParallelAndPermission(ParallelPermission[] permissions) {
		super(permissions[0].getPermission());
		this.permissions = permissions;
	}

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
