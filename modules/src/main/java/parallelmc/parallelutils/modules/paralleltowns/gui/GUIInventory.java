package parallelmc.parallelutils.modules.paralleltowns.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import parallelmc.parallelutils.modules.paralleltowns.TownRank;

public abstract class GUIInventory {
    public Inventory inventory;

    // represents a placeholder item in a gui
    public static ItemStack PLACEHOLDER;

    // initialize the placeholder on class load
    static {
        PLACEHOLDER = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        ItemMeta meta = PLACEHOLDER.getItemMeta();
        meta.displayName(Component.text('-', NamedTextColor.AQUA));
        PLACEHOLDER.setItemMeta(meta);
    }

    public GUIInventory(int size, Component name) {
        inventory = Bukkit.createInventory(null, size, name);
    }

    public abstract void onOpen(Player player);

    public abstract void onSlotClicked(Player player, int slotNum, ItemStack itemClicked);

    public Component getComponentForRank(short rank) {
        if (rank == TownRank.LEADER)
            return Component.text("Town Leader", NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false);
        else if (rank == TownRank.OFFICIAL)
            return Component.text("Town Official", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false);
        else
            return Component.text("Member", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false);
    }

}
