package parallelmc.parallelutils.modules.paralleltowns.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.paralleltowns.*;
import parallelmc.parallelutils.util.GUIInventory;

import java.util.ArrayList;
import java.util.List;

public class OptionsInventory extends GUIInventory {

    private static final int DELETE_INDEX = 2;
    private static final int STATUS_INDEX = 3;
    private static final int CHARTER_INDEX = 5;
    private static final int DISPLAY_INDEX = 6;

    public OptionsInventory() {
        super(9, Component.text("Town Options", NamedTextColor.DARK_AQUA, TextDecoration.BOLD));

        ItemStack leave = new ItemStack(Material.RED_DYE);
        ItemMeta meta = leave.getItemMeta();
        meta.displayName(Component.text("Leave Town", NamedTextColor.DARK_RED, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        List<Component> loreText = new ArrayList<>();
        loreText.add(Component.text("Click here to ", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false)
                .append(Component.text("leave", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)));
        loreText.add(Component.text("your town!", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        meta.lore(loreText);
        leave.setItemMeta(meta);


        ItemStack exit = new ItemStack(Material.BARRIER);
        meta = exit.getItemMeta();
        meta.displayName(Component.text("Back to Town GUI", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        exit.setItemMeta(meta);


        inventory.setContents(new ItemStack[]{
                PLACEHOLDER_LIGHT_BLUE,
                leave,
                AIR,
                AIR,
                AIR,
                AIR,
                AIR,
                exit,
                PLACEHOLDER_LIGHT_BLUE
        });
    }


    @Override
    public void onOpen(Player player) {
        // only display certain buttons to leaders
        Town town = ParallelTowns.get().getPlayerTown(player);
        TownMember member = town.getMember(player);
        if (member.getTownRank() == TownRank.LEADER) {
            ItemStack delete = new ItemStack(Material.REDSTONE_BLOCK);
            ItemMeta meta = delete.getItemMeta();
            meta.displayName(Component.text("Delete Town", NamedTextColor.DARK_RED, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
            List<Component> loreText = new ArrayList<>();
            loreText.add(Component.text("Click here to ", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false)
                    .append(Component.text("delete", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)));
            loreText.add(Component.text("your town!", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            meta.lore(loreText);
            delete.setItemMeta(meta);
            inventory.setItem(DELETE_INDEX, delete);

            ItemStack status = new ItemStack(town.isOpen() ? Material.LIME_WOOL : Material.RED_WOOL);
            meta = status.getItemMeta();
            meta.displayName(Component.text("Update Town Status", NamedTextColor.YELLOW, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
            loreText.clear();
            loreText.add(Component.text("Click here to update", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            loreText.add(Component.text("the town status!", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            loreText.add(Component.empty());
            loreText.add(Component.text("Current Status:", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            if (town.isOpen())
                loreText.add(Component.text("Open", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
            else
                loreText.add(Component.text("Invite Only", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
            meta.lore(loreText);
            status.setItemMeta(meta);
            inventory.setItem(STATUS_INDEX, status);

            ItemStack charter = new ItemStack(Material.WRITABLE_BOOK);
            meta = charter.getItemMeta();
            meta.displayName(Component.text("Update Town Charter", NamedTextColor.YELLOW, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
            loreText.clear();
            loreText.add(Component.text("Click here to update", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            loreText.add(Component.text("the town charter!", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            loreText.add(Component.empty());
            loreText.add(Component.text("You must be holding a", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            loreText.add(Component.text("book and quill", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
            loreText.add(Component.text("to update the town charter!", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            meta.lore(loreText);
            charter.setItemMeta(meta);
            inventory.setItem(CHARTER_INDEX, charter);

            DisplayItem item = town.getDisplayItem();
            ItemStack display = new ItemStack(item.getMaterial());
            meta = display.getItemMeta();
            if (item.getModelData() != -1) {
                meta.setCustomModelData(item.getModelData());
            }
            meta.displayName(Component.text("Update List Item", NamedTextColor.YELLOW, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
            loreText.clear();
            loreText.add(Component.text("Click here to update", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            loreText.add(Component.text("the town list item!", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            loreText.add(Component.empty());
            loreText.add(Component.text("You must be holding", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            loreText.add(Component.text("any item", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
            loreText.add(Component.text("you want to update it to!", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            meta.lore(loreText);
            display.setItemMeta(meta);
            inventory.setItem(DISPLAY_INDEX, display);
        }
    }

    @Override
    public void onSlotClicked(Player player, int slotNum, ItemStack itemClicked) {
        switch (slotNum) {
            case 1 -> {
                Town town = ParallelTowns.get().getPlayerTown(player);
                TownMember member = town.getMember(player);
                // there must be at least one leader in the town
                if (member.getTownRank() == TownRank.LEADER && town.getMembers().values().stream().filter(x -> x.getTownRank() == TownRank.LEADER).count() == 1) {
                    player.closeInventory();
                    ParallelChat.sendParallelMessageTo(player, "You cannot leave, you are the only leader!");
                    return;
                }
                ParallelTowns.get().openTownConfirmationForPlayer(player, town, ConfirmationAction.LEAVE);
            }
            case 2 -> {
                Town town = ParallelTowns.get().getPlayerTown(player);
                if (town.getMember(player).getTownRank() == TownRank.LEADER) {
                    ParallelTowns.get().openTownConfirmationForPlayer(player, town, ConfirmationAction.DELETE);
                }
                else {
                    // this message should never display but sanity check anyway
                    ParallelChat.sendParallelMessageTo(player, "You must be a leader in order to delete the town!");
                }
            }
            case 3 -> {
                Town town = ParallelTowns.get().getPlayerTown(player);
                if (town.getMember(player).getTownRank() == TownRank.LEADER) {
                    ParallelTowns.get().openTownConfirmationForPlayer(player, town, ConfirmationAction.STATUS);
                }
                else {
                    // this message should never display but sanity check anyway
                    ParallelChat.sendParallelMessageTo(player, "You must be a leader in order to toggle the town status!");
                }
            }
            case 5 -> {
                Town town = ParallelTowns.get().getPlayerTown(player);
                ItemStack hand = player.getInventory().getItemInMainHand();
                if (hand.getType() != Material.WRITABLE_BOOK) {
                    player.closeInventory();
                    ParallelChat.sendParallelMessageTo(player, "You must be holding a book and quill to update the town charter.");
                }
                else {
                    ParallelTowns.get().openTownConfirmationForPlayer(player, town, ConfirmationAction.CHARTER);
                }
            }
            case 6 -> {
                Town town = ParallelTowns.get().getPlayerTown(player);
                ItemStack hand = player.getInventory().getItemInMainHand();
                if (hand.getType() == Material.AIR) {
                    player.closeInventory();
                    ParallelChat.sendParallelMessageTo(player, "You must be holding an item to update the town list item.");
                }
                else {
                    ParallelTowns.get().openTownConfirmationForPlayer(player, town, ConfirmationAction.DISPLAY);
                }
            }
            case 7 -> ParallelTowns.get().openMainMenuForPlayer(player);
        }
    }
}
