package parallelmc.parallelutils.modules.npcshops.maggieshop;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.mojang.authlib.properties.Property;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
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
        super(54, Component.text("Maggie's Charms", NamedTextColor.YELLOW));

        for (int i = 0; i < 54; i++) {
            inventory.setItem(i, PLACEHOLDER_YELLOW);
        }

        String url = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTA4OWY0ODU0YzczYTg4Yjg1ODQ3NWM5MTg2MzNjYjgxZWIyODJkYThlNzVhMTdkM2Y2ODAwODBjNThiNjVmNSJ9fX0=";
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta)head.getItemMeta();
        headMeta.displayName(Component.text("Maggie", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Welcome to my charms shop!", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("Spruce up your items here!", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        headMeta.lore(lore);
        CraftPlayerProfile profile = new CraftPlayerProfile(UUID.randomUUID(), null);
        profile.setProperty("textures", new Property("textures", url));
        headMeta.setPlayerProfile(profile);
        head.setItemMeta(headMeta);

        inventory.setItem(12, head);

        ItemStack remover = new ItemStack(Material.PAPER);
        ItemMeta meta = remover.getItemMeta();
        meta.displayName(Component.text("Charm Remover", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        lore.clear();
        lore.add(Component.text("Costs ", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)
                .append(Component.text("20 riftcoins", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false)));
        lore.add(Component.text("Remove your charms with this handy item!"));
        meta.lore(lore);
        meta.setCustomModelData(1000000);
        remover.setItemMeta(meta);

        inventory.setItem(14, remover);

        ItemStack name = new ItemStack(Material.NAME_TAG);
        meta = name.getItemMeta();
        meta.displayName(Component.text("Item Name Styling Charms", NamedTextColor.WHITE, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        lore.clear();
        lore.add(Component.text("Click here to browse charms that", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("style the name of an item!", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        name.setItemMeta(meta);

        inventory.setItem(30, name);

        ItemStack kill = new ItemStack(Material.NETHERITE_SWORD);
        meta = kill.getItemMeta();
        meta.displayName(Component.text("Kill Message Styling Charms", NamedTextColor.RED, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        lore.clear();
        lore.add(Component.text("Click here to browse charms", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("that change kill messages!", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        kill.setItemMeta(meta);

        inventory.setItem(31, kill);

        ItemStack particle = new ItemStack(Material.BLAZE_POWDER);
        meta = particle.getItemMeta();
        meta.displayName(Component.text("Particle Trail Charms", NamedTextColor.GOLD, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        lore.clear();
        lore.add(Component.text("Click here to browse charms that", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("add particle trails behind you!", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        particle.setItemMeta(meta);

        inventory.setItem(32, particle);

        ItemStack arrow = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta potMeta = (PotionMeta)arrow.getItemMeta();
        potMeta.displayName(Component.text("Arrow Trail Charms", NamedTextColor.GREEN, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        lore.clear();
        lore.add(Component.text("Click here to browse charms that", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("add particle trails behind arrows!", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        potMeta.lore(lore);
        potMeta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
        potMeta.setColor(Color.GREEN);
        arrow.setItemMeta(potMeta);

        inventory.setItem(39, arrow);

        ItemStack shaped = new ItemStack(Material.DIAMOND_CHESTPLATE);
        meta = shaped.getItemMeta();
        meta.displayName(Component.text("Shaped Particle Charms", NamedTextColor.AQUA, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        lore.clear();
        lore.add(Component.text("Click here to browse charms that", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("add particle effects on your person!", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        shaped.setItemMeta(meta);

        inventory.setItem(40, shaped);

        ItemStack coming = new ItemStack(Material.PLAYER_HEAD);
        headMeta = (SkullMeta)coming.getItemMeta();
        headMeta.displayName(Component.text("???", NamedTextColor.YELLOW, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        lore.clear();
        lore.add(Component.text("Coming soon!", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        headMeta.lore(lore);
        profile = new CraftPlayerProfile(UUID.randomUUID(), null);
        url = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFkYzA0OGE3Y2U3OGY3ZGFkNzJhMDdkYTI3ZDg1YzA5MTY4ODFlNTUyMmVlZWQxZTNkYWYyMTdhMzhjMWEifX19";
        profile.setProperty("textures", new Property("textures", url));
        headMeta.setPlayerProfile(profile);
        coming.setItemMeta(headMeta);

        inventory.setItem(41, coming);

        this.puPlugin = puPlugin;
    }

    @Override
    public void onOpen(Player player) { }

    @Override
    public void onSlotClicked(Player player, int slotNum, ItemStack itemClicked) {
        switch(slotNum) {
            case 14 -> {
                if (EconomyManager.get().getBalance(player) < 20) {
                    ParallelChat.sendParallelMessageTo(player, Component.text("You don't have enough riftcoins!", NamedTextColor.RED));
                }
                else if (player.getInventory().firstEmpty() == -1) {
                    ParallelChat.sendParallelMessageTo(player, Component.text("You don't have enough inventory space!", NamedTextColor.RED));
                }
                else {
                    EconomyManager.get().removeRiftcoins(player, 20);
                    puPlugin.getServer().dispatchCommand(puPlugin.getServer().getConsoleSender(), "pu giveremover " + player.getName());
                    ParallelChat.sendParallelMessageTo(player, Component.text("You purchased a", NamedTextColor.AQUA)
                            .append(Component.text(" Charm Remover!", NamedTextColor.WHITE)));
                }
            }
            case 30 -> NPCShops.get().getMaggieShop().openCharmShopFor(player, ShopCategory.ITEM_NAME, "Item Name");
            case 31 -> NPCShops.get().getMaggieShop().openCharmShopFor(player, ShopCategory.KILL_MESSAGE, "Kill Message");
            case 32 -> NPCShops.get().getMaggieShop().openCharmShopFor(player, ShopCategory.PARTICLE_TRAIL, "Particle Trail");
            case 39 -> NPCShops.get().getMaggieShop().openCharmShopFor(player, ShopCategory.ARROW_TRAIL, "Arrow Trail");
            case 40 -> NPCShops.get().getMaggieShop().openCharmShopFor(player, ShopCategory.SHAPED_PARTICLE, "Shaped Particle");
        }
    }
}
