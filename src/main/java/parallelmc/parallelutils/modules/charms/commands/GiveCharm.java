package parallelmc.parallelutils.modules.charms.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.commands.ParallelCommand;
import parallelmc.parallelutils.commands.permissions.ParallelPermission;
import parallelmc.parallelutils.modules.charms.ParallelCharms;
import parallelmc.parallelutils.modules.charms.data.Charm;
import parallelmc.parallelutils.modules.charms.data.CharmOptions;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import java.util.HashMap;
import java.util.List;

public class GiveCharm extends ParallelCommand {

	private final String USAGE = "Usage: /pu giveCharm options-name";

	private final ParallelCharms pCharms;
	private final HashMap<String, CharmOptions> options;

	public GiveCharm(ParallelCharms pCharms, HashMap<String, CharmOptions> options) {
		super("giveCharm", new ParallelPermission("parallelutils.charm"));
		this.pCharms = pCharms;
		this.options = options;
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {
		if (sender instanceof Player player) {
			if (args.length < 2) {
				ParallelChat.sendParallelMessageTo(player, USAGE);
				return false;
			}

			String name = args[1];

			if (!options.containsKey(name)) {
				ParallelChat.sendParallelMessageTo(player, "Unknown charm option");
				ParallelChat.sendParallelMessageTo(player, USAGE);
				return false;
			}

			CharmOptions option = options.get(name);

			Charm charm = new Charm(pCharms, option);

			ItemStack item = new ItemStack(Material.NAME_TAG);
			charm.setCharmAppl(item);

			if (player.getInventory().firstEmpty() == -1) {
				player.getWorld().dropItem(player.getLocation(), item);
			} else {
				if (player.getInventory().addItem(item).size() == 0) {
					return true;
				} else {
					ParallelChat.sendParallelMessageTo(player, "Unable to give item");
					return false;
				}
			}

			return true;
		} else {
			sender.sendMessage("Command must be run by a player!");
			return false;
		}
	}

	@Override
	public List<String> getTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		return null;
	}
}
