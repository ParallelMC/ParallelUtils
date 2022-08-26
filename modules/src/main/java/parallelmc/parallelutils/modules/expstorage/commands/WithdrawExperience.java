package parallelmc.parallelutils.modules.expstorage.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.expstorage.ExpDatabase;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import java.util.logging.Level;

public class WithdrawExperience implements CommandExecutor {

	private final ParallelUtils puPlugin;
	private final ExpDatabase db;

	public WithdrawExperience(ParallelUtils puPlugin, ExpDatabase expDatabase) {
		this.puPlugin = puPlugin;
		this.db = expDatabase;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
		if (commandSender instanceof Player player) {
			if (player.getWorld().getBlockAt(player.getLocation()).getType() != Material.ENDER_CHEST) {
				ParallelChat.sendMessageTo(player, "You must be standing on top of an Ender Chest to run this command!");
				return true;
			}
			String uuid = player.getUniqueId().toString();

			int totalExp = db.getExpForPlayer(uuid);
			if (args.length == 0 || totalExp == 0) {
				ParallelChat.sendMessageTo(player, "You have " + totalExp + " experience points available to withdraw.");
				return true;
			}
			String amount = args[0].toLowerCase();

			if (amount.equals("all")) {
				withdrawExp(totalExp, uuid, player);
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
					ParallelChat.sendMessageTo(player, "You cannot withdraw zero or negative experience points!");
					return true;
				}

				if (totalExp < requestedExperience) {
					ParallelChat.sendMessageTo(player, "You do not have enough stored experience points to withdraw " + requestedExperience + " points!");
					return true;
				}

				withdrawExp(requestedExperience, uuid, player);
			}
			return true;
		}

		ParallelUtils.log(Level.WARNING, "Tried to withdraw experience from non-player command source: " + commandSender.getName());
		return true;
	}

	// This is the wrapper to withdraw exp. The process is:
	// Send a "Withdrawing..." message
	// Run an async task to execute the database code
	// On completion, run the callback
	private void withdrawExp(int amount, String uuid, Player player) {
		ParallelChat.sendMessageTo(player, "Withdrawing...");
		depositWithCallback(amount, uuid, player,
				(amount1, player12) -> {
					player12.giveExp(amount1);
					ParallelChat.sendMessageTo(player12, "Withdrew " + amount1 + " experience points!");
				},
				player1 -> ParallelChat.sendMessageTo(player1, "Failed to withdraw exp!"));
	}

	private void depositWithCallback(int amount, String uuid, Player player,
	                                 WithdrawExpSuccessCallback successfulCallback, WithdrawExpFailCallback failCallback) {
		new BukkitRunnable() {
			@Override
			public void run() {
				boolean successful = db.withdrawExpForPlayer(uuid, amount);

				new BukkitRunnable() {
					@Override
					public void run() {
						if (successful) {
							successfulCallback.successfulWithdraw(amount, player);
						} else {
							failCallback.failedWithdraw(player);
						}
					}
				}.runTask(puPlugin);
			}
		}.runTaskAsynchronously(puPlugin);
	}

	private interface WithdrawExpSuccessCallback {
		void successfulWithdraw(int amount, Player player);
	}

	private interface WithdrawExpFailCallback {
		void failedWithdraw(Player player);
	}

}
