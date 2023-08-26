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
import org.eclipse.sisu.inject.Legacy;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.npcshops.NPCShops;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.util.EconomyManager;
import parallelmc.parallelutils.util.GUIInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class MaggieRankedInventory extends GUIInventory {

    public MaggieRankedInventory() {
        super(54, Component.text("Maggie's Charms: ", NamedTextColor.YELLOW).append(Component.text("Ranked Shop", NamedTextColor.LIGHT_PURPLE)));

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
        List<ShopCharm> charms = NPCShops.get().getMaggieShop().getAllRankedCharms();
        for (int i = 0; i < charms.size(); i++) {
            ShopCharm c = charms.get(i);
            lore.clear();
            lore.add(Component.text(c.charmName(), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            lore.addAll(c.lore());
            lore.add(Component.text("Costs ", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)
                    .append(Component.text(c.price() + " riftcoins", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false)));
            lore.add(Component.text("You must have a donator rank of ", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
                    .append(getFormattingFromPermission(c.requiredRank())));
            lore.add(Component.text("or higher to purchase this charm!", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
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
            ShopCharm charm = NPCShops.get().getMaggieShop().getRankedCharm(slotNum);
            if (charm == null) {
                ParallelUtils.log(Level.WARNING, "Charm is null!");
                return;
            }
            if (!player.hasPermission(charm.requiredRank())) {
                ParallelChat.sendParallelMessageTo(player, Component.text("You are missing the required rank for this charm!", NamedTextColor.RED));
            }
            if (EconomyManager.get().getBalance(player) < charm.price()) {
                ParallelChat.sendParallelMessageTo(player, Component.text("You don't have enough riftcoins!", NamedTextColor.RED));
            }
            else if (player.getInventory().firstEmpty() == -1) {
                ParallelChat.sendParallelMessageTo(player, Component.text("You don't have enough inventory space!", NamedTextColor.RED));
            }
            else {
                EconomyManager.get().removeRiftcoins(player, charm.price());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("pu givecharm %s %s", player.getName(), charm.charmName()));
                ParallelChat.sendParallelMessageTo(player,
                        MiniMessage.miniMessage().deserialize(String.format("<aqua>You bought a <yellow>Charm Applicator <aqua>(%s) for <orange> %d riftcoins!", charm.charmName(), charm.price())));
            }
        }
    }

    private Component getFormattingFromPermission(String permission) {
        return switch (permission.split("\\.")[2]) {
            case "bronze" -> MiniMessage.miniMessage().deserialize("<bold><#cd7f32>Bronze");
            case "silver" -> MiniMessage.miniMessage().deserialize("<bold><#dbdbdb>Silver");
            case "gold" -> MiniMessage.miniMessage().deserialize("<bold><gold>Gold");
            case "diamond" -> MiniMessage.miniMessage().deserialize("<bold><aqua>Diamond");
            case "rift_master" ->
                // really don't want to convert this lol
                    LegacyComponentSerializer.legacySection().deserialize("&x&5&B&1&6&D&B&lR&x&6&4&1&8&D&C&li&x&6&B&1&B&D&C&lf&x&7&3&1&D&D&D&lt &x&7&A&2&0&D&D&lM&x&8&1&2&3&D&E&la&x&8&7&2&6&D&E&ls&x&8&D&2&9&D&F&lt&x&9&3&2&C&D&F&le&x&9&9&2&F&E&0&lr");
            default -> Component.empty();
        };
    }
}
