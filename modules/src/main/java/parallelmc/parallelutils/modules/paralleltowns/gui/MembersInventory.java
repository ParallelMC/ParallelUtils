package parallelmc.parallelutils.modules.paralleltowns.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.paralleltowns.ParallelTowns;
import parallelmc.parallelutils.modules.paralleltowns.Town;
import parallelmc.parallelutils.modules.paralleltowns.TownMember;
import parallelmc.parallelutils.modules.paralleltowns.TownRank;

import java.util.*;
import java.util.logging.Level;

public class MembersInventory extends GUIInventory {

    private static final int MAP_INDEX = 4;
    private static final int EXIT_INDEX = 49;

    public MembersInventory() {
        super(54, Component.text("Town Members", NamedTextColor.DARK_AQUA, TextDecoration.BOLD));

        ItemStack map = new ItemStack(Material.MAP);
        ItemMeta meta = map.getItemMeta();
        meta.displayName(Component.text("Town Members", NamedTextColor.YELLOW, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("View all of your town's", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("members below!", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        map.setItemMeta(meta);

        ItemStack exit = new ItemStack(Material.BARRIER);
        meta = exit.getItemMeta();
        meta.displayName(Component.text("Back to Town GUI", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        exit.setItemMeta(meta);

        inventory.setItem(MAP_INDEX, map);
        inventory.setItem(EXIT_INDEX, exit);
    }

    @Override
    public void onOpen(Player player) {
        // load player skulls in gui when opened
        Town town = ParallelTowns.get().getPlayerTown(player);
        // sure hope this is somewhat efficient
        List<UUID> members = town.getMembers()
                .entrySet().stream()
                .sorted(Comparator.comparingInt(x -> x.getValue().getTownRank()))
                .map(Map.Entry::getKey)
                .toList();
        int slot = 9;
        // since sorted() sorts ascending and reversed() causes issues, we get to loop through the list backwards
        for (int i = members.size() - 1; i >= 0; i--) {
            UUID member = members.get(i);
            OfflinePlayer p = Bukkit.getOfflinePlayer(member);
            if (p.getName() == null) {
                ParallelUtils.log(Level.WARNING, "Could not get name for UUID " + member + " when querying town " + town.getName());
                continue;
            }
            TownMember tm = ParallelTowns.get().getPlayerTownStatus(member);
            if (tm == null) {
                ParallelUtils.log(Level.WARNING, "Could not get town member status for UUID " + member + " when querying town " + town.getName());
                continue;
            }
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta)head.getItemMeta();
            meta.setOwningPlayer(p);
            meta.displayName(Component.text(p.getName(), NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
            List<Component> lore = new ArrayList<>();
            lore.add(getComponentForRank(tm.getTownRank()));
            if (tm.getIsFounder())
                lore.add(Component.text("Town Founder", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.empty());
            if (p.isOnline())
                lore.add(Component.text("Online!", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
            else
                // TODO: add offline since data
                lore.add(Component.text("Offline", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);
            head.setItemMeta(meta);
            inventory.setItem(slot, head);
            slot++;
            // TODO: add pagination when necessary
            if (slot >= 44) {
                break;
            }
        }
    }

    @Override
    public void onSlotClicked(Player player, int slotNum, ItemStack itemClicked) {
        if (itemClicked.getType() == Material.PLAYER_HEAD) {
            TownMember member = ParallelTowns.get().getPlayerTownStatus(player);
            // only leaders and officials can open the member options gui
            if (member.getTownRank() != TownRank.MEMBER) {
                SkullMeta meta = (SkullMeta) itemClicked.getItemMeta();
                ParallelTowns.get().guiManager.openMemberOptionsMenuForPlayer(player, meta.getOwningPlayer());
            }
        }
        if (slotNum == 49) {
            ParallelTowns.get().guiManager.openMainMenuForPlayer(player);
        }
    }
}
