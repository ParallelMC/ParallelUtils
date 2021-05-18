package parallelmc.parallelutils.commands.custommobs;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.commands.ParallelCommand;
import parallelmc.parallelutils.commands.permissions.ParallelPermission;

/**
 * A command to display usages of other commands
 * Usage: /pu help <page>
 */
public class ParallelHelpCommand extends ParallelCommand {

	private static final int PAGE_SIZE = 10;

	private static final String[] HELP_MESSAGES = {
			"/pu help <page>",
			"/pu test",
			"/pu summon <mobs> <x> <y> <z>",
			"/pu createspawner <type> <x> <y> <z> [world]",
			"/pu listspawners <page>",
			"/pu deletespawner <uuid>",
			"/pu deletespawner <x> <y> <z> [world]"
	};

	public ParallelHelpCommand() {
		super("help", new ParallelPermission("parallelutils.help"));
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {

		int numPages = (int)Math.ceil((double)HELP_MESSAGES.length / (double)PAGE_SIZE);

		int page = 1;

		if (args.length > 1) {
			page = Integer.parseInt(args[1]);
		}

		if (page > numPages || page <= 0) {
			sender.sendMessage("Invalid page number!");
			return true;
		}

		int start = (page-1)*PAGE_SIZE;
		int end = start+PAGE_SIZE;

		if (end > HELP_MESSAGES.length) {
			end = HELP_MESSAGES.length;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.YELLOW).append("--------- ").append(ChatColor.WHITE).append("Help: Index (")
				.append(page).append("/").append(numPages).append(")")
				.append(ChatColor.YELLOW).append(" --------------------\n").append(ChatColor.RESET);
		for (int i=start; i<end; i++) {
			sb.append(ChatColor.GREEN).append(HELP_MESSAGES[i]).append("\n").append(ChatColor.RESET);
		}

		sender.sendMessage(sb.toString());

		return true;
	}
}
