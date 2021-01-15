package parallelmc.parallelutils.commands;

import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.command.ServerCommandSender;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;
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
										//player.getWorld().spawnEntity(player.getLocation(), Parallelutils.mobTypes.getType("wisp"));
										NMSWisp wisp = new NMSWisp(((CraftWorld)player.getWorld()).getHandle());
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
