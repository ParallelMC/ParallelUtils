package parallelmc.parallelutils.modules.performanceTools.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.commands.ParallelCommand;
import parallelmc.parallelutils.commands.permissions.ParallelOrPermission;
import parallelmc.parallelutils.commands.permissions.ParallelPermission;

import java.util.ArrayList;
import java.util.List;

/**
 * A command that lists all loaded chunks
 * Usage: /pu loadedChunks
 * /pu loadedChunks world
 * /pu loadedChunks world x z radius
 */
public class FindLoadedChunksCommand extends ParallelCommand {

	public FindLoadedChunksCommand() {
		super("loadedChunks", new ParallelOrPermission(new ParallelPermission[] {
				new ParallelPermission("parallelutils.performance"),
				new ParallelPermission("parallelutils.performance.chunks")
		}));
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {

		List<World> worlds = sender.getServer().getWorlds();

		TextComponent.Builder component = Component.text().append(Component.text("Loaded Chunks", NamedTextColor.AQUA));

		component.append(Component.newline());

		if (args.length == 2) {
			// /pu loadedchunks world
			World world = sender.getServer().getWorld(args[1]);

			if (world == null) {
				sender.sendMessage("World not found!");
				return false;
			}

			worlds = new ArrayList<>();
			worlds.add(world);
		} else if (args.length == 5) {
			// /pu loadedchunks world x z radius
			World world = sender.getServer().getWorld(args[1]);

			if (world == null) {
				sender.sendMessage("World not found!");
				return false;
			}

			try {
				double x = Integer.parseInt(args[2])/16.0; // Convert to chunk coords
				double z = Integer.parseInt(args[3])/16.0;
				double radius = Integer.parseInt(args[4])/16.0;

				Chunk[] chunks = world.getLoadedChunks();

				int count = 0;

				for (Chunk c : chunks) {
					int cx = c.getX();
					int cz = c.getZ();

					if (Math.abs(cx - x) < radius && Math.abs(cz - z) < radius) {
						component.append(Component.text(world.getName() + ", " + cx + ", " + cz)).append(Component.newline());
						count++;
					}
				}

				component.append(Component.text("Total: " + count)).append(Component.newline());

				sender.sendMessage(component.build());

				return true;
			} catch (NumberFormatException e) {
				sender.sendMessage("Invalid coordinates or radius!");
			}
		}

		int count = 0;
		for (World w : worlds) {
			Chunk[] chunks = w.getLoadedChunks();
			count += chunks.length;

			for (Chunk c : chunks) {
				int x = c.getX();
				int z = c.getZ();
				String world = c.getWorld().getName();

				component.append(Component.text(world + ", " + x + ", " + z)).append(Component.newline());
			}
		}

		component.append(Component.text("Total: " + count)).append(Component.newline());

		sender.sendMessage(component.build());

		return true;
	}

	@Override
	public List<String> getTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {

		ArrayList<String> tabComplete = new ArrayList<>();

		if (args.length == 1) {
			tabComplete.add("world");
		} else if (args.length == 2) {
			tabComplete.add("x");
		} else if (args.length == 3) {
			tabComplete.add("z");
		} else if (args.length == 4) {
			tabComplete.add("radius");
		}

		return tabComplete;
	}
}
