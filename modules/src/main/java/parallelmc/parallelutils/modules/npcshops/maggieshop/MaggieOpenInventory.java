package parallelmc.parallelutils.modules.npcshops.maggieshop;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import parallelmc.parallelutils.modules.npcshops.NPCShops;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.util.EconomyManager;
import parallelmc.parallelutils.util.GUIInventory;

import java.util.ArrayList;
import java.util.List;

public class MaggieOpenInventory extends GUIInventory {

    public MaggieOpenInventory() {
        super(54, Component.text("Maggie's Charms: Open Shop", NamedTextColor.YELLOW));

        inventory.setItem(45, PLACEHOLDER_YELLOW);
        inventory.setItem(46, PLACEHOLDER_YELLOW);
        inventory.setItem(47, PLACEHOLDER_YELLOW);
        inventory.setItem(51, PLACEHOLDER_YELLOW);
        inventory.setItem(52, PLACEHOLDER_YELLOW);
        inventory.setItem(53, PLACEHOLDER_YELLOW);

        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta meta = back.getItemMeta();
        meta.displayName(Component.text("Go Back", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        back.setItemMeta(meta);
        inventory.setItem(49, back);
    }

    @Override
    public void onOpen(Player player) {
        ItemStack charm = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = charm.getItemMeta();
        meta.displayName(Component.text("Charm Applicator", NamedTextColor.YELLOW));
        meta.setCustomModelData(1000000);
        List<Component> lore = new ArrayList<>();
        List<ShopCharm> charms = NPCShops.get().getMaggieShop().getAllOpenCharms();
        for (int i = 0; i < charms.size(); i++) {
            ShopCharm c = charms.get(i);
            lore.clear();
            lore.add(Component.text(c.charmName(), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            for (String s : c.lore()) {
                lore.add(LegacyComponentSerializer.legacyAmpersand().deserialize(s));
            }
            lore.add(Component.text("Costs ", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)
                    .append(Component.text(c.price() + " riftcoins", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false)));
            meta.lore(lore);
            charm.setItemMeta(meta);
            inventory.setItem(i, charm);
        }
    }

    @Override
    public void onSlotClicked(Player player, int slotNum, ItemStack itemClicked) {
        if (slotNum == 49) {
            NPCShops.get().getMaggieShop().openShopFor(player);
        }
        else {
            ShopCharm charm = NPCShops.get().getMaggieShop().getOpenCharm(slotNum);
            if (EconomyManager.get().getBalance(player) < charm.price()) {
                ParallelChat.sendParallelMessageTo(player, Component.text("You don't have enough riftcoins!", NamedTextColor.RED));
            }
            else if (player.getInventory().firstEmpty() == -1) {
                ParallelChat.sendParallelMessageTo(player, Component.text("You don't have enough inventory space!", NamedTextColor.RED));
            }
            else {
                EconomyManager.get().removeRiftcoins(player, charm.price());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("pu givecharm %s %s", player.getName(), charm.charmId()));
                ParallelChat.sendParallelMessageTo(player,
                        MiniMessage.miniMessage().deserialize(String.format("<aqua>You bought a <yellow>Charm Applicator <aqua>(%s) for <orange> %d riftcoins!", charm.charmName(), charm.price())));
            }
        }
    }
}
