package parallelmc.parallelutils.modules.parallelchat.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.parallelchat.messages.JoinLeaveMessage;
import parallelmc.parallelutils.util.GUIInventory;

import java.util.*;
import java.util.logging.Level;

public class JoinLeaveSelectInventory extends GUIInventory {
    private static final int MAP_INDEX = 4;
    private static final int DISABLE_INDEX = 48;
    private static final int EXIT_INDEX = 50;
    private final String EVENT;

    private static final NamespacedKey KEY = new NamespacedKey("parallelutils", "message_id");

    public JoinLeaveSelectInventory(String event) {
        super(54, Component.text(event + " Message Selection", NamedTextColor.DARK_AQUA, TextDecoration.BOLD));

        EVENT = event;

        ItemStack map = new ItemStack(Material.MAP);
        ItemMeta meta = map.getItemMeta();
        meta.displayName(Component.text(event + " Messages",  NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Click a " + event + " message", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("to enable it!", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        map.setItemMeta(meta);

        ItemStack disable = new ItemStack(Material.NAME_TAG);
        meta = disable.getItemMeta();
        meta.displayName(Component.text("Disable " + event + " Message", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        disable.setItemMeta(meta);

        ItemStack exit = new ItemStack(Material.BARRIER);
        meta = exit.getItemMeta();
        meta.displayName(Component.text("Return to Main Menu", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        exit.setItemMeta(meta);

        inventory.setItem(MAP_INDEX, map);
        inventory.setItem(DISABLE_INDEX, disable);
        inventory.setItem(EXIT_INDEX, exit);
    }

    @Override
    public void onOpen(Player player) {
        HashMap<String, JoinLeaveMessage> messages = ParallelChat.get().customMessageManager.getCustomJoinLeaveMessages();
        int slot = 9;
        String selected;
        if (EVENT.equalsIgnoreCase("join"))
            selected = ParallelChat.get().customMessageManager.getJoinMessageNameForPlayer(player);
        else
            selected = ParallelChat.get().customMessageManager.getLeaveMessageNameForPlayer(player);
        for (Map.Entry<String, JoinLeaveMessage> m :
                messages.entrySet().stream().filter(x -> x.getValue().event().equalsIgnoreCase(EVENT)).sorted(new RankComparator()).toList()) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            JoinLeaveMessage msg = m.getValue();
            meta.displayName(Component.text(msg.name(), NamedTextColor.YELLOW, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
            meta.getPersistentDataContainer().set(KEY, PersistentDataType.STRING, m.getKey());
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Required Rank: " + msg.requiredRank().toUpperCase(), NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.empty());
            lore.add(Component.text("Text: ", NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false)
                    .append(Component.text(msg.text(), NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)));
            // if this message is selected by the player, make it glow
            if (selected != null && selected.equalsIgnoreCase(msg.name())) {
                meta.addEnchant(Enchantment.VANISHING_CURSE, 1, false);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                lore.add(Component.empty());
                lore.add(Component.text("SELECTED", NamedTextColor.GREEN, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
            }
            meta.lore(lore);
            item.setItemMeta(meta);
            inventory.setItem(slot, item);
            slot++;
            // TODO: add pagination if necessary
            if (slot >= 44) {
                break;
            }
        }
    }

    @Override
    public void onSlotClicked(Player player, int slotNum, ItemStack itemClicked) {
        if (slotNum == MAP_INDEX)
            return;

        if (slotNum == EXIT_INDEX) {
            ParallelChat.get().openMainJoinLeaveInventory(player);
            return;
        }

        if (slotNum == DISABLE_INDEX) {
            if (EVENT.equalsIgnoreCase("join"))
                ParallelChat.get().customMessageManager.disableJoinMessage(player);
            else
                ParallelChat.get().customMessageManager.disableLeaveMessage(player);
            ParallelChat.sendParallelMessageTo(player, "Disabled your custom " + EVENT + " message!");
            player.closeInventory();
            return;
        }

        String name = PlainTextComponentSerializer.plainText().serialize(itemClicked.displayName());
        // PlainTextComponentSerializer converts bold text to brackets
        // i.e. <bold>Hello --> [Hello]
        // so we have to remove them here
        name = name.substring(1, name.length() - 1);

        String id = itemClicked.getItemMeta().getPersistentDataContainer().get(KEY, PersistentDataType.STRING);
        if (id == null) {
            ParallelUtils.log(Level.SEVERE, "Clicked custom message has no message id NBT!");
            player.closeInventory();
            return;
        }
        String rank = ParallelChat.get().customMessageManager.getRequiredRankForMessage(id);
        if (!player.hasPermission("group." + rank)) {
            ParallelChat.sendParallelMessageTo(player, "You need " + rank.toUpperCase() + " rank to unlock this " + EVENT + " message!");
            return;
        }
        if (EVENT.equalsIgnoreCase("join"))
            ParallelChat.get().customMessageManager.selectJoinMessage(player, id);
        else
            ParallelChat.get().customMessageManager.selectLeaveMessage(player, id);
        ParallelChat.sendParallelMessageTo(player, "Enabled the " + name + " " + EVENT + " message!");
        player.closeInventory();
    }
}
