package parallelmc.parallelutils.modules.expstorage.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.expstorage.ExpDatabase;
import parallelmc.parallelutils.modules.expstorage.ExpStorage;

import java.util.logging.Level;

public class WithdrawExperience implements CommandExecutor {

	private final ExpDatabase db;

	public WithdrawExperience(ExpDatabase expDatabase) {
		this.db = expDatabase;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
		if (commandSender instanceof Player player) {
			if (player.getWorld().getBlockAt(player.getLocation()).getType() != Material.ENDER_CHEST) {
				ExpStorage.sendMessageTo(player, "You must be standing on top of an Ender Chest to run this command!");
				return true;
			}
			String uuid = player.getUniqueId().toString();

			int totalExp = db.getExpForPlayer(uuid);
			if (args.length == 0 || totalExp == 0) {
				ExpStorage.sendMessageTo(player, "You have " + totalExp + " experience points available to withdraw.");
				return true;
			}
			String amount = args[0].toLowerCase();

			if (amount.equals("all")) {
				player.giveExp(totalExp);
				db.withdrawExpForPlayer(uuid, totalExp);
				ExpStorage.sendMessageTo(player, "Withdrew " + totalExp + " experience points!");
			}
			else {
				int requestedExperience;
				try {
					requestedExperience = Integer.parseInt(amount);
				} catch (NumberFormatException e) {
					// use plugin.yml usage
					return false;
				}

				if (requestedExperience <= 0) {
					ExpStorage.sendMessageTo(player, "You cannot withdraw zero or negative experience points!");
					return true;
				}

				if (totalExp < requestedExperience) {
					ExpStorage.sendMessageTo(player, "You do not have enough stored experience points to withdraw " + requestedExperience + " points!");
					return true;
				}

				player.giveExp(requestedExperience);
				db.withdrawExpForPlayer(uuid, requestedExperience);
				ExpStorage.sendMessageTo(player, "Withdrew " + requestedExperience + " experience points!");
			}
			return true;
		}

		Parallelutils.log(Level.WARNING, "Tried to withdraw experience from non-player command source: " + commandSender.getName());
		return true;
	}

}
