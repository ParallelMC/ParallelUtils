package parallelmc.parallelutils.commands;

import org.bukkit.command.Command;
import parallelmc.parallelutils.commands.permissions.ParallelPermission;

public abstract class ParallelParentCommand extends ParallelCommand {

	private Command[] subCommands;

	public ParallelParentCommand(String name, ParallelPermission permission) {
		super(name, permission);
	}
}
