package parallelmc.parallelutils.modules.parallelchat.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.parallelchat.messages.JoinLeaveMessage;
import parallelmc.parallelutils.util.GUIInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JoinLeaveSelectInventory extends GUIInventory {
    private static final int MAP_INDEX = 4;
    private static final int DISABLE_INDEX = 48;
    private static final int EXIT_INDEX = 50;
    private final String EVENT;

    public JoinLeaveSelectInventory(String event) {
        super(54, Component.text(event + "Message Selection", NamedTextColor.DARK_AQUA, TextDecoration.BOLD));

        EVENT = event;

        ItemStack map = new ItemStack(Material.MAP);
        ItemMeta meta = map.getItemMeta();
        meta.displayName(Component.text(event + " Messages"));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Click a " + event + " message", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("to enable it!", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        map.setItemMeta(meta);

        ItemStack disable = new ItemStack(Material.NAME_TAG);
        meta = disable.getItemMeta();
        meta.displayName(Component.text("Disable" + event + " Message", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        disable.setItemMeta(meta);

        ItemStack exit = new ItemStack(Material.BARRIER);
        meta = exit.getItemMeta();
        meta.displayName(Component.text("Exit GUI", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        exit.setItemMeta(meta);

        inventory.setItem(MAP_INDEX, map);
        inventory.setItem(DISABLE_INDEX, disable);
        inventory.setItem(EXIT_INDEX, exit);
    }

    @Override
    public void onOpen(Player player) {
        HashMap<String, JoinLeaveMessage> messages = ParallelChat.get().customMessageManager.getCustomJoinLeaveMessages();
        int slot = 9;
        for (Map.Entry<String, JoinLeaveMessage> m : messages.entrySet().stream().filter(x -> x.getValue().event().equalsIgnoreCase(EVENT)).toList()) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text(m.getKey(), NamedTextColor.YELLOW, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
            JoinLeaveMessage msg = m.getValue();
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Required Rank: " + msg.requiredRank().toUpperCase(), NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.empty());
            lore.add(Component.text("Text: ", NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false)
                    .append(Component.text(msg.text(), NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)));
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
            player.closeInventory();
            return;
        }

        if (slotNum == DISABLE_INDEX) {
            if (EVENT.equalsIgnoreCase("join"))
                ParallelChat.get().customMessageManager.disableJoinMessage(player);
            else
                ParallelChat.get().customMessageManager.disableLeaveMessage(player);
            ParallelChat.sendParallelMessageTo(player, "Disabled your custom " + EVENT + " message!");
            return;
        }

        String name = PlainTextComponentSerializer.plainText().serialize(itemClicked.displayName());
        // PlainTextComponentSerializer converts bold text to brackets
        // i.e. <bold>Hello --> [Hello]
        // so we have to remove them here
        name = name.substring(1, name.length() - 1);
        String rank = ParallelChat.get().customMessageManager.getRequiredRankForMessage(name);
        if (!player.hasPermission("group." + rank)) {
            ParallelChat.sendParallelMessageTo(player, "You need " + rank.toUpperCase() + " rank to unlock this " + EVENT + " message!");
            return;
        }
        if (EVENT.equalsIgnoreCase("join"))
            ParallelChat.get().customMessageManager.selectJoinMessage(player, name);
        else
            ParallelChat.get().customMessageManager.selectLeaveMessage(player, name);
        ParallelChat.sendParallelMessageTo(player, "Enabled the " + name + " " + EVENT + " message!");
        player.closeInventory();
    }
}
