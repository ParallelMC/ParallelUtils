package parallelmc.parallelutils.commands.custommobs;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.commands.ParallelCommand;
import parallelmc.parallelutils.commands.ParallelPermission;
import parallelmc.parallelutils.commands.permissions.ParallelOrPermission;
import parallelmc.parallelutils.custommobs.nmsmobs.EntityWisp;

import java.util.logging.Level;

public class ParallelWorldCommand extends ParallelCommand {

	public static final String[] WORLD_MOBS = new String[]{"wisp"};

	public ParallelWorldCommand() {
		super("world", new ParallelOrPermission(new ParallelPermission[]{new ParallelPermission("parallelutils.spawn"), new ParallelPermission("parallelutils.spawn.world")}));
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			if (args.length <= 1) {
				sender.sendMessage("Options:\n" +
						"wisp");
				return true;
			}

			PluginManager manager = Bukkit.getPluginManager();
			JavaPlugin plugin = (JavaPlugin) manager.getPlugin(Constants.pluginName);

			if (plugin == null) {
				Parallelutils.log(Level.SEVERE, "Unable to execute command 'world'. Plugin " + Constants.pluginName + " does not exist!");
				return false;
			}

			switch (args[1]) {
				case "wisp":
					EntityWisp wisp = EntityWisp.spawn(plugin, (CraftServer)sender.getServer(), (CraftWorld)player.getWorld(), player.getLocation());
					break;
			}
		} else {
			sender.sendMessage("This command can only be run by a player");
			return false;
		}
		return true;
	}
}
