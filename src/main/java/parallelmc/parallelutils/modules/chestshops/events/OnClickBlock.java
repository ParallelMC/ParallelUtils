package parallelmc.parallelutils.modules.chestshops.events;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.chestshops.ChestShops;
import parallelmc.parallelutils.modules.chestshops.Shop;
import parallelmc.parallelutils.modules.chestshops.ShopResult;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import java.util.HashMap;
import java.util.logging.Level;

public class OnClickBlock implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onClickBlock(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (block.getState() instanceof Sign) {
                Shop shop = ChestShops.get().getShopFromSignPos(block.getLocation());
                if (shop == null) {
                    Parallelutils.log(Level.WARNING, "Returned shop was null.");
                    return;
                }
                Block c = player.getWorld().getBlockAt(shop.chestPos());
                if (!(c.getState() instanceof Chest chest)) {
                    Parallelutils.log(Level.WARNING, "Block at " + shop.chestPos() + " should be a chest but it is actually a " + c.getType() + "! Removing...");
                    ChestShops.get().removeShop(shop.owner(), shop.chestPos());
                    return;
                }
                ItemStack diamonds = event.getItem();
                ShopResult result = ChestShops.get().attemptPurchase(player, shop, chest, diamonds);
                switch (result) {
                    case SHOP_EMPTY -> ParallelChat.sendParallelMessageTo(player, "This shop is out of stock!");
                    case INVENTORY_FULL -> ParallelChat.sendParallelMessageTo(player, "Your inventory is full!");
                    case NO_DIAMONDS -> ParallelChat.sendParallelMessageTo(player, "You must be holding diamonds in your hand to purchase an item!");
                    case INSUFFICIENT_FUNDS -> ParallelChat.sendParallelMessageTo(player, "You do not have enough diamonds to purchase this item!");
                    case SHOP_FULL -> ParallelChat.sendParallelMessageTo(player, "This chest shop cannot accept any more currency!");
                }
            }
            else if (block.getState() instanceof Chest chest) {
                Shop shop;
                InventoryHolder holder = chest.getInventory().getHolder();
                // check both sides of the double chest since each side is a separate block
                if (holder instanceof DoubleChest dc) {
                    Chest temp = (Chest)dc.getLeftSide();
                    if (temp == null) {
                        Parallelutils.log(Level.WARNING, "OnClickBlock: getLeftSide() returned null!");
                        return;
                    }
                    shop = ChestShops.get().getShopFromChestPos(temp.getLocation());
                    if (shop == null) {
                        temp = (Chest)dc.getRightSide();
                        if (temp == null) {
                            Parallelutils.log(Level.WARNING, "OnClickBlock: getRightSide() returned null!");
                            return;
                        }
                        shop = ChestShops.get().getShopFromChestPos(temp.getLocation());
                        if (shop == null)
                            return;
                    }
                    if (!player.hasPermission("parallelutils.bypass.chestshops") && shop.owner() != player.getUniqueId()) {
                        event.setCancelled(true);
                        ParallelChat.sendParallelMessageTo(player, "You cannot open this chest shop!");
                    }
                    return;
                }
                shop = ChestShops.get().getShopFromChestPos(block.getLocation());
                if (shop == null)
                    return;
                if (!player.hasPermission("parallelutils.bypass.chestshops") && shop.owner() != player.getUniqueId()) {
                    event.setCancelled(true);
                    ParallelChat.sendParallelMessageTo(player, "You cannot open this chest shop!");
                }
            }
        }
        else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (block.getState() instanceof Sign) {
                Shop shop = ChestShops.get().getShopFromSignPos(block.getLocation());
                if (shop != null) {
                    if (ChestShops.get().getPreviewInventory(player) != null) {
                        Parallelutils.log(Level.WARNING, player.getName() + " tried to open a shop preview with one already open!");
                        event.setCancelled(true);
                        return;
                    }
                    Block c = player.getWorld().getBlockAt(shop.chestPos());
                    if (!(c.getState() instanceof Chest chest)) {
                        Parallelutils.log(Level.WARNING, "Block at " + shop.chestPos() + " should be a chest but it is actually a " + c.getType() + "! Removing...");
                        ChestShops.get().removeShop(shop.owner(), shop.chestPos());
                        return;
                    }
                    Inventory inv = chest.getBlockInventory();
                    int slot = inv.first(shop.item());
                    if (slot == -1) {
                        ParallelChat.sendParallelMessageTo(player, "This shop is out of stock!");
                        return;
                    }
                    ChestShops.get().openShopPreview(player, shop, inv);
                    ParallelChat.sendParallelMessageTo(player, "Opening preview...");
                }
            }
        }
    }
}
