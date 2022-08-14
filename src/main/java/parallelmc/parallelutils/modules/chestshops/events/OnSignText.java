package parallelmc.parallelutils.modules.chestshops.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyFormat;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import parallelmc.parallelutils.modules.chestshops.ChestShops;
import parallelmc.parallelutils.modules.chestshops.Shop;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
                if (attached.getType() == Material.CHEST || attached.getType() == Material.BARREL) {
                    Container chest = (Container)attached.getState();
                    Shop existing = ChestShops.get().getShopFromChestPos(chest.getLocation());
                    if (existing != null) {
                        event.setCancelled(true);
                        ParallelChat.sendParallelMessageTo(player, "Error: A shop already exists at this location.");
                        return;
                    }
                    if (chest.getInventory().getHolder() instanceof DoubleChest dc) {
                        InventoryHolder left = dc.getLeftSide();
                        InventoryHolder right = dc.getRightSide();
                        if (left == null || right == null) {
                            return;
                        }
                        if (ChestShops.get().getShopFromChestPos(((Chest)left).getLocation()) != null ||
                                ChestShops.get().getShopFromChestPos(((Chest)right).getLocation()) != null) {
                            event.setCancelled(true);
                            ParallelChat.sendParallelMessageTo(player, "Error: A shop already exists at this location.");
                            return;
                        }
                    }
                    Inventory inv = chest.getInventory();
                    ItemStack sell = inv.getItem(0);
                    if (sell == null || sell.getType() == Material.AIR) {
                        event.setCancelled(true);
                        ParallelChat.sendParallelMessageTo(player, "Please place the item to sell in the first slot of the chest.");
                        return;
                    }
                    if (sell.getType() == Material.DIAMOND || sell.getType() == Material.DIAMOND_BLOCK) {
                        event.setCancelled(true);
                        ParallelChat.sendParallelMessageTo(player, "You cannot sell diamonds!");
                        return;
                    }
                    int sellNum;
                    try {
                        sellNum = Integer.parseInt(lines.get(1));
                        if (sell.hasItemMeta()) {
                            if (sellNum < 1 || sellNum > sell.getMaxStackSize()) {
                                event.setCancelled(true);
                                ParallelChat.sendParallelMessageTo(player, "Invalid sell amount! Must be between 1 and " + sell.getMaxStackSize() + ".");
                                return;
                            }
                        } else {
                            if (sellNum < 1 || sellNum > sell.getMaxStackSize() * 4) {
                                event.setCancelled(true);
                                ParallelChat.sendParallelMessageTo(player, "Invalid sell amount! Must be between 1 and " + sell.getMaxStackSize() * 4 + ".");
                                return;
                            }
                        }
                    } catch (NumberFormatException e) {
                        event.setCancelled(true);
                        ParallelChat.sendParallelMessageTo(player, "Invalid chest shop format!");
                        return;
                    }
                    int buyNum = Integer.parseInt(lines.get(2));
                    if (buyNum < 0 || buyNum > 64) {
                        event.setCancelled(true);
                        ParallelChat.sendParallelMessageTo(player, "Invalid buy amount! Must be between 0 and 64.");
                        return;
                    }
                    event.line(0, Component.text("ChestShop"));
                    event.line(1, Component.text(player.getName()));
                    Component name = sell.displayName();
                    if (sell.hasItemMeta() && sell.getItemMeta().hasDisplayName()) {
                        name = sell.getItemMeta().displayName();
                    }
                    // warning can be ignored, compiler doesn't recognize the hasDisplayName check
                    // we have to do this or else long names don't display at all
                    String trim = LegacyComponentSerializer.legacyAmpersand().serialize(name);
                    if (trim.length() > 15) {
                        trim = trim.substring(0, 12);
                        if (trim.endsWith("&")) {
                            trim = trim.substring(0, 11);
                        }
                        trim += "...";
                        event.line(2, Component.text(sellNum + " ").append(LegacyComponentSerializer.legacyAmpersand().deserialize(trim)));
                    }
                    else {
                        // if shorter than 13 characters just use the existing component
                        event.line(2, Component.text(sellNum + " ").append(name));
                    }
                    if (buyNum == 0) {
                        event.line(3, Component.text("Free"));
                    }
                    else {
                        event.line(3, Component.text(buyNum + " diamonds"));
                    }
                    ChestShops.get().addShop(player.getUniqueId(), UUID.randomUUID(), attached.getLocation(), event.getBlock().getLocation(), sell.getType(), sellNum, buyNum);
                    ParallelChat.sendParallelMessageTo(player, "Chest shop created!");
                }
                else {
                    event.setCancelled(true);
                    ParallelChat.sendParallelMessageTo(player, "Chest shop sign must be placed on the side of a regular chest or barrel!");
                }
            }
        }
    }
}
