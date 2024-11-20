package parallelmc.parallelutils.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.util.GUIInventory;
import parallelmc.parallelutils.util.GUIManager;

public class OnMenuInteract implements Listener {
    private final ParallelUtils puPlugin;
    public OnMenuInteract(ParallelUtils puPlugin) {
        this.puPlugin = puPlugin;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        InventoryAction action = event.getAction();
        if (action != InventoryAction.PICKUP_ALL && action != InventoryAction.PICKUP_HALF)
            return;
        Player player = (Player)event.getWhoClicked();
        GUIInventory inv = GUIManager.get().getOpenInventory(player);
        if (inv != null && (inv.inventory.equals(event.getClickedInventory()) || inv.inventory.equals(event.getInventory()))) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            // don't fire event for "placeholder" items
            if (item == null || item.getType() == Material.AIR || item.getType() == Material.LIGHT_BLUE_STAINED_GLASS_PANE) {
                return;
            }
            if (GUIManager.get().isOnCooldown(player.getUniqueId())) {
                player.sendMessage(Component.text("Slow down! Please wait a moment between clicks.", NamedTextColor.RED));
                return;
            }
            GUIManager.get().setCooldown(player.getUniqueId());
            inv.onSlotClicked(player, event.getRawSlot(), item);
            Bukkit.getScheduler().runTaskLater(puPlugin, () -> GUIManager.get().removeCooldown(player.getUniqueId()), 4);
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
