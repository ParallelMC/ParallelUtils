package parallelmc.parallelutils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ParallelTestCommand extends ParallelCommand {

	public ParallelTestCommand() {
		super("test", new ParallelPermission("parallelutils.test"));
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {
		if (!hasPermissions(sender)) {
			return false;
		}

		sender.sendMessage("tested");

		return true;
	}
}
