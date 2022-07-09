package parallelmc.parallelutils.modules.chestshops.events;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.chestshops.ChestShops;
import parallelmc.parallelutils.modules.chestshops.Shop;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import java.util.*;
import java.util.logging.Level;

public class OnClickBlock implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onClickBlock(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        // TODO: add left click support
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
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
                Inventory inv = chest.getBlockInventory();
                List<? extends ItemStack> items = inv.all(shop.item()).values().stream().toList();
                int itemAmt = 0;
                for (ItemStack i : items) {
                    itemAmt += i.getAmount();
                }
                if (inv.getContents().length == 0 || items.size() == 0 || itemAmt < shop.sellAmt()) {
                    ParallelChat.sendParallelMessageTo(player, "This shop is out of stock!");
                    return;
                }
                ItemStack item = event.getItem();
                if (player.getInventory().firstEmpty() == -1) {
                    ParallelChat.sendParallelMessageTo(player, "Your inventory is full!");
                    return;
                }
                if (item == null || item.getType() != Material.DIAMOND) {
                    ParallelChat.sendParallelMessageTo(player, "You must be holding diamonds in your hand to purchase an item!");
                    return;
                }
                if (item.getAmount() < shop.buyAmt()) {
                    ParallelChat.sendParallelMessageTo(player, "You do not have enough diamonds to purchase this item!");
                    return;
                }
                // make a copy of each item
                ItemStack give = new ItemStack(items.get(0));
                give.setAmount(shop.sellAmt());
                ItemStack take = new ItemStack(item);
                take.setAmount(shop.buyAmt());
                if (inv.addItem(take).size() > 0) {
                    // if addItem returns any stacks, then the chest is too full
                    // undo the addition and cancel the transaction
                    inv.removeItem(take);
                    ParallelChat.sendParallelMessageTo(player, "This chest shop cannot accept any more currency!");
                    return;
                }
                item.subtract(shop.buyAmt());
                player.getInventory().addItem(give);
                inv.removeItem(give);
                ParallelChat.sendParallelMessageTo(player, "You bought " + shop.sellAmt() + "x " + shop.item() + "!");
            }
            else if (block.getState() instanceof Chest) {
                Shop shop = ChestShops.get().getShopFromChestPos(block.getLocation());
                if (shop == null)
                    return;
                if (shop.owner() != player.getUniqueId()) {
                    event.setCancelled(true);
                    ParallelChat.sendParallelMessageTo(player, "You cannot open this chest shop!");
                }
            }
        }
    }
}
