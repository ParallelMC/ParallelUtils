package parallelmc.parallelutils.modules.custommobs.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.commands.Commands;
import parallelmc.parallelutils.commands.ParallelCommand;
import parallelmc.parallelutils.commands.permissions.ParallelOrPermission;
import parallelmc.parallelutils.commands.permissions.ParallelPermission;
import parallelmc.parallelutils.modules.custommobs.registry.SpawnerRegistry;
import parallelmc.parallelutils.modules.custommobs.spawners.SpawnTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/**
 * A command to create a spawner at a given location
 * Usage: /pu createspawner <mob> <x> <y> <z> [world]
 */
public class ParallelCreateSpawnerCommand extends ParallelCommand {
	public static final String[] SUMMON_MOBS = new String[]{"wisp", "fire_wisp"};

	private final String USAGE = "Usage: /pu createspawner <mob> <x> <y> <z> [world]";

	public ParallelCreateSpawnerCommand() {
		// Requires either the parallelutils.spawn permission, the parallelutils.spawn.spawners permission
		// or the parallelutils.spawn.spawners.create permission
		super("createspawner", new ParallelOrPermission(new ParallelPermission[]
				{new ParallelPermission("parallelutils.spawn"), new ParallelPermission("parallelutils.spawn.spawners"),
						new ParallelPermission("parallelutils.spawn.spawners.create")}));
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {
		// If no arguments are given, show what arguments are valid
		if (args.length <= 1) {
			String options = "Options:\n";
			for (String s : SUMMON_MOBS) {
				options += s + "\n";
			}
			sender.sendMessage(options);
			return true;
		}

		PluginManager manager = Bukkit.getPluginManager();
		JavaPlugin plugin = (JavaPlugin) manager.getPlugin(Constants.PLUGIN_NAME);

		if (plugin == null) {
			Parallelutils.log(Level.SEVERE, "Unable to execute command 'spawnerCreate'. Plugin " + Constants.PLUGIN_NAME + " does not exist!");
			return false;
		}


		// Ensure a mob and coordinates were entered and verify that the mob is valid
		if (args.length < 5 && args.length != 2) {
			if (validMobType(args[1])) {
				sender.sendMessage("Please enter a valid mob type.");
			} else {
				sender.sendMessage("Please enter a full set of coordinates.");
			}
			sender.sendMessage(USAGE);
			return true;
		}

		Location spawnerLocation;

		if (args.length == 2 && sender instanceof Player player) {
			spawnerLocation = player.getLocation().toBlockLocation();
		} else {

			// If this is a player and no world was
			World world = Bukkit.getWorld(Constants.DEFAULT_WORLD);

			if (args.length > 5) {
				world = Bukkit.getWorld(args[5]);
			} else {
				if (sender instanceof Player player) {
					world = player.getWorld();
				}
			}

			// Convert the location from chat notation (+ tildas) to a Location object
			try {
				spawnerLocation = Commands.convertLocation(sender, args[2], args[3], args[4], world);
			} catch (NumberFormatException e) {
				sender.sendMessage("Incorrect coordinate formatting!");
				sender.sendMessage(USAGE);
				return true;
			}
		}

		// Register the spawner and create the spawner task
		switch (args[1]) {
			case "wisp" -> {
				SpawnerRegistry.getInstance().registerSpawner("wisp", spawnerLocation, true);
				BukkitTask wtask = new SpawnTask("wisp", spawnerLocation, 0)
						.runTaskTimer(plugin, 0, SpawnerRegistry.getInstance().
								getSpawnerOptions("wisp").cooldown);
				SpawnerRegistry.getInstance().addSpawnTaskID(spawnerLocation, wtask.getTaskId());
			}
			case "fire_wisp" -> {
				SpawnerRegistry.getInstance().registerSpawner("fire_wisp", spawnerLocation, true);
				BukkitTask fwtask = new SpawnTask("fire_wisp", spawnerLocation, 0)
						.runTaskTimer(plugin, 0, SpawnerRegistry.getInstance().
								getSpawnerOptions("fire_wisp").cooldown);
				SpawnerRegistry.getInstance().addSpawnTaskID(spawnerLocation, fwtask.getTaskId());
			}
		}
		return true;
	}

	@Override
	public List<String> getTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		ArrayList<String> list = new ArrayList<>();

		if (args.length == 2) {
			list.addAll(Arrays.asList(ParallelSummonCommand.SUMMON_MOBS));
		} else if (args.length == 3) {
			if (sender instanceof Player player) {
				list.addAll(Commands.getTargetedBlockTabHelper(player, 3));
			}
		} else if (args.length == 4) {
			if (sender instanceof Player player) {
				list.addAll(Commands.getTargetedBlockTabHelper(player, 2));
			}
		} else if (args.length == 5) {
			if (sender instanceof Player player) {
				list.addAll(Commands.getTargetedBlockTabHelper(player, 1));
			}
		}

		return list;
	}

	/**
	 * Verifies that the given mob is a custom mob that can be spawned by ParallelUtils
	 *
	 * @param type The mob to verify
	 * @return Returns true when the given mob is valid
	 */
	private boolean validMobType(String type) {
		for (String s : SUMMON_MOBS) {
			if (s.equalsIgnoreCase(type)) {
				return true;
			}
		}
		return false;
	}
}

