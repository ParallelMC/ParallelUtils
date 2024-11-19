package parallelmc.parallelutils.modules.points.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.points.Points;
import parallelmc.parallelutils.modules.points.RedeemableItem;
import parallelmc.parallelutils.util.GUIInventory;

import java.util.ArrayList;
import java.util.List;

public class PointsRedeemInventory extends GUIInventory {

    private static List<RedeemableItem> redeemableItems = new ArrayList<>();

    public PointsRedeemInventory() {
        super(45, Component.text("Advancement Points Redemption", NamedTextColor.GOLD, TextDecoration.BOLD));

        ItemStack exit = new ItemStack(Material.BARRIER);
        ItemMeta meta = exit.getItemMeta();
        meta.displayName(Component.text("Exit GUI", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        exit.setItemMeta(meta);

        inventory.setItem(40, exit);
    }


    @Override
    public void onOpen(Player player) {
        redeemableItems = Points.get().getRedeemableItems();
        int playerPoints = Points.get().getPlayerPoints(player);
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skull = (SkullMeta)head.getItemMeta();
        skull.displayName(Component.text(player.getName(), NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        skull.setPlayerProfile(player.getPlayerProfile());
        skull.lore(List.of(Component.text("Advancement Points: ", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false)
                .append(Component.text(playerPoints, NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))));
        head.setItemMeta(skull);
        inventory.setItem(4, head);

        List<RedeemableItem> items = Points.get().getRedeemableItems();
        int slot = 9;
        for (RedeemableItem item : items) {
            ItemStack i = new ItemStack(item.getMaterial());
            ItemMeta meta = i.getItemMeta();
            if (item.getModelData() != -1) {
                meta.setCustomModelData(item.getModelData());
            }
            meta.displayName(i.displayName().color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, true).decoration(TextDecoration.ITALIC, false));
            List<Component> lore = List.of(
                    Component.empty(),
                    Component.text("Required Points: ", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false)
                            .append(Component.text(item.getRequiredPoints(), playerPoints >= item.getRequiredPoints() ? NamedTextColor.GREEN : NamedTextColor.RED))

            );
            meta.lore(lore);
            i.setItemMeta(meta);
            inventory.setItem(slot, i);
            slot++;
        }
    }

    @Override
    public void onSlotClicked(Player player, int slotNum, ItemStack itemClicked) {
        if (slotNum > 8 && slotNum < 36) {
            RedeemableItem clicked = redeemableItems.get(slotNum - 9);
            // yes this looks very stupid I know
            if (player.hasPermission(clicked.getPermission())) {
                player.playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
                player.closeInventory();
                ParallelChat.sendParallelMessageTo(player, "You have already redeemed that item!");
                return;
            }
            if (player.getInventory().firstEmpty() == -1) {
                player.playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
                player.closeInventory();
                ParallelChat.sendParallelMessageTo(player, "Your inventory is too full!");
                return;
            }
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            player.closeInventory();
            ParallelChat.sendParallelMessageTo(player, Component.text("Successfully redeemed the ", NamedTextColor.GREEN).append(itemClicked.displayName()));
            for (String s : redeemableItems.get(slotNum - 9).getCommands()) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player%", player.getName()));
            }
        }
        if (slotNum == 49) {
            player.closeInventory();
        }
    }
}
