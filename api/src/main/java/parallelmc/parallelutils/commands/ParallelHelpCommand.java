package parallelmc.parallelutils.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.commands.permissions.ParallelPermission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * A command to display usages of other commands
 * Usage: /pu help <page>
 */
public class ParallelHelpCommand extends ParallelCommand {

	private static final int PAGE_SIZE = 8;

	private final ParallelUtils puPlugin;

	public ParallelHelpCommand() {
		super("help", "Show a list of ParallelUtils commands",
				new ParallelPermission("parallelutils.help"));

		PluginManager manager = Bukkit.getPluginManager();
		Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

		if (plugin == null) {
			ParallelUtils.log(Level.SEVERE, "Unable to initialize ParallelHelpCommand. Plugin " + Constants.PLUGIN_NAME
					+ " does not exist!");
			puPlugin = null;
			return;
		}

		puPlugin = (ParallelUtils) plugin;
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {

		Map<String, ParallelCommand> commands = puPlugin.getCommands();

		List<String> sortedNames = new ArrayList<>(commands.keySet());
		Collections.sort(sortedNames);

		int numPages = (int)Math.ceil((double)commands.size() / (double)PAGE_SIZE);

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

		if (end > commands.size()) {
			end = commands.size();
		}

		TextComponent.Builder builder = Component.text()
				.append(Component.text("--------- ", NamedTextColor.YELLOW))
				.append(Component.text("Help: Index ("))
				.append(Component.text(page))
				.append(Component.text("/"))
				.append(Component.text(numPages))
				.append(Component.text(")"))
				.append(Component.text(" --------------------\n", NamedTextColor.YELLOW));

		for (int i=start; i<end; i++) {
			String name = sortedNames.get(i);
			String helpText = commands.get(name).helpText;

			builder.append(Component.text("/pu " + name, NamedTextColor.GREEN))
					.append(Component.text(": "))
					.append(Component.text(helpText))
					.append(Component.newline());
		}

		if (page != 1) {
			builder.append(Component.text("[Back] ", NamedTextColor.AQUA)
					.clickEvent(ClickEvent.runCommand("/pu help " + (page - 1))));
		}
		if (page != numPages) {
			builder.append(Component.text(" [Forward]", NamedTextColor.AQUA)
					.clickEvent(ClickEvent.runCommand("/pu help " + (page+1))));
		}

		sender.sendMessage(builder.build());

		return true;
	}

	@Override
	public List<String> getTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		ArrayList<String> list = new ArrayList<>();
		if (args.length == 2) {
			list.add("page");
		}

		return list;
	}
}
