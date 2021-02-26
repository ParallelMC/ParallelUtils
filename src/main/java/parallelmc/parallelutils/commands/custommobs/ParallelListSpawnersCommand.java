package parallelmc.parallelutils.commands.custommobs;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.commands.ParallelCommand;
import parallelmc.parallelutils.commands.permissions.ParallelOrPermission;
import parallelmc.parallelutils.commands.permissions.ParallelPermission;
import parallelmc.parallelutils.custommobs.registry.SpawnerRegistry;
import parallelmc.parallelutils.custommobs.spawners.SpawnerData;

public class ParallelListSpawnersCommand extends ParallelCommand {

	private static final int PAGE_SIZE = 10;

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

		int numPages = (int)Math.ceil((double)data.length / (double)PAGE_SIZE);

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

		if (end > data.length) {
			end = data.length;
		}

		StringBuilder sb = new StringBuilder();
		for (int i=start; i<end; i++) {
			sb.append("--------------------------------------------\n");
			sb.append("ID: ").append(data[i].getUuid()).append("\n");
			sb.append("Type: ").append(data[i].getType()).append("\n");
			Location location = data[i].getLocation();
			sb.append("World: ").append(location.getWorld().getName()).append("\n");
			sb.append("Location: ").append(location.getBlockX())
					.append(" ").append(location.getBlockY())
					.append(" ").append(location.getBlockZ()).append("\n");
			sb.append("HasLeash: ").append(data[i].hasLeash()).append("\n");
		}
		sb.append("--------------------------------------------\n");
		sb.append("Page ").append(page).append("/").append(numPages);

		sender.sendMessage(sb.toString());

		return true;
	}
}
