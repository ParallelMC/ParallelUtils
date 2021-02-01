package parallelmc.parallelutils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class ParallelParentCommand extends ParallelCommand {

	private Command[] subCommands;

	public ParallelParentCommand(String name, ParallelPermission permission) {
		super(name, permission);
	}
}
