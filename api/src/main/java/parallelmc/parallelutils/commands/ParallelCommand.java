package parallelmc.parallelutils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.commands.permissions.ParallelPermission;

import java.util.List;

/**
 * An abstract Command for ParallelUtils
 */
public abstract class ParallelCommand {

	public String name;
	public String helpText;
	public ParallelPermission permission;

	/**
	 * Creates a new ParallelCommand with the specified name and permission
	 * @param name The name of the command
	 * @param permission The permission for the created command
	 */
	public ParallelCommand(String name, ParallelPermission permission) {
		this.name = name;
		this.helpText = "";
		this.permission = permission;
	}

	/**
	 * Creates a new ParallelCommand with the specified name, helpText and permission
	 * @param name The name of the command
	 * @param helpText The helpText of the command
	 * @param permission The permission for the created command
	 */
	public ParallelCommand(String name, String helpText, ParallelPermission permission) {
		this.name = name;
		this.helpText = helpText;
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
	 * Retrieve the tab complete array associated with the given command and arguments
	 * @param sender The sender of this command
	 * @param args The arguments associated with the command
	 * @return The List associated with the given command, sender, and arguments
	 */
	public abstract List<String> getTabComplete(@NotNull CommandSender sender, @NotNull String[] args);

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
