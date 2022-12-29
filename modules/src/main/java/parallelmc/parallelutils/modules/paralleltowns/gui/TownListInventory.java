package parallelmc.parallelutils.modules.paralleltowns.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.paralleltowns.DisplayItem;
import parallelmc.parallelutils.modules.paralleltowns.ParallelTowns;
import parallelmc.parallelutils.modules.paralleltowns.Town;

import java.util.*;
import java.util.logging.Level;

public class TownListInventory  extends GUIInventory {

    private static final int MAP_INDEX = 4;
    private static final int EXIT_INDEX = 49;

    private final List<Integer> openSlots;

    public TownListInventory() {
        super(54, Component.text("Town List", NamedTextColor.DARK_AQUA, TextDecoration.BOLD));

        ItemStack map = new ItemStack(Material.MAP);
        ItemMeta meta = map.getItemMeta();
        meta.displayName(Component.text("Town List", NamedTextColor.YELLOW, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("View all of the existing", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("towns below!", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        map.setItemMeta(meta);

        ItemStack exit = new ItemStack(Material.BARRIER);
        meta = exit.getItemMeta();
        meta.displayName(Component.text("Exit GUI", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        exit.setItemMeta(meta);

        inventory.setItem(MAP_INDEX, map);
        inventory.setItem(EXIT_INDEX, exit);

        // keep track of which slots house an "open" town
        openSlots = new ArrayList<>();
    }

    @Override
    public void onOpen(Player player) {
        List<Town> towns = ParallelTowns.get().getAllTowns();
        int slot = 9;
        for (Town town : towns) {
            DisplayItem display = town.getDisplayItem();
            ItemStack item = new ItemStack(display.getMaterial());
            ItemMeta meta = item.getItemMeta();
            if (display.getModelData() != -1) {
                meta.setCustomModelData(display.getModelData());
            }
            meta.displayName(Component.text(town.getName(), NamedTextColor.YELLOW, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
            List<Component> lore = new ArrayList<>();
            var findFounder = town.getMembers().entrySet().stream().filter(x -> x.getValue().getIsFounder()).findFirst();
            if (findFounder.isPresent()) {
                UUID uuid = findFounder.get().getKey();
                OfflinePlayer founder = Bukkit.getOfflinePlayer(uuid);
                if (founder.getName() == null) {
                    ParallelUtils.log(Level.WARNING, "Could not get name for UUID " + uuid + " when querying town founder for " + town.getName());
                    continue;
                }
                lore.add(Component.empty());
                lore.add(Component.text("Founded By:", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false));
                lore.add(Component.text(founder.getName(), NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
            }
            lore.add(Component.empty());
            lore.add(Component.text("Members:", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.text(town.getMembers().size(), NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.empty());
            lore.add(Component.text("Town Status:", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false));
            if (town.isOpen()) {
                lore.add(Component.text("Open", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
                openSlots.add(slot);
            }
            else
                lore.add(Component.text("Invite Only", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);
            item.setItemMeta(meta);
            inventory.setItem(slot, item);
            slot++;
            // TODO: add pagination when necessary
            if (slot >= 44) {
                break;
            }
        }
    }

    @Override
    public void onSlotClicked(Player player, int slotNum, ItemStack itemClicked) {
        if (slotNum == EXIT_INDEX) {
            player.closeInventory();
        }
        if (openSlots.contains(slotNum)) {
            if (ParallelTowns.get().isPlayerInTown(player)) {
                player.closeInventory();
                ParallelChat.sendParallelMessageTo(player, "You are already in a town!");
                return;
            }
            String townName = PlainTextComponentSerializer.plainText().serialize(itemClicked.displayName());
            // PlainTextComponentSerializer converts bold text to brackets
            // i.e. <bold>Hello --> [Hello]
            // so we have to remove them here
            townName = townName.substring(1, townName.length() - 1);
            Town town = ParallelTowns.get().getTownByName(townName);
            if (town == null) {
                ParallelUtils.log(Level.SEVERE, "Failed to get town with name " + townName);
                return;
            }
            ParallelTowns.get().guiManager.openTownConfirmationForPlayer(player, town, ConfirmationAction.JOIN);
        }
    }
}
