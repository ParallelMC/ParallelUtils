package parallelmc.parallelutils.modules.paralleltowns.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.paralleltowns.ParallelTowns;
import parallelmc.parallelutils.modules.paralleltowns.Town;
import parallelmc.parallelutils.modules.paralleltowns.TownMember;
import parallelmc.parallelutils.modules.paralleltowns.TownRank;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class MemberOptionsInventory extends GUIInventory {

    private static final int PROMOTE_INDEX = 11;
    private static final int DEMOTE_INDEX = 12;
    private static final int RETIRE_INDEX = 13;

    // the town member that this menu is modifying
    private final OfflinePlayer townMember;

    // suppress warnings for member.getName() calls as they will never be null
    // see MembersInventory.java:L56
    @SuppressWarnings("ConstantConditions")
    public MemberOptionsInventory(OfflinePlayer member) {
        super(18, Component.text(member.getName(), NamedTextColor.DARK_AQUA, TextDecoration.BOLD));
        this.townMember = member;

        TownMember tm = ParallelTowns.get().getPlayerTownStatus(member.getUniqueId());
        if (tm == null) {
            ParallelUtils.log(Level.WARNING, "Could not get town member status for UUID " + member);
            return;
        }
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta smeta = (SkullMeta)head.getItemMeta();
        smeta.setOwningPlayer(member);
        smeta.displayName(Component.text(member.getName(), NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        List<Component> lore = new ArrayList<>();
        lore.add(getComponentForRank(tm.getTownRank()));
        if (tm.getIsFounder())
            lore.add(Component.text("Town Founder", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());
        if (member.isOnline())
            lore.add(Component.text("Online!", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
        else
            // TODO: add offline since data
            lore.add(Component.text("Offline", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        smeta.lore(lore);
        head.setItemMeta(smeta);

        ItemStack evict = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta meta = evict.getItemMeta();
        meta.displayName(Component.text("Evict Player", NamedTextColor.DARK_RED, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        lore.clear();
        lore.add(Component.text("Click here to evict " + member.getName(), NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("from your town!", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        evict.setItemMeta(meta);

        ItemStack exit = new ItemStack(Material.BARRIER);
        meta = exit.getItemMeta();
        meta.displayName(Component.text("Back to Members List", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        exit.setItemMeta(meta);

        inventory.setContents(new ItemStack[] {
                PLACEHOLDER,
                PLACEHOLDER,
                PLACEHOLDER,
                PLACEHOLDER,
                head,
                PLACEHOLDER,
                PLACEHOLDER,
                PLACEHOLDER,
                PLACEHOLDER,
                PLACEHOLDER,
                PLACEHOLDER,
                new ItemStack(Material.AIR),
                new ItemStack(Material.AIR),
                new ItemStack(Material.AIR),
                evict,
                exit,
                PLACEHOLDER,
                PLACEHOLDER
        });
    }

    @Override
    public void onOpen(Player player) {
        TownMember member = ParallelTowns.get().getPlayerTownStatus(player);
        if (player.getUniqueId().equals(townMember.getUniqueId()) && member.getTownRank() != TownRank.MEMBER) {
            ItemStack retire = new ItemStack(Material.RED_DYE);
            ItemMeta meta = retire.getItemMeta();
            meta.displayName(Component.text("Retire Position", NamedTextColor.DARK_RED, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Click here to step down", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.text("from your position!", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);
            retire.setItemMeta(meta);
            inventory.setItem(RETIRE_INDEX, retire);
        }
        // only leaders can promote/demote players
        else if (member.getTownRank() == TownRank.LEADER) {
            ItemStack promote = new ItemStack(Material.GREEN_DYE);
            ItemMeta meta = promote.getItemMeta();
            meta.displayName(Component.text("Promote", NamedTextColor.DARK_GREEN, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Click here to promote", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.text(townMember.getName() + "!", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);
            promote.setItemMeta(meta);

            ItemStack demote = new ItemStack(Material.RED_DYE);
            meta = demote.getItemMeta();
            meta.displayName(Component.text("Demote", NamedTextColor.RED, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
            lore.clear();
            lore.add(Component.text("Click here to demote", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.text(townMember.getName() + "!", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);
            demote.setItemMeta(meta);

            inventory.setItem(PROMOTE_INDEX, promote);
            inventory.setItem(DEMOTE_INDEX, demote);
        }
    }

    @Override
    public void onSlotClicked(Player player, int slotNum, ItemStack itemClicked) {
        Town town = ParallelTowns.get().getPlayerTown(player);
        switch (slotNum) {
            case 11 -> ParallelTowns.get().guiManager.openTownMemberConfirmationForPlayer(player, town, townMember, ConfirmationAction.PROMOTE);
            case 12 -> {
                TownMember member = town.getMember(townMember.getUniqueId());
                if (member.getTownRank() == TownRank.LEADER) {
                    player.closeInventory();
                    ParallelChat.sendParallelMessageTo(player, "You cannot demote other leaders!");
                    return;
                }
                ParallelTowns.get().guiManager.openTownMemberConfirmationForPlayer(player, town, townMember, ConfirmationAction.DEMOTE);
            }
            case 13 -> {
                TownMember member = town.getMember(player);
                // there must be at least one leader in the town
                if (member.getTownRank() == TownRank.LEADER && town.getMembers().values().stream().filter(x -> x.getTownRank() == TownRank.LEADER).count() == 1) {
                    player.closeInventory();
                    ParallelChat.sendParallelMessageTo(player, "You cannot retire, you are the only leader!");
                    return;
                }
                ParallelTowns.get().guiManager.openTownConfirmationForPlayer(player, town, ConfirmationAction.RETIRE);
            }
            case 14 -> {
                if (player.getUniqueId().equals(townMember.getUniqueId())) {
                    player.closeInventory();
                    ParallelChat.sendParallelMessageTo(player, "You cannot evict yourself! Use the leave button in the options menu to leave.");
                    return;
                }
                TownMember pl = town.getMember(player);
                TownMember member = town.getMember(townMember.getUniqueId());
                if (pl.getTownRank() <= member.getTownRank()) {
                    player.closeInventory();
                    ParallelChat.sendParallelMessageTo(player, "You can only evict players that are a lower rank than you!");
                    return;
                }
                ParallelTowns.get().guiManager.openTownMemberConfirmationForPlayer(player, town, townMember, ConfirmationAction.EVICT);
            }
            case 15 -> ParallelTowns.get().guiManager.openMembersMenuForPlayer(player);
        }
    }
}
