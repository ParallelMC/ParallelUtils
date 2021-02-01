package parallelmc.parallelutils.commands.permissions;

import org.bukkit.command.CommandSender;
import parallelmc.parallelutils.commands.ParallelPermission;

public class ParallelNullPermission extends ParallelPermission {
	public ParallelNullPermission() {
		super("");
	}

	@Override
	public boolean hasPermission(CommandSender sender) {
		return true;
	}
}
