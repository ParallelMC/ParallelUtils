package parallelmc.parallelutils.commands;

import org.bukkit.command.CommandSender;

public class ParallelAndPermission extends ParallelPermission{

	protected ParallelPermission[] permissions;

	public ParallelAndPermission(ParallelPermission[] permissions) {
		super(permissions[0].permission);
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
