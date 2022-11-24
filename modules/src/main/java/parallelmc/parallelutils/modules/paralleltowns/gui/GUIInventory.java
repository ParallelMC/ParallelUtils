package parallelmc.parallelutils.modules.paralleltowns.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class GUIInventory {
    public Inventory inventory;
    public GUIInventory(int size, Component name) {
        inventory = Bukkit.createInventory(null, size, name);
    }

    public abstract void onOpen(Player player);

    public abstract void onSlotClicked(Player player, int slotNum);

    // helper function to create placeholder slots in inventories
    public static ItemStack placeholder() {
        ItemStack placeholder = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        ItemMeta meta = placeholder.getItemMeta();
        meta.displayName(Component.text('-', NamedTextColor.AQUA));
        placeholder.setItemMeta(meta);
        return placeholder;
    }
}
