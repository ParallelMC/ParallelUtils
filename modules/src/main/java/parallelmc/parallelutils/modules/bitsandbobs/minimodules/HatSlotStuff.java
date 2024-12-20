package parallelmc.parallelutils.modules.bitsandbobs.minimodules;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import parallelmc.parallelutils.util.BukkitTools;
import parallelmc.parallelutils.util.MessageTools;
import parallelmc.parallelutils.util.MessageType;

public class HatSlotStuff implements Listener {

    Plugin plugin;

    public HatSlotStuff() {
        plugin = BukkitTools.getPlugin();
    }

    @EventHandler
    public void onHelmetSlotClick(InventoryClickEvent event) {
        // Make sure the player is clicking on the helmet slot with an actual item that isn't already supposed to go
        // in the head slot
        if (event.getClickedInventory() != null &&
                event.getClickedInventory().getType() == InventoryType.PLAYER &&
                event.getRawSlot() == 5 &&
                event.getWhoClicked().getItemOnCursor().getType() != Material.AIR &&
                event.getWhoClicked().getItemOnCursor().getType().getEquipmentSlot() != EquipmentSlot.HEAD) {

            // Get the cursor and helmet items
            Player player = (Player) event.getWhoClicked();
            ItemStack cursorItem = player.getItemOnCursor();
            if (player.hasPermission("parallelutils.hat.*") ||
                    ((cursorItem.getType() == Material.PAPER || cursorItem.getType() == Material.LEATHER_HORSE_ARMOR)
                      && cursorItem.getItemMeta().hasCustomModelData())) {

                ItemStack helmetItem = player.getInventory().getHelmet();
                player.getInventory().setHelmet(null);
                player.setItemOnCursor(null);

                // Swap the cursor and helmet items
                // Delay hat placement by one tick (ty Mojang)
                Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                    @Override
                    public void run() {
                        player.getInventory().setHelmet(cursorItem);
                        player.setItemOnCursor(helmetItem);
                        MessageTools.sendMessage(player, "Item set as hat!", MessageType.SUCCESS);
                    }
                },1L);
            }
        }
    }


}
