package parallelmc.parallelutils.commands;

import org.bukkit.command.CommandSender;

public class ParallelPermission {

	protected String permission;

	public ParallelPermission(String permission) {
		this.permission = permission;
	}

	public boolean hasPermission(CommandSender sender) {
		return Commands.hasPermission(sender, permission);
	}
}
