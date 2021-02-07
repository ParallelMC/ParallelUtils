package parallelmc.parallelutils.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.craftbukkit.v1_16_R3.command.ServerCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.commands.custommobs.ParallelSpawnerCreateCommand;
import parallelmc.parallelutils.commands.custommobs.ParallelSummonCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {

	private final Parallelutils plugin;

	public Commands(Parallelutils plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (command.getName().equalsIgnoreCase("parallelutils") || command.getName().equalsIgnoreCase("pu")) {
			if (!hasPermission(sender, "parallelutils.basic")) {
				sender.sendMessage("You do not have permission");
				return true;
			}
			if (args.length == 0) {
				//Give version information
			} else {
				switch (args[0]) {
					case "test":
						new ParallelTestCommand().execute(sender, command, args);
						break;
					case "summon":
						new ParallelSummonCommand().execute(sender, command, args);
						break;
					case "spawnerCreate":
						new ParallelSpawnerCreateCommand().execute(sender,command,args);
						break;
					default:
						sender.sendMessage("PU: Command not found");
				}
			}
		}
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		ArrayList<String> list = new ArrayList<>();

		if (!hasPermission(sender, "parallelutils.basic")) {
			return list;
		}

		if (command.getName().equalsIgnoreCase("parallelutils") || command.getName().equalsIgnoreCase("pu") && args.length == 1) {
			// List every sub-command
			list.add("test");
			list.add("summon");
			list.add("spawnerCreate");
		}

		if (args.length == 2) {
			if (args[0].equals("summon")) {
				list.addAll(Arrays.asList(ParallelSummonCommand.SUMMON_MOBS));
			} else if (args[0].equals("spawnerCreate")) {
				list.addAll(Arrays.asList(ParallelSpawnerCreateCommand.SUMMON_MOBS));
			}
		}

		return list;
	}

	public static boolean hasPermission(CommandSender sender, String permission) {
		return sender instanceof ServerCommandSender || sender.isOp() || sender.hasPermission(permission);
	}

	public static Location convertLocation(CommandSender sender, String sx, String sy, String sz) {
		int x, y, z;

		World world;

		if (sender instanceof Player) {
			Player player = (Player) sender;

			Location playerLoc = player.getLocation();

			world = playerLoc.getWorld();

			if (sx.trim().startsWith("~")) {
				x = playerLoc.getBlockX() + Integer.parseInt(sx.trim().substring(1));
			} else {
				x = Integer.parseInt(sx);
			}
			if (sy.trim().startsWith("~")) {
				y = playerLoc.getBlockY() + Integer.parseInt(sy.trim().substring(1));
			} else {
				y = Integer.parseInt(sy);
			}
			if (sz.trim().startsWith("~")) {
				z = playerLoc.getBlockZ() + Integer.parseInt(sz.trim().substring(1));
			} else {
				z = Integer.parseInt(sz);
			}
		} else {
			world = null;
			x = Integer.parseInt(sx);
			y = Integer.parseInt(sy);
			z = Integer.parseInt(sz);
		}

		return new Location(world, x, y, z);
	}
}
