package parallelmc.parallelutils.modules.charms.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.commands.ParallelCommand;
import parallelmc.parallelutils.commands.permissions.ParallelPermission;
import parallelmc.parallelutils.modules.charms.ParallelCharms;

import java.util.List;

public class ReloadCharms extends ParallelCommand {

	private final ParallelCharms charms;

	public ReloadCharms(ParallelCharms charms) {
		super("reloadCharms", new ParallelPermission("parallelutils.charm.reload"));
		this.charms = charms;
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {
		try {
			charms.resetCharms();
		} catch (Exception e) {
			e.printStackTrace();
			sender.sendMessage("Unable to reset charms! Contact a sys admin and developer.");
			return false;
		}

		sender.sendMessage("Charms reset!");
		return true;
	}

	@Override
	public List<String> getTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		return null;
	}
}
