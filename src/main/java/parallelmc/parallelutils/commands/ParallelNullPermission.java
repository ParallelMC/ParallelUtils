package parallelmc.parallelutils.commands;

import org.bukkit.command.CommandSender;

public class ParallelNullPermission extends ParallelPermission {
	public ParallelNullPermission() {
		super("");
	}

	@Override
	public boolean hasPermission(CommandSender sender) {
		return true;
	}
}
