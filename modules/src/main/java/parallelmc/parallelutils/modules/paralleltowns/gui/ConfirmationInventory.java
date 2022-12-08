package parallelmc.parallelutils.modules.paralleltowns.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.units.qual.C;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.paralleltowns.ParallelTowns;
import parallelmc.parallelutils.modules.paralleltowns.Town;
import parallelmc.parallelutils.modules.paralleltowns.TownMember;

import java.util.EnumSet;
import java.util.logging.Level;


public class ConfirmationInventory extends GUIInventory {

    // the town member that this confirmation pertains to, if any
    private final OfflinePlayer townMember;

    // the town that this confirmation pertains to, if any
    private final Town town;

    // the action that this confirmation will execute if the confirmation is accepted
    private final ConfirmationAction action;

    private static final EnumSet<ConfirmationAction> townActions = EnumSet.of(ConfirmationAction.DELETE, ConfirmationAction.LEAVE,
            ConfirmationAction.CHARTER, ConfirmationAction.RETIRE, ConfirmationAction.DISPLAY, ConfirmationAction.STATUS, ConfirmationAction.JOIN);

    public ConfirmationInventory(Town town, OfflinePlayer member, ConfirmationAction action) {
        super(9, Component.text("Confirmation", NamedTextColor.DARK_AQUA, TextDecoration.BOLD));
        this.townMember = member;
        this.town = town;
        this.action = action;

        if (townActions.contains(action)) {
            ParallelUtils.log(Level.SEVERE, "Attempted to initialize a Confirmation with an invalid action! (" + action + ")");
            return;
        }

        ItemStack yes = new ItemStack(Material.LIME_WOOL);
        ItemMeta meta = yes.getItemMeta();
        meta.displayName(Component.text("Yes!", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
        yes.setItemMeta(meta);

        ItemStack no = new ItemStack(Material.RED_WOOL);
        meta = no.getItemMeta();
        meta.displayName(Component.text("No!", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        no.setItemMeta(meta);

        ItemStack paper = new ItemStack(Material.PAPER);
        meta = paper.getItemMeta();
        meta.displayName(Component.text("Are you sure you want to " + action + " " + member.getName() + "?", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        paper.setItemMeta(meta);

        ItemStack air = new ItemStack(Material.AIR);

        inventory.setContents(new ItemStack[] {
                air,
                air,
                yes,
                air,
                paper,
                air,
                no,
                air,
                air
        });
    }

    public ConfirmationInventory(Town town, ConfirmationAction action) {
        super(9, Component.text("Confirmation", NamedTextColor.DARK_AQUA, TextDecoration.BOLD));
        this.townMember = null;
        this.town = town;
        this.action = action;

        if (!townActions.contains(action)) {
            ParallelUtils.log(Level.SEVERE, "Attempted to initialize a Confirmation with an invalid action! (" + action + ")");
            return;
        }

        ItemStack yes = new ItemStack(Material.LIME_WOOL);
        ItemMeta meta = yes.getItemMeta();
        meta.displayName(Component.text("Yes!", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
        yes.setItemMeta(meta);

        ItemStack no = new ItemStack(Material.RED_WOOL);
        meta = no.getItemMeta();
        meta.displayName(Component.text("No!", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        no.setItemMeta(meta);

        ItemStack paper = new ItemStack(Material.PAPER);
        meta = paper.getItemMeta();
        switch (action) {
            case JOIN -> meta.displayName(Component.text("Are you sure you want to join this town?", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            case LEAVE -> meta.displayName(Component.text("Are you sure you want to leave the town?", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            case CHARTER -> meta.displayName(Component.text("Are you sure you want to update the town charter?", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            case RETIRE -> meta.displayName(Component.text("Are you sure you want to retire from your position?", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            case DISPLAY -> meta.displayName(Component.text("Are you sure you want to update the town list item?", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            case STATUS -> meta.displayName(Component.text("Are you sure you want to toggle the town status?", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            case DELETE ->  meta.displayName(Component.text("Are you sure you want to delete the town?", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            default -> {
                ParallelUtils.log(Level.SEVERE, "Unhandled action in confirmation: " + action);
                return;
            }
        }
        paper.setItemMeta(meta);

        ItemStack air = new ItemStack(Material.AIR);

        inventory.setContents(new ItemStack[] {
                air,
                air,
                yes,
                air,
                paper,
                air,
                no,
                air,
                air
        });
    }


    @Override
    public void onOpen(Player player) { }

    @Override
    public void onSlotClicked(Player player, int slotNum, ItemStack itemClicked) {
        switch (slotNum) {
            case 2 -> {
                player.closeInventory();
                switch (action) {
                    case PROMOTE -> {
                        if (town.promoteMember(townMember.getUniqueId())) {
                            ParallelChat.sendParallelMessageTo(player, townMember.getName() + " was successfully promoted!");
                            TownMember member = town.getMember(townMember.getUniqueId());
                            town.sendMessage(townMember.getName() + " was promoted to " + member.getTownRankStr() + " by " + player.getName() + "!", NamedTextColor.GREEN);
                        }
                        else {
                            ParallelChat.sendParallelMessageTo(player, "Unable to promote " + townMember.getName() + ", they are already the highest rank.");
                        }
                    }
                    case DEMOTE -> {
                        if (town.demoteMember(townMember.getUniqueId())) {
                            ParallelChat.sendParallelMessageTo(player, townMember.getName() + " was successfully demoted!");
                            TownMember member = town.getMember(townMember.getUniqueId());
                            town.sendMessage(townMember.getName() + " was demoted to " + member.getTownRankStr() + " by " + player.getName() + ".", NamedTextColor.RED);
                        }
                        else {
                            ParallelChat.sendParallelMessageTo(player, "Unable to demote " + townMember.getName() + ", they are already the lowest rank.");
                        }
                    }
                    case EVICT -> {
                        if (townMember.isOnline()) {
                            // warning can be ignored, isOnline() checks for this
                            townMember.getPlayer().sendMessage(Component.text("[" + town.getName() + "]: ", NamedTextColor.GOLD)
                                    .append(Component.text("You have been evicted from the town!", NamedTextColor.RED)));
                        }
                        ParallelTowns.get().removePlayerFromTown(townMember.getUniqueId(), town);
                        ParallelChat.sendParallelMessageTo(player, townMember.getName() + " was successfully evicted!");
                        town.sendMessage(townMember.getName() + " was evicted by " + player.getName() + ".", NamedTextColor.RED);
                    }
                    case DELETE -> {
                        town.sendMessage("The town has been deleted by " + player.getName() + ".", NamedTextColor.RED);
                        ParallelTowns.get().deleteTown(town.getName());
                        ParallelChat.sendParallelMessageTo(player, "Town " + town.getName() + " was deleted.");
                    }
                    case JOIN -> {
                        ParallelTowns.get().addPlayerToTown(player, town);
                    }
                    case LEAVE -> {
                        town.sendMessage(player.getName() + " has left the town.", NamedTextColor.RED);
                        ParallelTowns.get().removePlayerFromTown(player.getUniqueId(), town);
                    }
                    case CHARTER -> {
                        ItemStack item = player.getInventory().getItemInMainHand();
                        if (item.getType() != Material.WRITABLE_BOOK) {
                            ParallelUtils.log(Level.SEVERE, "Attempted to accept a town charter update while the player was not holding a book!");
                            return;
                        }
                        town.sendMessage("The town charter has been updated by " + player.getName() + "!", NamedTextColor.YELLOW);
                        BookMeta meta = (BookMeta)item.getItemMeta();
                        town.setCharter(meta.pages());
                    }
                    case DISPLAY -> {
                        ItemStack item = player.getInventory().getItemInMainHand();
                        if (item.getType() == Material.AIR) {
                            ParallelUtils.log(Level.SEVERE, "Attempted to accept a town list item update while the player was not holding an item!");
                            return;
                        }
                        town.sendMessage("The town display item has been updated by " + player.getName() + " to a " + item.getType() + "!", NamedTextColor.YELLOW);
                        int modelData = -1;
                        ItemMeta meta = item.getItemMeta();
                        if (meta.hasCustomModelData()) {
                            modelData = meta.getCustomModelData();
                        }
                        town.setDisplayItem(item.getType(), modelData);
                    }
                    case STATUS -> {
                        boolean status = !town.isOpen();
                        town.setIsOpen(status);
                        town.sendMessage("The town status has been set to " + (status ? "Open" : "Invite Only") + " by " + player.getName() + "!", NamedTextColor.YELLOW);
                    }
                    case RETIRE -> {
                        String rank = town.getMember(player).getTownRankStr();
                        town.demoteMember(player.getUniqueId());
                        town.sendMessage(player.getName() + " has retired from their position of " + rank + "!", NamedTextColor.YELLOW);
                    }
                }
            }
            case 6 -> ParallelTowns.get().guiManager.openMainMenuForPlayer(player);
            default -> { }
        }
    }
}
