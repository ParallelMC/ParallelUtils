package parallelmc.parallelutils.modules.npcshops.maggieshop;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.mojang.authlib.properties.Property;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.npcshops.NPCShops;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.util.EconomyManager;
import parallelmc.parallelutils.util.GUIInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MaggieHomeInventory extends GUIInventory {
    private final ParallelUtils puPlugin;

    public MaggieHomeInventory(ParallelUtils puPlugin) {
        super(27, Component.text("Maggie's Charms", NamedTextColor.YELLOW));

        for (int i = 0; i < 27; i++)
            inventory.setItem(i, PLACEHOLDER_YELLOW);

        String url = "basehead-eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTA4OWY0ODU0YzczYTg4Yjg1ODQ3NWM5MTg2MzNjYjgxZWIyODJkYThlNzVhMTdkM2Y2ODAwODBjNThiNjVmNSJ9fX0=";
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta)head.getItemMeta();
        CraftPlayerProfile profile = new CraftPlayerProfile(UUID.randomUUID(), null);
        profile.setProperty("textures", new Property("textures", url));
        headMeta.setPlayerProfile(profile);
        head.setItemMeta(headMeta);

        inventory.setItem(14, head);

        ItemStack open = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = open.getItemMeta();
        meta.displayName(Component.text("Open Charms Shop", NamedTextColor.YELLOW, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("This shop is open to all ranks!", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());
        lore.add(Component.text("Click here to browse charms for sale!", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        open.setItemMeta(meta);

        inventory.setItem(16, open);

        ItemStack ranked = new ItemStack(Material.NAME_TAG);
        meta = ranked.getItemMeta();
        meta.displayName(Component.text("Ranked Charms Shop", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        lore.clear();
        lore.add(Component.text("This shop is open to voter and", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("donor-ranked players only!", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());
        lore.add(Component.text("Click here to browse charms for sale!", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        ranked.setItemMeta(meta);

        inventory.setItem(17, ranked);

        ItemStack remover = new ItemStack(Material.PAPER);
        meta = remover.getItemMeta();
        meta.displayName(Component.text("Charm Remover").decoration(TextDecoration.ITALIC, false));
        lore.clear();
        lore.add(Component.text("Costs ", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)
                .append(Component.text("20 riftcoins", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false)));
        lore.add(Component.text("Remove your charms with this handy item!"));
        remover.lore(lore);
        meta.setCustomModelData(1000000);
        remover.setItemMeta(meta);

        inventory.setItem(18, remover);

        this.puPlugin = puPlugin;
    }

    @Override
    public void onOpen(Player player) { }

    @Override
    public void onSlotClicked(Player player, int slotNum, ItemStack itemClicked) {
        switch(slotNum) {
            case 16 -> {
                NPCShops.get().getMaggieShop().openOpenShopFor(player);
            }
            case 17 -> {
                NPCShops.get().getMaggieShop().openRankedShopFor(player);
            }
            case 18 -> {
                if (EconomyManager.get().getBalance(player) < 20) {
                    ParallelChat.sendParallelMessageTo(player, Component.text("You don't have enough riftcoins!", NamedTextColor.RED));
                }
                else if (player.getInventory().firstEmpty() == -1) {
                    ParallelChat.sendParallelMessageTo(player, Component.text("You don't have enough inventory space!", NamedTextColor.RED));
                }
                else {
                    EconomyManager.get().removeRiftcoins(player, 20);
                    puPlugin.getServer().dispatchCommand(puPlugin.getServer().getConsoleSender(), "pu giveremover " + player.getName());
                    ParallelChat.sendParallelMessageTo(player, Component.text("You purchased a ", NamedTextColor.AQUA)
                            .append(Component.text(" Charm Remover!", NamedTextColor.WHITE)));
                }
            }
        }
    }
}
