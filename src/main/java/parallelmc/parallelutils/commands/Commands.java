package parallelmc.parallelutils.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.craftbukkit.v1_16_R3.command.ServerCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.commands.custommobs.*;

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
				// Give version information
				sender.sendMessage("ParallelUtils Version " + Constants.VERSION);
			} else {
				switch (args[0]) {
					case "help":
						new ParallelHelpCommand().execute(sender, command, args);
						break;
					case "test":
						new ParallelTestCommand().execute(sender, command, args);
						break;
					case "summon":
						new ParallelSummonCommand().execute(sender, command, args);
						break;
					case "createspawner":
						new ParallelCreateSpawnerCommand().execute(sender, command, args);
						break;
					case "listspawners":
						new ParallelListSpawnersCommand().execute(sender, command, args);
						break;
					case "deletespawner":
						new ParallelDeleteSpawnerCommand().execute(sender, command, args);
						break;
					default:
						sender.sendMessage("PU: Command not found");
				}
			}
		}
		return true;
	}

	// TODO: Figure out a way to make this more modular
	@Override
	public @Nullable
	List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		ArrayList<String> list = new ArrayList<>();

		if (!hasPermission(sender, "parallelutils.basic")) {
			return list;
		}

		if (command.getName().equalsIgnoreCase("parallelutils") || command.getName().equalsIgnoreCase("pu") && args.length == 1) {
			// List every sub-command
			list.add("help");
			list.add("test");
			list.add("summon");
			list.add("createspawner");
			list.add("listspawners");
			list.add("deletespawner");
		}

		if (args.length == 2) {
			if (args[0].equals("summon")) {
				list.addAll(Arrays.asList(ParallelSummonCommand.SUMMON_MOBS));
			} else if (args[0].equals("createspawner")) {
				list.addAll(Arrays.asList(ParallelCreateSpawnerCommand.SUMMON_MOBS));
			} else if (args[0].equals("listspawners") || args[0].equals("help")) {
				list.add("1");
			} else if (args[0].equals("deletespawner")) {
				list.add("uuid");

				if (sender instanceof Player) {
					Player player = (Player) sender;

					Block targetedBlock = player.getTargetBlock(5);

					if (targetedBlock != null && targetedBlock.isSolid()) {
						// Autofill targeted coords
						list.add(String.format("%d", targetedBlock.getX()));
						list.add(String.format("%d %d", targetedBlock.getX(), targetedBlock.getY()));
						list.add(String.format("%d %d %d", targetedBlock.getX(), targetedBlock.getY(), targetedBlock.getZ()));
					} else {
						Location location = player.getLocation();
						list.add(String.format("%d", location.getBlockX()));
						list.add(String.format("%d %d", location.getBlockX(), location.getBlockY()));
						list.add(String.format("%d %d %d", location.getBlockX(), location.getBlockY(), location.getBlockZ()));
					}
				}
			}
		} else if (args.length == 3) {
			if (args[0].equals("summon") || args[0].equals("createspawner")) {
				if (sender instanceof Player) {
					Player player = (Player) sender;

					Block targetedBlock = player.getTargetBlock(5);

					if (targetedBlock != null && targetedBlock.isSolid()) {
						// Autofill targeted coords
						list.add(String.format("%d", targetedBlock.getX()));
						list.add(String.format("%d %d", targetedBlock.getX(), targetedBlock.getY()));
						list.add(String.format("%d %d %d", targetedBlock.getX(), targetedBlock.getY(), targetedBlock.getZ()));
					} else {
						// Autofill tildas
						list.add("~");
						list.add("~ ~");
						list.add("~ ~ ~");
					}
				}
			} else if (args[0].equals("deletespawner")) {
				if (sender instanceof Player) {
					Player player = (Player) sender;

					Block targetedBlock = player.getTargetBlock(5);

					if (targetedBlock != null && targetedBlock.isSolid()) {
						// Autofill targeted coords
						list.add(String.format("%d", targetedBlock.getY()));
						list.add(String.format("%d %d", targetedBlock.getY(), targetedBlock.getZ()));
					} else {
						Location location = player.getLocation();
						list.add(String.format("%d", location.getBlockY()));
						list.add(String.format("%d %d", location.getBlockY(), location.getBlockZ()));
					}
				}
			}
		} else if (args.length == 4) {
			if (args[0].equals("summon") || args[0].equals("createspawner")) {
				if (sender instanceof Player) {
					Player player = (Player) sender;

					Block targetedBlock = player.getTargetBlock(5);

					if (targetedBlock != null && targetedBlock.isSolid()) {
						// Autofill targeted coords
						list.add(String.format("%d", targetedBlock.getY()));
						list.add(String.format("%d %d", targetedBlock.getY(), targetedBlock.getZ()));
					} else {
						// Autofill tildas
						list.add("~");
						list.add("~ ~");
					}
				}
			} else if (args[0].equals("deletespawner")) {
				if (sender instanceof Player) {
					Player player = (Player) sender;

					Block targetedBlock = player.getTargetBlock(5);

					if (targetedBlock != null && targetedBlock.isSolid()) {
						// Autofill targeted coords
						list.add(String.format("%d", targetedBlock.getZ()));
					} else {
						Location location = player.getLocation();
						list.add(String.format("%d", location.getBlockZ()));
					}
				}
			}
		} else if (args.length == 5) {
			if (args[0].equals("summon") || args[0].equals("createspawner")) {
				if (sender instanceof Player) {
					Player player = (Player) sender;

					Block targetedBlock = player.getTargetBlock(5);

					if (targetedBlock != null && targetedBlock.isSolid()) {
						// Autofill targeted coords
						list.add(String.format("%d", targetedBlock.getZ()));
					} else {
						// Autofill tildas
						list.add("~");
					}
				}
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
				if (sx.trim().length() == 1) {
					x = playerLoc.getBlockX();
				} else {
					x = playerLoc.getBlockX() + Integer.parseInt(sx.trim().substring(1));
				}
			} else {
				x = Integer.parseInt(sx);
			}
			if (sy.trim().startsWith("~")) {
				if (sy.trim().length() == 1) {
					y = playerLoc.getBlockY();
				} else {
					y = playerLoc.getBlockY() + Integer.parseInt(sy.trim().substring(1));
				}
			} else {
				y = Integer.parseInt(sy);
			}
			if (sz.trim().startsWith("~")) {
				if (sz.trim().length() == 1) {
					z = playerLoc.getBlockZ();
				} else {
					z = playerLoc.getBlockZ() + Integer.parseInt(sz.trim().substring(1));
				}
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
