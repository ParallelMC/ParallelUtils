package parallelmc.parallelutils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import parallelmc.parallelutils.Parallelutils;

import java.util.ArrayList;
import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {

	private final Parallelutils plugin;

	public Commands(Parallelutils plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (command.getName().equalsIgnoreCase("parallelutils") || command.getName().equalsIgnoreCase("pu")) {

			if (args.length == 0) {
				//Give version information
			} else {
				switch (args[0]) {
					case "test":
						if (sender.hasPermission("parallelutils.test")) {
							sender.sendMessage("tested");
						} else {
							sender.sendMessage("You do not have permission");
						}
						break;
				}
			}
		}
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		ArrayList<String> list = new ArrayList<>();

		if (command.getName().equalsIgnoreCase("parallelutils") || command.getName().equalsIgnoreCase("pu") && args.length == 1) {
			// List every sub-command
			list.add("test");
		}

		return list;
	}
}
