package parallelmc.parallelutils.commands.permissions;

import org.bukkit.command.CommandSender;
import parallelmc.parallelutils.commands.ParallelPermission;

public class ParallelOrPermission extends ParallelPermission {

	protected ParallelPermission[] permissions;

	public ParallelOrPermission(ParallelPermission[] permissions) {
		super(permissions[0].getPermission());
		this.permissions = permissions;
	}

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
