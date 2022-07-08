package parallelmc.parallelutils.modules.chestshops.events;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import parallelmc.parallelutils.modules.chestshops.ChestShops;
import parallelmc.parallelutils.modules.chestshops.Shop;
import parallelmc.parallelutils.modules.chestshops.ShopCurrency;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OnSignText implements Listener {
    // ChestShop formatting:
    //      ChestShop
    //       X items
    //      X currency

    // Plugin will update it to:
    //      ChestShop
    //     Player Name
    //      X items
    //     X currency
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onSignTextSet(SignChangeEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("parallelutils.chestshops.create")) {
            List<Component> lines = event.lines();
            if (lines.get(0).toString().equals("ChestShop")) {
                Directional d = (Directional)event.getBlock().getBlockData();
                Block attached = event.getBlock().getRelative(d.getFacing().getOppositeFace());
                if (attached.getState() instanceof Chest chest) {
                    Inventory inv = chest.getBlockInventory();
                    ItemStack sell = inv.getItem(0);
                    if (sell == null || sell.getType() == Material.AIR) {
                        event.setCancelled(true);
                        ParallelChat.sendParallelMessageTo(player, "Please place the item to sell in the first slot of the chest.");
                        return;
                    }
                    // if # of sellable items != # of items in the chest, then there is another item in the chest
                    if (inv.all(sell).size() != inv.getContents().length) {
                        event.setCancelled(true);
                        ParallelChat.sendParallelMessageTo(player, "Chest contents must all match the item in the first slot of the chest!");
                        return;
                    }
                    int sellNum;
                    try {
                        sellNum = Integer.parseInt(lines.get(1).toString());
                        if (sellNum < 1 || sellNum > sell.getMaxStackSize()) {
                            event.setCancelled(true);
                            ParallelChat.sendParallelMessageTo(player, "Invalid sell amount! Must be between 1 and " + sell.getMaxStackSize() + ".");
                            return;
                        }
                    } catch (NumberFormatException e) {
                        event.setCancelled(true);
                        ParallelChat.sendParallelMessageTo(player, "Invalid chest shop format!");
                        return;
                    }
                    Matcher regex = Pattern.compile("(\\d*)\\s(\\w*)").matcher(lines.get(2).toString());
                    if (regex.find()) {
                        int buyNum = Integer.parseInt(regex.group(1));
                        String currency = regex.group(2).toLowerCase();
                        ShopCurrency cur;
                        if (currency.contains("diamond"))
                            cur = ShopCurrency.DIAMOND;
                        else if (currency.contains("riftcoin"))
                            cur = ShopCurrency.RIFTCOIN;
                        else {
                            event.setCancelled(true);
                            ParallelChat.sendParallelMessageTo(player, "Invalid currency! Must be either diamonds or riftcoins.");
                            return;
                        }
                        if (buyNum < 1 || (cur == ShopCurrency.DIAMOND && buyNum > 64)) {
                            event.setCancelled(true);
                            ParallelChat.sendParallelMessageTo(player, "Invalid buy amount! Must be between 1 and 64 (for diamonds).");
                            return;
                        }
                        event.line(0, Component.text("ChestShop"));
                        event.line(1, Component.text(player.getName()));
                        if (sell.getType().toString().length() > 13)
                            event.line(2, Component.text(sellNum + " " + sell.getType().toString().substring(0, 13)));
                        else
                            event.line(2, Component.text(sellNum + " " + sell.getType()));
                        event.line(3, Component.text(buyNum + (cur == ShopCurrency.DIAMOND ? " diamond" : " riftcoin")));
                        ChestShops.get().addShop(player.getUniqueId(), attached.getLocation(), event.getBlock().getLocation(), sell.getType(), cur, sellNum, buyNum);
                        ParallelChat.sendParallelMessageTo(player, "Chest shop created!");
                    }
                }
                else {
                    event.setCancelled(true);
                    ParallelChat.sendParallelMessageTo(player, "Chest shop must be a regular chest!");
                }
            }
        }
    }
}
