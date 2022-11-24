package parallelmc.parallelutils.modules.paralleltowns.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.paralleltowns.ParallelTowns;
import parallelmc.parallelutils.modules.paralleltowns.Town;

import java.util.ArrayList;
import java.util.List;

public class MainMenuInventory extends GUIInventory {

    private static final int MAP_INDEX = 2;

    public MainMenuInventory() {
        super(9, Component.text("Town Menu", NamedTextColor.DARK_AQUA).decoration(TextDecoration.BOLD, true));
        ItemStack members = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = members.getItemMeta();
        meta.displayName(Component.text("Town Members", NamedTextColor.YELLOW, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        List<Component> loreText = new ArrayList<>();
        loreText.add(Component.text("Click here to see all", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        loreText.add(Component.text("members of the town!", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        meta.lore(loreText);
        members.setItemMeta(meta);

        ItemStack charter = new ItemStack(Material.WRITABLE_BOOK);
        meta = charter.getItemMeta();
        meta.displayName(Component.text("Town Charter", NamedTextColor.YELLOW, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        loreText.clear();
        loreText.add(Component.text("Click here to view the", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        loreText.add(Component.text("town's charter!", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        meta.lore(loreText);
        charter.setItemMeta(meta);

        ItemStack options = new ItemStack(Material.COMPASS);
        meta = options.getItemMeta();
        meta.displayName(Component.text("Town Options", NamedTextColor.YELLOW, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        loreText.clear();
        loreText.add(Component.text("Click here to view more", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        loreText.add(Component.text("options for your town!", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        meta.lore(loreText);
        options.setItemMeta(meta);

        ItemStack exit = new ItemStack(Material.BARRIER);
        meta = exit.getItemMeta();
        meta.displayName(Component.text("Exit Menu", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        exit.setItemMeta(meta);

        inventory.setContents(new ItemStack[]{
                        placeholder(),
                        placeholder(),
                        new ItemStack(Material.MAP),
                        members,
                        charter,
                        options,
                        exit,
                        placeholder(),
                        placeholder()
                }
        );
    }


    // creates a custom ItemStack for the "Town Info" slot
    private ItemStack CreateTownItemSlot(@NotNull Town town) {
        ItemStack item = new ItemStack(Material.MAP);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Town Info", NamedTextColor.YELLOW, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        List<Component> loreText = new ArrayList<>();
        loreText.add(Component.text("Town Name: ", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false).append(Component.text(town.getName(), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));
        loreText.add(Component.text("Members: ", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false).append(Component.text(town.getMembers().size(), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));
        loreText.add(Component.text("Founded: ", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false).append(Component.text(town.getFoundedDate(), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));
        meta.lore(loreText);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void onOpen(Player player) {
        // TODO: Get town data
        inventory.setItem(MAP_INDEX, CreateTownItemSlot(new Town("Test", player.getUniqueId())));
    }

    @Override
    public void onSlotClicked(Player player, int slotNum) {
        switch (slotNum) {
            case 5 -> ParallelTowns.get().guiManager.openOptionsMenuForPlayer(player);
            case 6 -> player.closeInventory();
            default -> {
            }
        }
    }
}
