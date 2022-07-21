package parallelmc.parallelutils.modules.chestshops.events;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.InventoryHolder;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.chestshops.ChestShops;
import parallelmc.parallelutils.modules.chestshops.Shop;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import java.util.logging.Level;

public class OnBreakShop implements Listener {
    @EventHandler
    public void onBreakShop(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (block.getState() instanceof Sign) {
            Shop s = ChestShops.get().getShopFromSignPos(block.getLocation());
            if (s == null) return;
            if (!player.hasPermission("parallelutils.bypass.chestshops") && !s.owner().equals(player.getUniqueId())) {
                event.setCancelled(true);
                ParallelChat.sendParallelMessageTo(player, "You cannot break this chest shop!");
                return;
            }
            if (ChestShops.get().isPlayerUsingShop(s)) {
                event.setCancelled(true);
                ParallelChat.sendParallelMessageTo(player, "Please wait, a player is using this shop.");
                return;
            }
            ChestShops.get().removeShop(s.owner(), s.chestPos());
            ParallelChat.sendParallelMessageTo(player, "Chest shop unregistered.");
        }
        else if (block.getState() instanceof Chest chest) {
            Shop s;
            InventoryHolder holder = chest.getInventory().getHolder();
            // check both sides of the double chest since each side is a separate block
            if (holder instanceof DoubleChest dc) {
                Chest temp = (Chest)dc.getLeftSide();
                if (temp == null) {
                    Parallelutils.log(Level.WARNING, "OnBreakShop: getLeftSide() returned null!");
                    return;
                }
                s = ChestShops.get().getShopFromChestPos(temp.getLocation());
                if (s == null) {
                    temp = (Chest)dc.getRightSide();
                    if (temp == null) {
                        Parallelutils.log(Level.WARNING, "OnBreakShop: getRightSide() returned null!");
                        return;
                    }
                    s = ChestShops.get().getShopFromChestPos(temp.getLocation());
                    if (s == null)
                        return;
                }
                if (!player.hasPermission("parallelutils.bypass.chestshops") && !s.owner().equals(player.getUniqueId())) {
                    event.setCancelled(true);
                    ParallelChat.sendParallelMessageTo(player, "You cannot break this chest shop!");
                    return;
                }
                if (ChestShops.get().isPlayerUsingShop(s)) {
                    event.setCancelled(true);
                    ParallelChat.sendParallelMessageTo(player, "Please wait, a player is using this shop.");
                    return;
                }
                ChestShops.get().removeShop(s.owner(), s.chestPos());
                ParallelChat.sendParallelMessageTo(player, "Chest shop unregistered.");
                return;
            }
            s = ChestShops.get().getShopFromChestPos(chest.getLocation());
            if (s == null) return;
            if (!player.hasPermission("parallelutils.bypass.chestshops") && !s.owner().equals(player.getUniqueId())) {
                event.setCancelled(true);
                ParallelChat.sendParallelMessageTo(player, "You cannot break this chest shop!");
                return;
            }
            if (ChestShops.get().isPlayerUsingShop(s)) {
                event.setCancelled(true);
                ParallelChat.sendParallelMessageTo(player, "Please wait, a player is using this shop.");
                return;
            }
            ChestShops.get().removeShop(s.owner(), s.chestPos());
            ParallelChat.sendParallelMessageTo(player, "Chest shop unregistered.");
        }
    }
}
