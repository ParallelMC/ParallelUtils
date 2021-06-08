package parallelmc.parallelutils.commands.custommobs;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.commands.ParallelCommand;
import parallelmc.parallelutils.commands.permissions.ParallelOrPermission;
import parallelmc.parallelutils.commands.permissions.ParallelPermission;
import parallelmc.parallelutils.custommobs.registry.SpawnerRegistry;
import parallelmc.parallelutils.custommobs.spawners.SpawnerData;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * A command to list all spawners currently in the world
 * Usage: /pu listspawners [page]
 */
public class ParallelListSpawnersCommand extends ParallelCommand {

	private static final int PAGE_SIZE = 10;

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

		StringBuilder sb = new StringBuilder();
		for (int i = start; i < end; i++) {
			try {
				sb.append("--------------------------------------------\n");
				sb.append("ID: ").append(data[i].getUuid()).append("\n");
				sb.append("Type: ").append(data[i].getType()).append("\n");
				Location location = data[i].getLocation();
				sb.append("World: ").append(location.getWorld().getName()).append("\n");
				sb.append("Location: ").append(location.getBlockX())
						.append(" ").append(location.getBlockY())
						.append(" ").append(location.getBlockZ()).append("\n");
				sb.append("HasLeash: ").append(data[i].hasLeash()).append("\n");
			} catch (NullPointerException e) {
				Parallelutils.log(Level.INFO, "NullPointerException, skipping");
			}
		}
		sb.append("--------------------------------------------\n");
		sb.append("Page ").append(page).append("/").append(numPages);


		sender.sendMessage(sb.toString());

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
