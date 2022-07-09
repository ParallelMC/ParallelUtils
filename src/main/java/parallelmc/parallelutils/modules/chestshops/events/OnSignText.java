package parallelmc.parallelutils.modules.chestshops.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.chestshops.ChestShops;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
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
        if (player.hasPermission("parallelutils.chestshops.create") || player.isOp()) {
            List<String> lines = new ArrayList<>();
            // thanks kyori
            event.lines().forEach((l) -> lines.add(PlainTextComponentSerializer.plainText().serialize(l)));
            if (lines.get(0).equals("ChestShop")) {
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
                    int sellNum;
                    try {
                        sellNum = Integer.parseInt(lines.get(1));
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
                    int buyNum = Integer.parseInt(lines.get(2));
                    if (buyNum < 1 || buyNum > 64) {
                        event.setCancelled(true);
                        ParallelChat.sendParallelMessageTo(player, "Invalid buy amount! Must be between 1 and 64.");
                        return;
                    }
                    event.line(0, Component.text("ChestShop"));
                    event.line(1, Component.text(player.getName()));
                    if (sell.getType().toString().length() > 13)
                        event.line(2, Component.text(sellNum + " " + sell.getType().toString().substring(0, 13)));
                    else
                        event.line(2, Component.text(sellNum + " " + sell.getType()));
                    event.line(3, Component.text(buyNum + " diamonds"));
                    ChestShops.get().addShop(player.getUniqueId(), attached.getLocation(), event.getBlock().getLocation(), sell.getType(), sellNum, buyNum);
                    ParallelChat.sendParallelMessageTo(player, "Chest shop created!");
                }
                else {
                    event.setCancelled(true);
                    ParallelChat.sendParallelMessageTo(player, "Chest shop must be a regular chest!");
                }
            }
        }
    }
}
