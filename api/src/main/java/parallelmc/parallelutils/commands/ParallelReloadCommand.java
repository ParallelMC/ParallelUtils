package parallelmc.parallelutils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.commands.permissions.ParallelPermission;

import java.util.ArrayList;
import java.util.List;

public class ParallelReloadCommand extends ParallelCommand {

	private static final String USAGE = "/pu reload <moduleName>";

	private final ParallelUtils puPlugin;

	public ParallelReloadCommand(ParallelUtils puPlugin) {
		super("reload", new ParallelPermission("parallelutils.modules.reload"));
		this.puPlugin = puPlugin;
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {
		if (args.length != 2) {
			sender.sendMessage(USAGE);
			return false;
		}

		String moduleName = args[1];

		if (puPlugin.isLoaded(moduleName)) {
			boolean result = puPlugin.unloadModule(moduleName);

			if (result) {
				sender.sendMessage("Successfully unloaded module");
			} else {
				sender.sendMessage("Failed to unload module. Contact a developer!");
			}
		}

		ParallelModule module = puPlugin.loadModule(moduleName);

		if (module != null) {
			sender.sendMessage("Successfully loaded module");
			return true;
		} else {
			sender.sendMessage("Failed to load module. Contact a developer!");
			return false;
		}
	}

	@Override
	public List<String> getTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		List<String> availableModules = new ArrayList<>(puPlugin.getModules());

		availableModules.add("moduleFile");

		return availableModules;
	}
}
