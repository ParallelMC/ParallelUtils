package parallelmc.parallelutils.modules.charms.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.commands.ParallelCommand;
import parallelmc.parallelutils.commands.permissions.ParallelPermission;
import parallelmc.parallelutils.modules.charms.data.Charm;
import parallelmc.parallelutils.modules.charms.data.CharmOptions;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Deprecated
public class ApplyCharm extends ParallelCommand {

	private final String USAGE = "Usage: /pu applyCharm";

	private final Charm testCharm;

	public ApplyCharm(Charm testCharm) {
		super("applyCharm", "Applies a charm to the held item", new ParallelPermission("parallelutils.charm"));

		this.testCharm = testCharm;
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {

		if (sender instanceof Player player) {
			ItemStack heldItem = player.getInventory().getItemInMainHand();

			boolean result = testCharm.apply(heldItem, player, false, true);

			sender.sendMessage("Result: " + result);
		}

		return false;
	}

	@Override
	public List<String> getTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		return null;
	}
}
