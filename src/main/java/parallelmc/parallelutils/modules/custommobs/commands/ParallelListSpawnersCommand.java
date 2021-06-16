package parallelmc.parallelutils.modules.custommobs.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.commands.ParallelCommand;
import parallelmc.parallelutils.commands.permissions.ParallelOrPermission;
import parallelmc.parallelutils.commands.permissions.ParallelPermission;
import parallelmc.parallelutils.modules.custommobs.registry.SpawnerRegistry;
import parallelmc.parallelutils.modules.custommobs.spawners.SpawnerData;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * A command to list all spawners currently in the world
 * Usage: /pu listspawners [page]
 */
public class ParallelListSpawnersCommand extends ParallelCommand {

	private static final int PAGE_SIZE = 3;

	private final String USAGE = "Usage: /pu listspawners [page]";

	public ParallelListSpawnersCommand() {
		super("listspawners", new ParallelOrPermission(new ParallelPermission[]
				{new ParallelPermission("parallelutils.spawn"), new ParallelPermission("parallelutils.spawn.spawners"),
						new ParallelPermission("parallelutils.spawn.spawners.list")}));
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {
		SpawnerData[] data = SpawnerRegistry.getInstance().getSpawnerData().toArray(new SpawnerData[0]);

		if (data.length == 0) {
			sender.sendMessage("No Spawners Exist!");
			return true;
		}

		int numPages = (int) Math.ceil((double) data.length / (double) PAGE_SIZE);

		int page = 1;

		if (args.length > 1) {
			page = Integer.parseInt(args[1]);
		}

		if (page > numPages || page <= 0) {
			sender.sendMessage("Invalid page number!");
			sender.sendMessage(USAGE);
			return true;
		}

		int start = (page - 1) * PAGE_SIZE;
		int end = start + PAGE_SIZE;

		if (end > data.length) {
			end = data.length;
		}

		ComponentBuilder componentBuilder = new ComponentBuilder();
		for (int i = start; i < end; i++) {
			try {
				componentBuilder.append("--------------------------------------------\n");
				componentBuilder.append("ID: ").append(data[i].getUuid()).append("\n");
				componentBuilder.append("Type: ").append(data[i].getType()).append("\n");
				Location location = data[i].getLocation();
				componentBuilder.append("World: ").append(location.getWorld().getName()).append("\n");

				String locString = "" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();
				TextComponent locComponent = new TextComponent(locString);
				locComponent.setColor(ChatColor.AQUA);
				locComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " +
						location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ()));
				locComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Teleport")));
				componentBuilder.append("Location: ").append(locComponent).append("\n", ComponentBuilder.FormatRetention.NONE);

				componentBuilder.append("HasLeash: ").append("" + data[i].hasLeash()).append("\n");
			} catch (NullPointerException e) {
				Parallelutils.log(Level.INFO, "NullPointerException, skipping...");
			}
		}
		componentBuilder.append("--------------------------------------------\n");

		if (page != 1) {
			TextComponent backComponent = new TextComponent("[Back] ");
			backComponent.setColor(ChatColor.AQUA);
			backComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pu listspawners " + (page-1)));
			componentBuilder.append(backComponent);
		}
		componentBuilder.append("Page ", ComponentBuilder.FormatRetention.NONE);
		componentBuilder.append("" + page).append("/").append("" + numPages);

		if (page != numPages) {
			TextComponent backComponent = new TextComponent(" [Forward]");
			backComponent.setColor(ChatColor.AQUA);
			backComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pu listspawners " + (page+1)));
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
