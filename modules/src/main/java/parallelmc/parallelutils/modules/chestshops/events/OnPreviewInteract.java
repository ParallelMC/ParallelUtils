package parallelmc.parallelutils.modules.chestshops.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import parallelmc.parallelutils.modules.chestshops.ChestShops;

public class OnPreviewInteract implements Listener {
    @EventHandler
    public void onPreviewClick(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        Inventory inv = ChestShops.get().getPreviewInventory(player);
        if (inv != null && (inv.equals(event.getClickedInventory()) || inv.equals(event.getInventory()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPreviewDrag(InventoryDragEvent event) {
        Player player = (Player)event.getWhoClicked();
        Inventory inv = ChestShops.get().getPreviewInventory(player);
        if (inv != null && inv.equals(event.getInventory())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPreviewMoveItem(InventoryMoveItemEvent event) {
        if (ChestShops.get().previewInventoryExists(event.getDestination())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClosePreview(InventoryCloseEvent event) {
        Player player = (Player)event.getPlayer();
        Inventory inv = ChestShops.get().getPreviewInventory(player);
        if (inv != null) {
            if (event.getInventory().equals(inv)) {
                ChestShops.get().closeShopPreview(player);
            }
        }
    }

    @EventHandler
    public void onLeaveWhilePreviewing(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Inventory inv = ChestShops.get().getPreviewInventory(player);
        if (inv != null) {
            ChestShops.get().closeShopPreview(player);
        }
    }
}
