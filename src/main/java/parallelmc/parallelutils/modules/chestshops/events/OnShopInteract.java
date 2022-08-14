package parallelmc.parallelutils.modules.chestshops.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.chestshops.ChestShops;
import parallelmc.parallelutils.modules.chestshops.ShopperData;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import java.util.logging.Level;

public class OnShopInteract implements Listener {
    // suppress supposed null pointers that the compiler doesn't recognize
    @SuppressWarnings("ConstantConditions")
    @EventHandler
    public void onShopClick(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        ShopperData data = ChestShops.get().getShoppingData(player);
        Inventory inv = event.getClickedInventory();
        if (data != null && data.fakeInv().equals(inv) && event.getCurrentItem() != null) {
            if (!inv.containsAtLeast(event.getCurrentItem(), data.shop().sellAmt())) {
                ParallelChat.sendParallelMessageTo(player, "Shop is out of stock!");
                player.closeInventory();
                return;
            }
            if (player.getItemOnCursor() != null && !player.getItemOnCursor().getType().isAir()) {
                ParallelChat.sendParallelMessageTo(player, "Your held mouse item must be empty!");
                player.closeInventory();
                return;
            }
            if (data.shop().buyAmt() > 0) {
                int slot = player.getInventory().first(Material.DIAMOND);
                if (slot == -1) {
                    ParallelChat.sendParallelMessageTo(player, "You do not have enough diamonds left!");
                    player.closeInventory();
                    return;
                }
                ItemStack diamonds = player.getInventory().getItem(slot);
                if (diamonds.getAmount() < data.shop().buyAmt()) {
                    ParallelChat.sendParallelMessageTo(player, "You do not have enough diamonds left!");
                    player.closeInventory();
                    return;
                }
                ItemStack take = new ItemStack(Material.DIAMOND);
                take.setAmount(data.shop().buyAmt());
                diamonds.subtract(data.shop().buyAmt());
                data.chestInv().addItem(take);
            }
            // make a copy of each item
            ItemStack give = new ItemStack(event.getCurrentItem());
            int maxStack = give.getMaxStackSize();
            if (data.shop().sellAmt() <= maxStack) {
                give.setAmount(data.shop().sellAmt());
                player.getInventory().addItem(give);
            }
            else {
                // prevent stacking unstackable items
                int itemsLeft = data.shop().sellAmt();
                while (itemsLeft > 0) {
                    give.setAmount(Math.min(itemsLeft, maxStack));
                    player.getInventory().addItem(give);
                    itemsLeft -= give.getMaxStackSize();
                }
            }
            int amtLeft = event.getCurrentItem().getAmount() - data.shop().sellAmt();
            ItemStack update = event.getCurrentItem().subtract(data.shop().sellAmt());
            data.chestInv().setItem(event.getRawSlot(), update);
            if (amtLeft < 0) {
                amtLeft = -amtLeft;
                while (amtLeft > 0) {
                    int nextSlot = data.chestInv().first(data.shop().item());
                    if (nextSlot == -1) {
                        Parallelutils.log(Level.SEVERE, "ItemStack was null when it shouldn't be!");
                        return;
                    }
                    ItemStack next = data.chestInv().getItem(nextSlot);
                    int amt = next.getAmount() - amtLeft;
                    next.subtract(amtLeft);
                    data.fakeInv().getItem(nextSlot).subtract(amtLeft);
                    if (amt > 0)
                        break;
                    amtLeft = -amt;
                }
            }
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
