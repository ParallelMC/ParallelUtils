package parallelmc.parallelutils.modules.bitsandbobs.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.util.MessageTools;
import parallelmc.parallelutils.util.MessageType;

public class Hat implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (commandSender instanceof Player player) {
            PlayerInventory inventory = player.getInventory();
            ItemStack heldItem = inventory.getItemInMainHand();
            if (heldItem.getType() == Material.AIR) {
                MessageTools.sendMessage(player, "You aren't holding anything in your hand!", MessageType.ERROR);
                return true;
            }
            // If the player has permission to wear all hats, or they're holding paper/leather horse armor with
            // custom model data, swap their hand and hat slots
            if (player.hasPermission("parallelutils.hat.*") ||
                    ((heldItem.getType() == Material.PAPER || heldItem.getType() == Material.LEATHER_HORSE_ARMOR)
                      && heldItem.getItemMeta().hasCustomModelData())) {

                ItemStack helmetItem = inventory.getHelmet();
                inventory.setItemInMainHand(helmetItem);
                inventory.setHelmet(heldItem);
                MessageTools.sendMessage(player, "Item set as hat!", MessageType.SUCCESS);
            } else {
                MessageTools.sendMessage(player, "You don't have permission to set this item as your hat!", MessageType.ERROR);
            }
            return true;
        } else {
            MessageTools.sendConsoleMessage("You must be a player to run this command!", MessageType.ERROR);
        }
        return true;
    }
}
