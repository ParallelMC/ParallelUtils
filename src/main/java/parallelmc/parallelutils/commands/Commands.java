package parallelmc.parallelutils.commands;

import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.craftbukkit.v1_16_R1.CraftServer;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R1.command.ServerCommandSender;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftZombie;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.custommobs.EntityWisp;
import parallelmc.parallelutils.custommobs.NMSWisp;

import java.util.ArrayList;
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
						if (hasPermission(sender, "parallelutils.test")) {
							sender.sendMessage("tested");
						} else {
							sender.sendMessage("You do not have permission");
						}
						break;
					case "world":
						if (hasPermission(sender, "parallelutils.spawn") ||
								hasPermission(sender, "parallelutils.spawn.world")) {
							if(sender instanceof Player){
								Player player = (Player) sender;

								if (args.length <= 1) {
									sender.sendMessage("Options:\n" +
														"wisp");
									break;
								}

								switch (args[1]) {
									case "wisp":
										NMSWisp wisp = new NMSWisp(((CraftWorld)player.getWorld()).getHandle());
										EntityWisp.setupNBT(plugin, (CraftZombie) CraftEntity.getEntity((CraftServer)sender.getServer(), wisp));
										Location l = player.getLocation();
										wisp.setPosition(l.getX(), l.getY(), l.getZ());
										((CraftWorld)player.getWorld()).getHandle().addEntity(wisp, CreatureSpawnEvent.SpawnReason.CUSTOM);
										break;
								}
							}
						} else {
							sender.sendMessage("You do not have permission");
						}
						break;
					case "reset":
						/*Entity mob = Bukkit.getEntity(UUID.fromString("29cff125-d7b0-4a20-a03f-97c0b16149b2"));

						if (mob != null) {
							EntityWisp.setupNBT(plugin, (CraftZombie)mob);

							EntityZombie wisp = ((CraftZombie) mob).getHandle();
							NMSWisp.initPathfinder(wisp);
						} else {
							System.out.println("Mob is null!");
						}*/
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
			sender.sendMessage("You do not have permission");
			return list;
		}

		if (command.getName().equalsIgnoreCase("parallelutils") || command.getName().equalsIgnoreCase("pu") && args.length == 1) {
			// List every sub-command
			list.add("test");
			list.add("world");
		}

		return list;
	}

	private boolean hasPermission(CommandSender sender, String permission) {
		return sender instanceof ServerCommandSender || sender.isOp() || sender.hasPermission(permission);
	}
}
