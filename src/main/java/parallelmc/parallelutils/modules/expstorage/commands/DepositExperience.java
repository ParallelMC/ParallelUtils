package parallelmc.parallelutils.modules.expstorage.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.expstorage.ExpConverter;
import parallelmc.parallelutils.modules.expstorage.ExpDatabase;
import parallelmc.parallelutils.modules.expstorage.ExpStorage;

import java.util.logging.Level;

public class DepositExperience implements CommandExecutor {

	private final Parallelutils puPlugin;
	private final ExpDatabase db;

	public DepositExperience(Parallelutils puPlugin, ExpDatabase expDatabase) {
		this.puPlugin = puPlugin;
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
				depositExp(totalExp, uuid, player);
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

				depositExp(requestedExperience, uuid, player);
			}
			return true;
		}
		Parallelutils.log(Level.WARNING, "Tried to deposit experience from non-player command source: " + commandSender.getName());
		return true;
	}

	// This is the wrapper to deposit exp. The process is:
	// Send a "Depositing..." message
	// Run an async task to execute the database code
	// On completion, run the callback
	private void depositExp(int amount, String uuid, Player player) {
		ExpStorage.sendMessageTo(player, "Depositing...");
		depositWithCallback(amount, uuid, player,
				(amount1, player12) -> {
					player12.giveExp(-amount1);
					ExpStorage.sendMessageTo(player12, "Deposited " + amount1 + " experience points!");
				},
				player1 -> ExpStorage.sendMessageTo(player1, "Failed to deposit exp!"));
	}

	private void depositWithCallback(int amount, String uuid, Player player,
	                                 DepositExpSuccessfulCallback successfulCallback, DepositExpFailCallback failCallback) {
		new BukkitRunnable() {
			@Override
			public void run() {
				boolean successful = db.storeExpForPlayer(uuid, amount);

				new BukkitRunnable() {
					@Override
					public void run() {
						if (successful) {
							successfulCallback.successfulDeposit(amount, player);
						} else {
							failCallback.failedDeposit(player);
						}
					}
				}.runTask(puPlugin);
			}
		}.runTaskAsynchronously(puPlugin);
	}

	private interface DepositExpSuccessfulCallback {
		void successfulDeposit(int amount, Player player);
	}

	private interface DepositExpFailCallback {
		void failedDeposit(Player player);
	}
}
