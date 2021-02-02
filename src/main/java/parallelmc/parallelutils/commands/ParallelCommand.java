package parallelmc.parallelutils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.commands.permissions.ParallelPermission;

public abstract class ParallelCommand {

	public String name;
	public ParallelPermission permission;

	public ParallelCommand(String name, ParallelPermission permission) {
		this.name = name;
		this.permission = permission;
	}

	public abstract boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args);

	protected boolean hasPermissions(CommandSender sender) {
		if (!permission.hasPermission(sender)) {
			sender.sendMessage("You do not have permission");
			return false;
		}
		return true;
	}
}
