package parallelmc.parallelutils.modules.parallelchat.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.util.GUIInventory;

import java.util.ArrayList;
import java.util.List;

public class JoinLeaveInventory extends GUIInventory {
    public JoinLeaveInventory() {
        super(9, Component.text("Category Selection", NamedTextColor.DARK_AQUA, TextDecoration.BOLD));

        ItemStack join = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta meta = join.getItemMeta();
        meta.displayName(Component.text("Join Messages", NamedTextColor.GOLD, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        List<Component> loreText = new ArrayList<>();
        loreText.add(Component.text("Click to view", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        loreText.add(Component.text("custom join messages!", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        meta.lore(loreText);
        join.setItemMeta(meta);

        ItemStack leave = new ItemStack(Material.OAK_DOOR);
        meta = leave.getItemMeta();
        meta.displayName(Component.text("Leave Messages", NamedTextColor.GOLD, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        loreText.clear();
        loreText.add(Component.text("Click to view", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        loreText.add(Component.text("custom leave messages!", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        meta.lore(loreText);
        leave.setItemMeta(meta);

        ItemStack exit = new ItemStack(Material.BARRIER);
        meta = exit.getItemMeta();
        meta.displayName(Component.text("Exit GUI", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        exit.setItemMeta(meta);

        inventory.setContents(new ItemStack[] {
                PLACEHOLDER_LIGHT_BLUE,
                PLACEHOLDER_LIGHT_BLUE,
                join,
                AIR,
                leave,
                AIR,
                exit,
                PLACEHOLDER_LIGHT_BLUE,
                PLACEHOLDER_LIGHT_BLUE
        });
    }

    @Override
    public void onOpen(Player player) { }

    @Override
    public void onSlotClicked(Player player, int slotNum, ItemStack itemClicked) {
        switch (slotNum) {
            case 2 -> {
                ParallelChat.get().openJoinMessageInventory(player);
            }
            case 4 -> {
                ParallelChat.get().openLeaveMessageInventory(player);
            }
            case 6 -> {
                player.closeInventory();
            }
            default -> { }
        }
    }
}
