package parallelmc.parallelutils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.commands.permissions.ParallelPermission;

import java.util.List;

/**
 * A command that delays the execution of another command
 * Usage: /pu wait <time> <command>
 */
public class ParallelWaitCommand extends ParallelCommand{

	private static final String USAGE = "Usage: /pu wait <time> <command>";

	private final Parallelutils puPlugin;

	public ParallelWaitCommand(Parallelutils puPlugin) {
		super("wait", new ParallelPermission("parallelutils.admin")); // Only admins should be allowed to use this!

		this.puPlugin = puPlugin;
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {
		if (sender instanceof ConsoleCommandSender || hasPermissions(sender)) {
			if (args.length < 3) {
				sender.sendMessage(USAGE);
				return false;
			}

			String timeStr = args[1];

			try {
				long time = Long.parseLong(timeStr);

				StringBuilder comm = new StringBuilder();

				for (int i=2; i<args.length; i++) {
					comm.append(args[i]);
					comm.append(" ");
				}

				new BukkitRunnable() {
					@Override
					public void run() {
						sender.getServer().dispatchCommand(sender.getServer().getConsoleSender(), comm.toString());
					}
				}.runTaskLater(puPlugin, time);
			} catch (NumberFormatException e) {
				sender.sendMessage(USAGE);
				return false;
			}
		}
		return false;
	}

	@Override
	public List<String> getTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		return null;
	}
}
