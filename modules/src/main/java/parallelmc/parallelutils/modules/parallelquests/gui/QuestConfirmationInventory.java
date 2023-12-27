package parallelmc.parallelutils.modules.parallelquests.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelquests.ParallelQuests;
import parallelmc.parallelutils.util.GUIInventory;

import java.util.logging.Level;

// TODO: add this functionality to main GUI API
public class QuestConfirmationInventory extends GUIInventory {

    private final String questID;

    private final QuestConfirmationAction action;

    public QuestConfirmationInventory(String questID, QuestConfirmationAction action) {
        super(9, Component.text("Confirmation", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD));

        ItemStack yes = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta meta = yes.getItemMeta();
        meta.displayName(Component.text("Yes", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
        yes.setItemMeta(meta);
        inventory.setItem(2, yes);

        ItemStack no = new ItemStack(Material.RED_CONCRETE);
        meta = no.getItemMeta();
        meta.displayName(Component.text("No", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        no.setItemMeta(meta);
        inventory.setItem(6, no);

        ItemStack paper = new ItemStack(Material.PAPER);
        meta = paper.getItemMeta();
        if (action == QuestConfirmationAction.START_QUEST)
            meta.displayName(Component.text("Are you sure you want to start this quest?", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        else if (action == QuestConfirmationAction.CANCEL_QUEST)
            meta.displayName(Component.text("Are you sure you want to cancel this quest?", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        paper.setItemMeta(meta);
        inventory.setItem(4, paper);

        this.questID = questID;
        this.action = action;
    }

    @Override
    public void onOpen(Player player) { }

    @Override
    public void onSlotClicked(Player player, int slotNum, ItemStack itemClicked) {
        if (slotNum == 2) {
            player.closeInventory();
            Profile profile = PlayerConverter.getID(player);
            QuestPackage pkg = Config.getPackages().get(questID);
            if (pkg == null) {
                ParallelUtils.log(Level.SEVERE, "Failed to get quest package for quest ID " + questID);
                return;
            }
            if (action == QuestConfirmationAction.START_QUEST) {
                try {
                    BetonQuest.event(profile, new EventID(pkg,questID + ".startQuestFolder"));
                } catch (ObjectNotFoundException e) {
                    ParallelUtils.log(Level.SEVERE, "Failed to start quest with quest ID " + questID);
                }
            }
            else if (action == QuestConfirmationAction.CANCEL_QUEST) {
                try {
                    BetonQuest.event(profile, new EventID(pkg,questID + ".cancelQuest"));
                } catch (ObjectNotFoundException e) {
                    ParallelUtils.log(Level.SEVERE, "Failed to cancel quest with quest ID " + questID);
                }
            }
        }
        else if (slotNum == 6) {
            ParallelQuests.get().openQuestInventoryForPlayer(player);
        }
    }
}
