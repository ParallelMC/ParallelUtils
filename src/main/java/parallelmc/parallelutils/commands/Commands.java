package parallelmc.parallelutils.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.craftbukkit.v1_17_R1.command.ServerCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import parallelmc.parallelutils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class implements the Bukkit {@code CommandExecutor} and {@code TabCompleter} and is responsible for
 * handling all Commands for ParallelUtils as well as implementing tab completion.
 */
public class Commands implements CommandExecutor, TabCompleter {

	private final HashMap<String, ParallelCommand> commandMap;

	public Commands() {
		commandMap = new HashMap<>();
	}

	/**
	 * Adds a new command to the commandmap
	 *
	 * @param name    The name of the command
	 * @param command The command to be run when the name is called
	 * @return Returns true when the command was added successfully, false if the command already exists.
	 */
	public boolean addCommand(String name, ParallelCommand command) {
		if (commandMap.containsKey(name.toLowerCase().strip())) {
			return false;
		}

		commandMap.put(name.toLowerCase().strip(), command);

		return true;
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
				ParallelCommand executingCommand = commandMap.get(args[0]);

				if (executingCommand != null) {
					executingCommand.execute(sender, command, args);
				} else {
					sender.sendMessage("ParallelUtils: Command not found");
				}
			}
		}
		return true;
	}

	@Override
	@Nullable
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		ArrayList<String> list = new ArrayList<>();

		// The CommandSender needs parallelutils.basic to see the tab completions
		if (!hasPermission(sender, "parallelutils.basic")) {
			return list;
		}

		// Show ParallelUtils commands

		String lowerName = command.getName().toLowerCase().strip();

		if (lowerName.equals("parallelutils") || lowerName.equals("pu") && args.length == 1) {
			// List every sub-command
			list.addAll(commandMap.keySet());
		} else {
			if (commandMap.containsKey(args[0].toLowerCase().strip())) {
				return commandMap.get(args[0].toLowerCase().strip()).getTabComplete(sender, args);
			}
		}
		return list;
	}

	/**
	 * Return a deep copy of the command map. Modifying the returned map will not modify the commands
	 * @return A deep copy of the command map
	 */
	public Map<String, ParallelCommand> getCommands() {
		return commandMap.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	/**
	 * Checks if the CommandSender is the server, if the sender is an op, or if the sender has the requested permission
	 *
	 * @param sender     The sender to check
	 * @param permission The permission to check the sender has
	 * @return Return true if the sender has permission
	 */
	public static boolean hasPermission(CommandSender sender, String permission) {
		return sender instanceof ServerCommandSender || sender.isOp() || sender.hasPermission(permission);
	}

	/**
	 * Converts a location from command syntax (with tildas) to a Location object.
	 *
	 * @param sender The sender requesting this conversion (sets world to the sender's world if {@code world} is null)
	 * @param sx     The x location param
	 * @param sy     The y location param
	 * @param sz     The z location param
	 * @param world  The World location param
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
	 *
	 * @param sender The sender requesting this conversion (sets world to the sender's world if sender is a player)
	 * @param sx     The x location param
	 * @param sy     The y location param
	 * @param sz     The z location param
	 * @return The constructed Location object
	 * @throws NumberFormatException Thrown if x, y, or z is not a valid integer
	 */
	public static Location convertLocation(CommandSender sender, String sx, String sy, String sz) throws NumberFormatException {
		if (sender instanceof Player player) {
			Location playerLoc = player.getLocation();

			World world = playerLoc.getWorld();

			return convertLocation(sender, sx, sy, sz, world);
		} else {
			return convertLocation(sender, sx, sy, sz, Bukkit.getWorld(Constants.DEFAULT_WORLD));
		}
	}

	/**
	 * Returns a tab complete list for a targeted block
	 *
	 * @param player The player of the command to extract targeted block
	 * @param depth  The depth of command positions. If depth==1, format will be similar to "~", if depth==2, format will be similar to "~ ~", and similar for depth==3.
	 * @return The List of the targeted block tab helper
	 */
	public static List<String> getTargetedBlockTabHelper(@NotNull Player player, int depth) {
		ArrayList<String> list = new ArrayList<>();
		Block targetedBlock = player.getTargetBlock(5);

		if (targetedBlock != null && targetedBlock.isSolid()) {
			// Autofill targeted coords
			if (depth >= 1) {
				list.add(String.format("%d", targetedBlock.getZ()));
			}
			if (depth >= 2) {
				list.add(String.format("%d %d", targetedBlock.getY(), targetedBlock.getZ()));
			}
			if (depth >= 3) {
				list.add(String.format("%d %d %d", targetedBlock.getX(), targetedBlock.getY(), targetedBlock.getZ()));
			}
		} else {
			// Autofill tildas
			if (depth >= 1) {
				list.add("~");
			}
			if (depth >= 2) {
				list.add("~ ~");
			}
			if (depth >= 3) {
				list.add("~ ~ ~");
			}
		}
		return list;
	}
}
