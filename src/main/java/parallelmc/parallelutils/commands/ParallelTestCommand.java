package parallelmc.parallelutils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.commands.permissions.ParallelPermission;

import java.util.ArrayList;
import java.util.List;

/**
 * A command to test the Permissions and Command systems
 */
public class ParallelTestCommand extends ParallelCommand {

	public ParallelTestCommand() {
		super("test", "A simple test command",
				new ParallelPermission("parallelutils.test"));
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {
		if (!hasPermissions(sender)) {
			return false;
		}

		sender.sendMessage("tested");

		return true;
	}

	@Override
	public List<String> getTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		return new ArrayList<>();
	}
}
