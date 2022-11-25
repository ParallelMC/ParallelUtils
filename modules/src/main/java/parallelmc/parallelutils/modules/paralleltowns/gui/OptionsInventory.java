package parallelmc.parallelutils.modules.paralleltowns.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.paralleltowns.ParallelTowns;
import parallelmc.parallelutils.modules.paralleltowns.TownMember;
import parallelmc.parallelutils.modules.paralleltowns.TownRank;

import java.util.ArrayList;
import java.util.List;

public class OptionsInventory extends GUIInventory {

    private static final int DELETE_INDEX = 3;

    public OptionsInventory() {
        super(9, Component.text("Town Options", NamedTextColor.DARK_AQUA, TextDecoration.BOLD));

        ItemStack exit = new ItemStack(Material.BARRIER);
        ItemMeta meta = exit.getItemMeta();
        meta.displayName(Component.text("Back to Town GUI", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        exit.setItemMeta(meta);

        inventory.setContents(new ItemStack[]{
                        PLACEHOLDER,
                        PLACEHOLDER,
                        PLACEHOLDER,
                        new ItemStack(Material.AIR),
                        new ItemStack(Material.AIR),
                        exit,
                        PLACEHOLDER,
                        PLACEHOLDER,
                        PLACEHOLDER
                }
        );
    }


    @Override
    public void onOpen(Player player) {
        // only display delete button to leaders
        TownMember member = ParallelTowns.get().getPlayerTownStatus(player);
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
        }
    }

    @Override
    public void onSlotClicked(Player player, int slotNum, ItemStack itemClicked) {
        switch (slotNum) {
            case 3 -> {
                TownMember member = ParallelTowns.get().getPlayerTownStatus(player);
                if (member.getTownRank() == TownRank.LEADER) {
                    // TODO: open "are you sure" ui and handle deletion there
                    player.closeInventory();
                    ParallelTowns.get().deleteTown(member.getTownName());
                    ParallelChat.sendParallelMessageTo(player, "Town deleted.");
                }
                else {
                    // this message should never display but sanity check anyway
                    ParallelChat.sendParallelMessageTo(player, "You must be a leader in order to delete the town!");
                }
            }
            case 5 -> ParallelTowns.get().guiManager.openMainMenuForPlayer(player);
            default -> {
            }
        }
    }
}
