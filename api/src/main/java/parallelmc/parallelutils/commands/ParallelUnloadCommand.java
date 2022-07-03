package parallelmc.parallelutils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.commands.permissions.ParallelPermission;

import java.util.List;

public class ParallelUnloadCommand extends ParallelCommand {

	private static final String USAGE = "/pu unload <moduleName>";

	private final ParallelUtils puPlugin;

	public ParallelUnloadCommand(ParallelUtils puPlugin) {
		super("unload", new ParallelPermission("parallelutils.modules.unload"));

		this.puPlugin = puPlugin;
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {
		if (args.length != 2) {
			sender.sendMessage(USAGE);
			return false;
		}

		String moduleName = args[1];

		boolean result = puPlugin.unloadModule(moduleName);

		if (result) {
			sender.sendMessage("Successfully unloaded module");
		} else {
			sender.sendMessage("Failed to unload module. Contact a developer!");
		}

		return result;
	}

	@Override
	public List<String> getTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		return puPlugin.getModules();
	}
}
