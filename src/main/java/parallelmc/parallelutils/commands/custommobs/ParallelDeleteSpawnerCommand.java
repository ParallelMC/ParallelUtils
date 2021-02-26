package parallelmc.parallelutils.commands.custommobs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.commands.ParallelCommand;
import parallelmc.parallelutils.commands.permissions.ParallelOrPermission;
import parallelmc.parallelutils.commands.permissions.ParallelPermission;
import parallelmc.parallelutils.custommobs.registry.SpawnerRegistry;

public class ParallelDeleteSpawnerCommand extends ParallelCommand {

	private String USAGE = "Usage: /pu deletespawner <uuid> \n /pu deletespawner <x> <y> <z>";

	public ParallelDeleteSpawnerCommand() {
		super("deletespawner", new ParallelOrPermission(new ParallelPermission[]
				{new ParallelPermission("parallelutils.spawn"), new ParallelPermission("parallelutils.spawn.spawners"),
						new ParallelPermission("parallelutils.spawn.spawners.delete")}) );
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {

		if (args.length == 2) {
			// UUID
			Location loc = SpawnerRegistry.getInstance().getSpawnerLoc(args[1]);
			if (loc != null && deleteThings(loc)) {
				sender.sendMessage("Spawner " + args[1] + " deleted");
				return true;
			} else {
				sender.sendMessage("Unable to delete spawner " + args[1] +" \nPlease check UUID and try again");
				return false;
			}
		} else if (args.length == 4) {
			// Location
			String world = Constants.DEFAULT_WORLD;

			if (sender instanceof Player) {
				Player player = (Player)sender;

				world = player.getWorld().getName();
			}

			int x,y,z;

			try {
				x = Integer.parseInt(args[1]);
				y = Integer.parseInt(args[2]);
				z = Integer.parseInt(args[3]);
			} catch (NumberFormatException e) {
				sender.sendMessage("Invalid position.\n");
				sender.sendMessage(USAGE);
				return false;
			}

			Location location = new Location(Bukkit.getWorld(world), x, y, z);

			if(deleteThings(location)) {
				sender.sendMessage("Spawner " + location.toString() + " deleted");
				return true;
			} else {
				sender.sendMessage("Unable to delete spawner " + location.toString() + "\nPlease check location and try again");
				return false;
			}

		} else {
			// Invalid
			sender.sendMessage(USAGE);
		}
		return false;
	}

	private boolean deleteThings(Location loc) {
		if (SpawnerRegistry.getInstance().deleteSpawner(loc)) {
			SpawnerRegistry.getInstance().removeMobCount(loc);

			Bukkit.getScheduler().cancelTask(SpawnerRegistry.getInstance().getSpawnTaskID(loc));
			SpawnerRegistry.getInstance().removeSpawnTaskID(loc);

			if (SpawnerRegistry.getInstance().getLeashTaskID(loc) != null) {
				Bukkit.getScheduler().cancelTask(SpawnerRegistry.getInstance().getLeashTaskID(loc));
				SpawnerRegistry.getInstance().removeLeashTaskID(loc);
				SpawnerRegistry.getInstance().removeSpawnerLeash(loc);
			}

			return true;
		}

		return false;
	}
}
