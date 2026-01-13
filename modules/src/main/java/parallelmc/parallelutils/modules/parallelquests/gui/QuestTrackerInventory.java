package parallelmc.parallelutils.modules.parallelquests.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import parallelmc.parallelutils.modules.parallelquests.ParallelQuests;
import parallelmc.parallelutils.modules.parallelquests.QuestStatus;
import parallelmc.parallelutils.util.GUIInventory;

import java.util.ArrayList;
import java.util.List;

public class QuestTrackerInventory extends GUIInventory {

    private static final int SPYGLASS_INDEX = 4;
    private static final int EXIT_INDEX = 49;

    public QuestTrackerInventory() {
        super(54, Component.text("Quest Tracker", NamedTextColor.GOLD, TextDecoration.BOLD));

        ItemStack map = new ItemStack(Material.SPYGLASS);
        ItemMeta meta = map.getItemMeta();
        meta.displayName(Component.text("Quest Tracker", NamedTextColor.YELLOW, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("View all of your", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("active quests below!", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        map.setItemMeta(meta);

        ItemStack exit = new ItemStack(Material.BARRIER);
        meta = exit.getItemMeta();
        meta.displayName(Component.text("Exit Menu", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        exit.setItemMeta(meta);

        inventory.setItem(SPYGLASS_INDEX, map);
        inventory.setItem(EXIT_INDEX, exit);
    }


    @Override
    public void onOpen(Player player) {
        List<QuestStatus> statuses = ParallelQuests.get().getQuestStatus(player.getUniqueId());
        int slot = 9;
        for (QuestStatus status : statuses) {
            ItemStack entry;
            // TODO: populate with quest info
            if (status.isCompleted()) {
                entry = new ItemStack(Material.YELLOW_CONCRETE);
            }
            else {
                entry = new ItemStack(Material.LIME_CONCRETE);
            }

            inventory.setItem(slot, entry);
            slot++;
            if (slot > 43)
                break;
        }
    }

    @Override
    public void onSlotClicked(Player player, int slotNum, ItemStack itemClicked) {
        if (slotNum == EXIT_INDEX) {
            player.closeInventory();
        }
    }
}
