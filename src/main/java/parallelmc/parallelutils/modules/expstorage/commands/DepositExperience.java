package parallelmc.parallelutils.modules.expstorage.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.expstorage.ExpConverter;
import parallelmc.parallelutils.modules.expstorage.ExpDatabase;
import parallelmc.parallelutils.modules.expstorage.ExpStorage;

import java.util.logging.Level;

public class DepositExperience implements CommandExecutor {

	private final ExpDatabase db;

	public DepositExperience(ExpDatabase expDatabase) {
		this.db = expDatabase;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
		if (commandSender instanceof Player player) {
			// make sure they are standing on an ender chest
			if (player.getWorld().getBlockAt(player.getLocation()).getType() != Material.ENDER_CHEST) {
				ExpStorage.sendMessageTo(player, "You must be standing on top of an Ender Chest to run this command!");
				return true;
			}
			int totalExp = ExpConverter.getPlayerCurrentExp(player);
			if (args.length == 0) {
				ExpStorage.sendMessageTo(player, "You have " + totalExp + " experience points available to deposit.");
				return true;
			}
			String amount = args[0].toLowerCase();
			String uuid = player.getUniqueId().toString();
			// deposit all experience
			if (amount.equals("all")) {
				// take all of their exp
				player.giveExp(-totalExp);
				db.storeExpForPlayer(uuid, totalExp);
				ExpStorage.sendMessageTo(player, "Deposited " + totalExp + " experience points!");
			}
			// deposit a certain number of experience points
			else {
				int requestedExperience;
				try {
					requestedExperience = Integer.parseInt(amount);
				} catch (NumberFormatException e) {
					// use plugin.yml usage
					return false;
				}

				if (requestedExperience <= 0) {
					ExpStorage.sendMessageTo(player, "You cannot deposit zero or negative experience points!");
					return true;
				}

				if (requestedExperience > totalExp) {
					ExpStorage.sendMessageTo(player, "You do not have enough experience points to deposit " + requestedExperience + " points!");
					return true;
				}

				player.giveExp(-requestedExperience);
				db.storeExpForPlayer(uuid, requestedExperience);
				ExpStorage.sendMessageTo(player, "Deposited " + requestedExperience + " experience points!");
			}
			return true;
		}
		Parallelutils.log(Level.WARNING, "Tried to deposit experience from non-player command source: " + commandSender.getName());
		return true;
	}
}
