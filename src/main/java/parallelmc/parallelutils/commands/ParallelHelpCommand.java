package parallelmc.parallelutils.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;
import parallelmc.parallelutils.commands.permissions.ParallelPermission;

import java.util.ArrayList;
import java.util.List;

// TODO: Make this dynamic
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

		// TODO: Is there a better way to do this? It's so messy
		ComponentBuilder componentBuilder = new ComponentBuilder();

		TextComponent smallYellow = new TextComponent("--------- ");
		smallYellow.setColor(net.md_5.bungee.api.ChatColor.YELLOW);

		TextComponent largeYellow = new TextComponent(" --------------------\n");
		largeYellow.setColor(net.md_5.bungee.api.ChatColor.YELLOW);

		componentBuilder.append(smallYellow, ComponentBuilder.FormatRetention.NONE)
				.append("Help: Index (", ComponentBuilder.FormatRetention.NONE)
				.append("" + page).append("/").append("" + numPages).append(")")
				.append(largeYellow, ComponentBuilder.FormatRetention.NONE);
		for (int i=start; i<end; i++) {
			TextComponent helpComponent = new TextComponent(HELP_MESSAGES[i]);
			helpComponent.setColor(net.md_5.bungee.api.ChatColor.GREEN);
			componentBuilder.append(helpComponent, ComponentBuilder.FormatRetention.NONE)
					.append("\n", ComponentBuilder.FormatRetention.NONE);
		}

		if (page != 1) {
			TextComponent backComponent = new TextComponent("[Back] ");
			backComponent.setColor(net.md_5.bungee.api.ChatColor.AQUA);
			backComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pu help " + (page-1)));
			componentBuilder.append(backComponent);
		}
		if (page != numPages) {
			TextComponent backComponent = new TextComponent(" [Forward]");
			backComponent.setColor(net.md_5.bungee.api.ChatColor.AQUA);
			backComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pu help " + (page+1)));
			componentBuilder.append(backComponent);
			TextComponent resetLocColor = new TextComponent();
			resetLocColor.setColor(ChatColor.RESET);
			componentBuilder.append(resetLocColor, ComponentBuilder.FormatRetention.NONE);
		}

		sender.sendMessage(componentBuilder.create());

		return true;
	}

	@Override
	public List<String> getTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		ArrayList<String> list = new ArrayList<>();
		if (args.length == 2) {
			list.add("1");
		}

		return list;
	}
}
