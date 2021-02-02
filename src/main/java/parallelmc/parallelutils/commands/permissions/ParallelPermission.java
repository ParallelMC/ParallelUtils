package parallelmc.parallelutils.commands.permissions;

import org.bukkit.command.CommandSender;
import parallelmc.parallelutils.commands.Commands;

public class ParallelPermission {

	String permission;

	public ParallelPermission(String permission) {
		this.permission = permission;
	}

	public boolean hasPermission(CommandSender sender) {
		return Commands.hasPermission(sender, permission);
	}

	public String getPermission() {
		return permission;
	}
}
