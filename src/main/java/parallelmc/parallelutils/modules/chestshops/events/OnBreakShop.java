package parallelmc.parallelutils.modules.chestshops.events;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import parallelmc.parallelutils.modules.chestshops.ChestShops;
import parallelmc.parallelutils.modules.chestshops.Shop;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

public class OnBreakShop implements Listener {
    @EventHandler
    public void onBreakShop(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (block.getState() instanceof Sign) {
            Shop s = ChestShops.get().getShopFromSignPos(block.getLocation());
            if (s == null) return;
            if (!player.hasPermission("parallelutils.bypass.chestshops") && s.owner() != player.getUniqueId()) {
                event.setCancelled(true);
                ParallelChat.sendParallelMessageTo(player, "You cannot break this chest shop!");
                return;
            }
            ChestShops.get().removeShop(s.owner(), s.chestPos());
            ParallelChat.sendParallelMessageTo(player, "Chest shop unregistered.");
        }
        else if (block.getState() instanceof Chest) {
            Shop s = ChestShops.get().getShopFromChestPos(block.getLocation());
            if (s == null) return;
            if (!player.hasPermission("parallelutils.bypass.chestshops") && s.owner() != player.getUniqueId()) {
                event.setCancelled(true);
                ParallelChat.sendParallelMessageTo(player, "You cannot break this chest shop!");
                return;
            }
            ChestShops.get().removeShop(s.owner(), s.chestPos());
            ParallelChat.sendParallelMessageTo(player, "Chest shop unregistered.");
        }
    }
}
