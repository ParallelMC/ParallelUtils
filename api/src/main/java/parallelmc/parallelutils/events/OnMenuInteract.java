package parallelmc.parallelutils.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.util.GUIInventory;
import parallelmc.parallelutils.util.GUIManager;

public class OnMenuInteract implements Listener {
    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        GUIInventory inv = GUIManager.get().getOpenInventory(player);
        if (inv != null && (inv.inventory.equals(event.getClickedInventory()) || inv.inventory.equals(event.getInventory()))) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            // don't fire event for "placeholder" items
            if (item == null || GUIInventory.isPlaceholderItem(item.getType()))
                return;
            inv.onSlotClicked(player, event.getRawSlot(), item);
        }
    }

    @EventHandler
    public void onMenuDrag(InventoryDragEvent event) {
        Player player = (Player)event.getWhoClicked();
        GUIInventory inv = GUIManager.get().getOpenInventory(player);
        if (inv != null && inv.inventory.equals(event.getInventory())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMenuMoveItem(InventoryMoveItemEvent event) {
        if (GUIManager.get().openInventoryExists(event.getDestination())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCloseMenu(InventoryCloseEvent event) {
        Player player = (Player)event.getPlayer();
        GUIInventory inv = GUIManager.get().getOpenInventory(player);
        if (inv != null && event.getInventory().equals(inv.inventory)) {
            GUIManager.get().closeMenu(player);
        }
    }

    @EventHandler
    public void onLeaveWhileInMenu(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        GUIInventory inv = GUIManager.get().getOpenInventory(player);
        if (inv != null) {
             GUIManager.get().closeMenu(player);
        }
    }

}
