package parallelmc.parallelutils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.commands.permissions.ParallelPermission;

/**
 * An abstract Command for ParallelUtils
 */
public abstract class ParallelCommand {

	public String name;
	public ParallelPermission permission;

	/**
	 * Creates a new ParallelCommand with the specified name and permission
	 * @param name The name of the command
	 * @param permission The permission for the created command
	 */
	public ParallelCommand(String name, ParallelPermission permission) {
		this.name = name;
		this.permission = permission;
	}

	/**
	 * Execute the command given the params from the Bukkit {@code onCommand} method
	 * @param sender The CommandSender that is executing this Command
	 * @param command The Bukkit {@code Command} object
	 * @param args The arguments for this command
	 * @return Returns true if the command executed successfully
	 */
	public abstract boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args);

	/**
	 * A helper method to check if a CommandSender has the permission associated with this command
	 * @param sender The sender to check
	 * @return Returns true if the sender has the permission
	 */
	protected boolean hasPermissions(CommandSender sender) {
		if (!permission.hasPermission(sender)) {
			sender.sendMessage("You do not have permission");
			return false;
		}
		return true;
	}
}
