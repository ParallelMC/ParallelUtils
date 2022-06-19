package parallelmc.parallelutils.modules.charms.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.commands.ParallelCommand;
import parallelmc.parallelutils.commands.permissions.ParallelPermission;

import java.util.List;

public class GiveRemover extends ParallelCommand {

	private final Parallelutils puPlugin;

	public GiveRemover(Parallelutils puPlugin) {
		super("giveRemover", new ParallelPermission("parallelutils.removecharm"));
		this.puPlugin = puPlugin;
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {

		Player player;

		if (args.length < 2) {
			if (sender instanceof Player) {
				player = (Player) sender;
			} else {
				return false;
			}
		} else {
			String playerName = args[1];

			player = sender.getServer().getPlayer(playerName);
		}

		if (player == null) return false;

		ItemStack item = new ItemStack(Material.PAPER);

		ItemMeta meta = item.getItemMeta();

		if (meta == null) return false;

		meta.displayName(Component.text("<italic:false><yellow>Charm Remover"));

		PersistentDataContainer pdc = meta.getPersistentDataContainer();

		pdc.set(new NamespacedKey(puPlugin, "ParallelUtils.CharmRemover"), PersistentDataType.INTEGER, 1);

		item.setItemMeta(meta);

		player.getInventory().addItem(item);

		return true;
	}

	@Override
	public List<String> getTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		return null;
	}
}
