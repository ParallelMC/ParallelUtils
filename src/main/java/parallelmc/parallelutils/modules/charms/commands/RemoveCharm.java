package parallelmc.parallelutils.modules.charms.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.commands.ParallelCommand;
import parallelmc.parallelutils.commands.permissions.ParallelPermission;
import parallelmc.parallelutils.modules.charms.data.Charm;

import java.util.List;

public class RemoveCharm extends ParallelCommand {

	private final String USAGE = "Usage: /pu removeCharm";

	private final Charm testCharm;

	public RemoveCharm(Charm testCharm) {
		super("removeCharm", "Removes a charm from the held item", new ParallelPermission("parallelutils.charm"));

		this.testCharm = testCharm;
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {

		if (sender instanceof Player player) {
			ItemStack heldItem = player.getInventory().getItemInMainHand();

			boolean result = testCharm.takeOff(heldItem);

			sender.sendMessage("Result: " + result);
		}

		return false;
	}

	@Override
	public List<String> getTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		return null;
	}
}
