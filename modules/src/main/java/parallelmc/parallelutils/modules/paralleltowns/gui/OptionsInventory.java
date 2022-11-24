package parallelmc.parallelutils.modules.paralleltowns.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import parallelmc.parallelutils.modules.paralleltowns.ParallelTowns;

import java.util.ArrayList;
import java.util.List;

public class OptionsInventory extends GUIInventory {

    public OptionsInventory() {
        super(9, Component.text("Town Options", NamedTextColor.DARK_AQUA).decoration(TextDecoration.BOLD, true));
        ItemStack delete = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta meta = delete.getItemMeta();
        meta.displayName(Component.text("Delete Town", NamedTextColor.DARK_RED, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        List<Component> loreText = new ArrayList<>();
        loreText.add(Component.text("Click here to ", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false)
                .append(Component.text("delete", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)));
        loreText.add(Component.text("your town!", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        meta.lore(loreText);
        delete.setItemMeta(meta);


        ItemStack exit = new ItemStack(Material.BARRIER);
        meta = exit.getItemMeta();
        meta.displayName(Component.text("Back to Town GUI", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        exit.setItemMeta(meta);

        inventory.setContents(new ItemStack[]{
                        placeholder(),
                        placeholder(),
                        placeholder(),
                        delete,
                        new ItemStack(Material.AIR),
                        exit,
                        placeholder(),
                        placeholder(),
                        placeholder()
                }
        );
    }


    @Override
    public void onOpen(Player player) { }

    @Override
    public void onSlotClicked(Player player, int slotNum) {
        if (slotNum == 5) {
            ParallelTowns.get().guiManager.openMainMenuForPlayer(player);
        }
    }
}
