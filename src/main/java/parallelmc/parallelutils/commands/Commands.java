package parallelmc.parallelutils.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.craftbukkit.v1_16_R3.command.ServerCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.commands.custommobs.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class implements the Bukkit {@code CommandExecutor} and {@code TabCompleter} and is responsible for
 * handling all Commands for ParallelUtils as well as implementing tab completion.
 */
public class Commands implements CommandExecutor, TabCompleter {

	public Commands() {

	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		// Both `parallelutils` and `pu` are valid command prefixes
		if (command.getName().equalsIgnoreCase("parallelutils") || command.getName().equalsIgnoreCase("pu")) {

			// All users need the "parallelutils.basic" permission
			if (!hasPermission(sender, "parallelutils.basic")) {
				sender.sendMessage("You do not have permission");
				return true;
			}

			// If no command was specified, just give the version
			if (args.length == 0) {
				// Give version information
				sender.sendMessage("ParallelUtils Version " + Constants.VERSION);
			} else {
				switch (args[0]) {
					case "help" -> new ParallelHelpCommand().execute(sender, command, args);
					case "test" -> new ParallelTestCommand().execute(sender, command, args);
					case "summon" -> new ParallelSummonCommand().execute(sender, command, args);
					case "createspawner" -> new ParallelCreateSpawnerCommand().execute(sender, command, args);
					case "listspawners" -> new ParallelListSpawnersCommand().execute(sender, command, args);
					case "deletespawner" -> new ParallelDeleteSpawnerCommand().execute(sender, command, args);
					default -> sender.sendMessage("PU: Command not found");
				}
			}
		}
		return true;
	}

	// TODO: Figure out a way to make this more modular
	@Override
	@Nullable
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		ArrayList<String> list = new ArrayList<>();

		// The CommandSender needs parallelutils.basic to see the tab completions
		if (!hasPermission(sender, "parallelutils.basic")) {
			return list;
		}

		// Show ParallelUtils commands
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
			switch (args[0]) {
				case "summon" -> list.addAll(Arrays.asList(ParallelSummonCommand.SUMMON_MOBS));
				case "createspawner" -> list.addAll(Arrays.asList(ParallelCreateSpawnerCommand.SUMMON_MOBS));
				case "listspawners", "help" -> list.add("1");
				case "deletespawner" -> {
					list.add("uuid");
					if (sender instanceof Player player) {

						Block targetedBlock = player.getTargetBlock(5);

						if (targetedBlock != null && targetedBlock.isSolid()) {
							// Autofill targeted coords
							list.add(String.format("%d", targetedBlock.getX()));
							list.add(String.format("%d %d", targetedBlock.getX(), targetedBlock.getY()));
							list.add(String.format("%d %d %d", targetedBlock.getX(), targetedBlock.getY(), targetedBlock.getZ()));
							list.add(String.format("%d %d %d world", targetedBlock.getX(), targetedBlock.getY(), targetedBlock.getZ()));
						} else {
							Location location = player.getLocation();
							list.add(String.format("%d", location.getBlockX()));
							list.add(String.format("%d %d", location.getBlockX(), location.getBlockY()));
							list.add(String.format("%d %d %d", location.getBlockX(), location.getBlockY(), location.getBlockZ()));
							list.add(String.format("%d %d %d world", location.getBlockX(), location.getBlockY(), location.getBlockZ()));
						}
					}
				}
			}
		} else if (args.length == 3) {
			if (args[0].equals("summon") || args[0].equals("createspawner")) {
				if (sender instanceof Player player) {
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
				if (sender instanceof Player player) {
					Block targetedBlock = player.getTargetBlock(5);

					if (targetedBlock != null && targetedBlock.isSolid()) {
						// Autofill targeted coords
						list.add(String.format("%d", targetedBlock.getY()));
						list.add(String.format("%d %d", targetedBlock.getY(), targetedBlock.getZ()));
						list.add(String.format("%d %d, world", targetedBlock.getY(), targetedBlock.getZ()));
					} else {
						Location location = player.getLocation();
						list.add(String.format("%d", location.getBlockY()));
						list.add(String.format("%d %d", location.getBlockY(), location.getBlockZ()));
						list.add(String.format("%d %d world", location.getBlockY(), location.getBlockZ()));
					}
				}
			}
		} else if (args.length == 4) {
			if (args[0].equals("summon") || args[0].equals("createspawner")) {
				if (sender instanceof Player player) {
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
				if (sender instanceof Player player) {
					Block targetedBlock = player.getTargetBlock(5);

					if (targetedBlock != null && targetedBlock.isSolid()) {
						// Autofill targeted coords
						list.add(String.format("%d", targetedBlock.getZ()));
						list.add(String.format("%d world", targetedBlock.getZ()));
					} else {
						Location location = player.getLocation();
						list.add(String.format("%d", location.getBlockZ()));
						list.add(String.format("%d world", location.getBlockZ()));
					}
				}
			}
		} else if (args.length == 5) {
			if (args[0].equals("summon") || args[0].equals("createspawner")) {
				if (sender instanceof Player player) {
					Block targetedBlock = player.getTargetBlock(5);

					if (targetedBlock != null && targetedBlock.isSolid()) {
						// Autofill targeted coords
						list.add(String.format("%d", targetedBlock.getZ()));
					} else {
						// Autofill tildas
						list.add("~");
					}
				}
			} else if (args[0].equals("deletespawner")) {
				if (sender instanceof Player) {
					list.add("world");
				}
			}
		}

		return list;
	}

	/**
	 * Checks if the CommandSender is the server, if the sender is an op, or if the sender has the requested permission
	 * @param sender The sender to check
	 * @param permission The permission to check the sender has
	 * @return Return true if the sender has permission
	 */
	public static boolean hasPermission(CommandSender sender, String permission) {
		return sender instanceof ServerCommandSender || sender.isOp() || sender.hasPermission(permission);
	}

	/**
	 * Converts a location from command syntax (with tildas) to a Location object.
	 * @param sender The sender requesting this conversion (sets world to the sender's world if {@code world} is null)
	 * @param sx The x location param
	 * @param sy The y location param
	 * @param sz The z location param
	 * @param world The World location param
	 * @return The constructed Location object
	 * @throws NumberFormatException Thrown if x, y, or z is not a valid integer
	 */
	public static Location convertLocation(CommandSender sender, String sx, String sy, String sz, World world) throws NumberFormatException {
		int x, y, z;

		Location defaultLoc = new Location(world, 0, 0, 0);

		if (sender instanceof Player player) {
			defaultLoc = player.getLocation();
		}

		if (sx.trim().startsWith("~")) {
			if (sx.trim().length() == 1) {
				x = defaultLoc.getBlockX();
			} else {
				x = defaultLoc.getBlockX() + Integer.parseInt(sx.trim().substring(1));
			}
		} else {
			x = Integer.parseInt(sx);
		}
		if (sy.trim().startsWith("~")) {
			if (sy.trim().length() == 1) {
				y = defaultLoc.getBlockY();
			} else {
				y = defaultLoc.getBlockY() + Integer.parseInt(sy.trim().substring(1));
			}
		} else {
			y = Integer.parseInt(sy);
		}
		if (sz.trim().startsWith("~")) {
			if (sz.trim().length() == 1) {
				z = defaultLoc.getBlockZ();
			} else {
				z = defaultLoc.getBlockZ() + Integer.parseInt(sz.trim().substring(1));
			}
		} else {
			z = Integer.parseInt(sz);
		}

		World finalWorld = world;

		if (world == null) {
			if (sender instanceof Player player) {
				finalWorld = player.getWorld();
			} else {
				finalWorld = Bukkit.getWorld(Constants.DEFAULT_WORLD);
			}
		}

		return new Location(finalWorld, x, y, z);
	}

	/**
	 * Converts a location from command syntax (with tildas) to a Location object.
	 * @param sender The sender requesting this conversion (sets world to the sender's world if sender is a player)
	 * @param sx The x location param
	 * @param sy The y location param
	 * @param sz The z location param
	 * @return The constructed Location object
	 * @throws NumberFormatException Thrown if x, y, or z is not a valid integer
	 */
	public static Location convertLocation(CommandSender sender, String sx, String sy, String sz) throws NumberFormatException{
		if (sender instanceof Player player) {
			Location playerLoc = player.getLocation();

			World world = playerLoc.getWorld();

			return convertLocation(sender, sx, sy, sz, world);
		} else {
			return convertLocation(sender, sx, sy, sz, Bukkit.getWorld(Constants.DEFAULT_WORLD));
		}
	}
}
