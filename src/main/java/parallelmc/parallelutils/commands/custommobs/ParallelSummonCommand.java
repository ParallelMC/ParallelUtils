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
import parallelmc.parallelutils.commands.permissions.ParallelPermission;
import parallelmc.parallelutils.commands.permissions.ParallelOrPermission;
import parallelmc.parallelutils.custommobs.nmsmobs.EntityWisp;
import parallelmc.parallelutils.custommobs.nmsmobs.SpawnReason;

import java.util.logging.Level;

public class ParallelSummonCommand extends ParallelCommand {

	public static final String[] SUMMON_MOBS = new String[]{"wisp"};

	public ParallelSummonCommand() {
		super("summon", new ParallelOrPermission(new ParallelPermission[]{new ParallelPermission("parallelutils.spawn"), new ParallelPermission("parallelutils.spawn.summon")}));
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			if (args.length <= 1) {
				String options = "Options:\n";
				for (String s : SUMMON_MOBS) {
					options += s + "\n";
				}
				sender.sendMessage(options);
				return true;
			}

			PluginManager manager = Bukkit.getPluginManager();
			JavaPlugin plugin = (JavaPlugin) manager.getPlugin(Constants.pluginName);

			if (plugin == null) {
				Parallelutils.log(Level.SEVERE, "Unable to execute command 'summon'. Plugin " + Constants.pluginName + " does not exist!");
				return false;
			}

			switch (args[1]) {
				case "wisp":
					EntityWisp wisp = EntityWisp.spawn(plugin, (CraftServer)sender.getServer(), (CraftWorld)player.getWorld(), player.getLocation(), SpawnReason.COMMAND, player.getLocation());
					break;
			}
		} else {
			sender.sendMessage("This command can only be run by a player");
			return false;
		}
		return true;
	}
}
