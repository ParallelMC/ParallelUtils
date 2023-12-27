package parallelmc.parallelutils.modules.parallelquests.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelquests.ParallelQuests;
import parallelmc.parallelutils.modules.parallelquests.QuestComparator;
import parallelmc.parallelutils.modules.parallelquests.QuestEntry;
import parallelmc.parallelutils.util.GUIInventory;
import parallelmc.parallelutils.util.GUIManager;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class QuestBoardInventory extends GUIInventory {

    private final ParallelUtils puPlugin;

    public QuestBoardInventory(ParallelUtils puPlugin) {
        super(54, Component.text("Quest Board", NamedTextColor.DARK_PURPLE));

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, PLACEHOLDER_PURPLE);
        }
        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, PLACEHOLDER_PURPLE);
        }

        this.puPlugin = puPlugin;
    }

    @Override
    public void onOpen(Player player) {
        List<QuestEntry> quests = ParallelQuests.get().getAllQuests();
        Profile profile = PlayerConverter.getID(player);
        List<ItemStack> items = new ArrayList<>();
        for (QuestEntry q : quests) {
            QuestPackage pkg = Config.getPackages().get(q.id());
            if (pkg == null) {
                ParallelUtils.log(Level.SEVERE, "Failed to get package for quest ID " + q.id());
                continue;
            }

            try {
                if (q.available()) {
                    if (BetonQuest.condition(profile, new ConditionID(pkg, "Parallel." + q.id() + ".isQuestDone"))) {
                        items.add(getQuestBlock(q, "red"));
                    }
                    else {
                        if (BetonQuest.condition(profile, new ConditionID(pkg, "Parallel." + q.id() + ".isQuestActive"))) {
                            items.add(getQuestBlock(q, "yellow"));
                        }
                        else {
                            items.add(getQuestBlock(q, "green"));
                        }
                    }
                }
                else {
                    items.add(getQuestBlock(q, "black"));
                }
            } catch (ObjectNotFoundException e) {
                ParallelUtils.log(Level.WARNING, "Failed to process quest ID " + q.id() + ", skipping!");
            }
        }

        List<ItemStack> sorted = items.stream().sorted(new QuestComparator()).toList();
        int currentSlot = 10;
        for (ItemStack s : sorted) {
            inventory.setItem(currentSlot, s);
            currentSlot++;
        }
    }

    @Override
    public void onSlotClicked(Player player, int slotNum, ItemStack itemClicked) {
        if (slotNum > 8 && slotNum < 45) {
            ItemMeta meta = itemClicked.getItemMeta();
            NamespacedKey key = new NamespacedKey(puPlugin, "QuestID");
            if (meta.getPersistentDataContainer().has(key)) {
                String id = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                if (itemClicked.getType() == Material.GREEN_CONCRETE) {
                    openQuestConfirmationInventoryForPlayer(player, id, QuestConfirmationAction.START_QUEST);
                } else if (itemClicked.getType() == Material.YELLOW_CONCRETE) {
                    openQuestConfirmationInventoryForPlayer(player, id, QuestConfirmationAction.CANCEL_QUEST);
                }
            }
        }
    }

    private ItemStack getQuestBlock(QuestEntry q, String color) {
        ItemStack item = new ItemStack(Material.BLACK_CONCRETE);
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = new ArrayList<>();
        switch (color) {
            case "black" -> {
                meta.displayName(Component.text(q.title(), NamedTextColor.DARK_GRAY, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
                lore.add(Component.text("Quest Unavailable", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            }
            case "red" -> {
                meta.displayName(Component.text(q.title(), NamedTextColor.DARK_RED, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
                lore.add(Component.text("Quest Complete!", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
            }
            case "yellow" -> {
                meta.displayName(Component.text(q.title(), NamedTextColor.YELLOW, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
                lore.add(Component.text("Quest Active!", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false));
            }
            case "green" -> {
                meta.displayName(Component.text(q.title(), NamedTextColor.DARK_GREEN, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
                lore.add(Component.text("Quest Available!", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
            }
        }
        lore.add(Component.empty());
        lore.addAll(getQuestDescription(q));
        meta.lore(lore);
        meta.getPersistentDataContainer().set(new NamespacedKey(puPlugin, "QuestID"), PersistentDataType.STRING, q.id());
        item.setItemMeta(meta);
        return item;
    }

    private List<Component> getQuestDescription(QuestEntry q) {
        List<Component> result = new ArrayList<>();
        for (String s : q.description()) {
            result.add(Component.text(s, NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        }
        return result;
    }

    private void openQuestConfirmationInventoryForPlayer(Player player, String questID, QuestConfirmationAction action) {
        GUIManager.get().openInventoryForPlayer(player, new QuestConfirmationInventory(questID, action));
    }
}
