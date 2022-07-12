package parallelmc.parallelutils.modules.chestshops.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import parallelmc.parallelutils.modules.chestshops.ChestShops;
import parallelmc.parallelutils.modules.chestshops.ShopperData;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

public class OnShopInteract implements Listener {
    @EventHandler
    public void onShopClick(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        ShopperData data = ChestShops.get().getShoppingData(player);
        if (data != null && data.fakeInv().equals(event.getClickedInventory()) && event.getCurrentItem() != null) {
            // make a copy of each item
            ItemStack give = new ItemStack(event.getCurrentItem());
            give.setAmount(data.shop().sellAmt());
            ItemStack take = new ItemStack(data.diamonds());
            take.setAmount(data.shop().buyAmt());
            data.diamonds().subtract(data.shop().buyAmt());
            player.getInventory().addItem(give);
            ItemStack update = event.getCurrentItem().subtract(data.shop().sellAmt());
            data.chestInv().setItem(event.getRawSlot(), update);
            data.chestInv().addItem(take);
            Component name = give.displayName();
            if (give.hasItemMeta() && give.getItemMeta().hasDisplayName()) {
                name = give.getItemMeta().displayName();
            }
            ParallelChat.sendParallelMessageTo(player, Component.text("You bought " + data.shop().sellAmt() + "x ", NamedTextColor.GREEN).append(name));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onShopDrag(InventoryDragEvent event) {
        Player player = (Player)event.getWhoClicked();
        ShopperData data = ChestShops.get().getShoppingData(player);
        if (data != null && data.fakeInv().equals(event.getInventory())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onShopMoveItem(InventoryMoveItemEvent event) {
        if (ChestShops.get().shopInventoryExists(event.getDestination())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCloseShop(InventoryCloseEvent event) {
        Player player = (Player)event.getPlayer();
        ShopperData data = ChestShops.get().getShoppingData(player);
        if (data != null) {
            if (event.getInventory().equals(data.fakeInv())) {
                ChestShops.get().stopShopping(player);
            }
        }
    }

    @EventHandler
    public void onLeaveWhileShopping(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ShopperData data = ChestShops.get().getShoppingData(player);
        if (data != null) {
            ChestShops.get().stopShopping(player);
        }
    }
}
