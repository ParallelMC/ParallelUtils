package parallelmc.parallelutils.commands;

import parallelmc.parallelutils.commands.permissions.ParallelPermission;

/**
 * An abstract class to execute multiple sub-ParallelCommands
 */
public abstract class ParallelParentCommand extends ParallelCommand {

	private ParallelCommand[] subCommands;

	/**
	 * Create a new ParentCommand with the given name and permission
	 * @param name The name of the new command
	 * @param permission The permission associated with this command
	 */
	public ParallelParentCommand(String name, ParallelPermission permission) {
		super(name, permission);
	}
}
