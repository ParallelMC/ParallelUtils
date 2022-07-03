package parallelmc.parallelutils.modules.parallelitems;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelitems.pocketteleporter.PlayerPositionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * A module that adds custom items to Parallel
 */
public class ParallelItems implements ParallelModule {

    private final HashMap<String, ItemStack> itemRegistry = new HashMap<>();
    private final HashMap<Integer, ItemStack> itemRegistryId = new HashMap<>();

    public static PlayerPositionManager posManager;

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

        if (plugin == null) {
            ParallelUtils.log(Level.SEVERE, "Unable to enable ParallelItems. Plugin " + Constants.PLUGIN_NAME
                    + " does not exist!");
            return;
        }

        ParallelUtils puPlugin = (ParallelUtils) plugin;

        if (!puPlugin.registerModule(this)) {
            ParallelUtils.log(Level.SEVERE, "Unable to register module ParallelItems! " +
                    "Module may already be registered. Quitting...");
            return;
        }

        registerItems();
        ParallelItemsEventRegistrar.registerEvents();
        puPlugin.addCommand("give", new ParallelItemsGiveCommand());

        posManager = new PlayerPositionManager(puPlugin);
        posManager.init();
    }

    /**
     * Method to register ItemStacks for each type of ParallelItem
     */
    private void registerItems() {
        PluginManager manager = Bukkit.getPluginManager();
        JavaPlugin plugin = (JavaPlugin) manager.getPlugin(Constants.PLUGIN_NAME);
        if (plugin == null) {
            ParallelUtils.log(Level.SEVERE, "PLUGIN NOT FOUND. THIS IS A PROBLEM");
            return;
        }

        // A special tag to keep track of ParallelItems. Each type of item has it's own number.
        NamespacedKey key = new NamespacedKey(plugin, "ParallelItem");

        ItemStack aoeBonemeal = new ItemStack(Material.BONE_MEAL);
        try {
            ItemMeta boneMeta = aoeBonemeal.getItemMeta();
            TextComponent name = Component.text("Enhanced Fertilizer", NamedTextColor.DARK_GREEN)
                    .decoration(TextDecoration.ITALIC, false);
            boneMeta.displayName(name);
            ArrayList<Component> lore = new ArrayList<>();
            lore.add(Component.text("Fertilizes crops, saplings,", NamedTextColor.DARK_PURPLE));
            lore.add(Component.text("or mushrooms in a 5x5 area.", NamedTextColor.DARK_PURPLE));
            boneMeta.lore(lore);
            boneMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            boneMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            boneMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);

            aoeBonemeal.setItemMeta(boneMeta);

            itemRegistry.put("enhanced_fertilizer", aoeBonemeal);
            itemRegistryId.put(1, aoeBonemeal);
        } catch (NullPointerException e) {
            ParallelUtils.log(Level.WARNING,"NullPointerException registering enhanced_fertilizer. " +
                    "Item will not work!");
            e.printStackTrace();
        }

        ItemStack baguette = new ItemStack(Material.BREAD);
        try {
            ItemMeta breada = baguette.getItemMeta();
            TextComponent name = Component.text("Baguette", NamedTextColor.WHITE)
                    .decoration(TextDecoration.ITALIC, false);
            breada.displayName(name);
            breada.setCustomModelData(1000000);

            breada.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 2);

            baguette.setItemMeta(breada);

            itemRegistry.put("baguette", baguette);
            itemRegistryId.put(2, baguette);
        } catch (NullPointerException e) {
            ParallelUtils.log(Level.WARNING,"NullPointerException registering baguette. " +
                    "Item will not work!");
            e.printStackTrace();
        }

        ItemStack spaceHelmetRed = new ItemStack(Material.PAPER);
        try {
            net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(spaceHelmetRed);

            CompoundTag compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new CompoundTag();
            ListTag modifiers = new ListTag();

            CompoundTag armor = new CompoundTag();
            armor.put("AttributeName", StringTag.valueOf("generic.armor"));
            armor.put("Name", StringTag.valueOf("generic.armor"));
            armor.put("Amount", IntTag.valueOf(2));
            armor.put("Operation", IntTag.valueOf(0));
            armor.put("UUIDLeast", IntTag.valueOf(894654));
            armor.put("UUIDMost", IntTag.valueOf(2872));
            armor.put("Slot", StringTag.valueOf("head"));
            modifiers.add(armor);

            CompoundTag toughness = new CompoundTag();
            toughness.put("AttributeName", StringTag.valueOf("generic.armor_toughness"));
            toughness.put("Name", StringTag.valueOf("generic.armor_toughness"));
            toughness.put("Amount", IntTag.valueOf(2));
            toughness.put("Operation", IntTag.valueOf(0));
            toughness.put("UUIDLeast", IntTag.valueOf(894654));
            toughness.put("UUIDMost", IntTag.valueOf(2872));
            toughness.put("Slot", StringTag.valueOf("head"));
            modifiers.add(toughness);

            compound.put("AttributeModifiers", modifiers);
            nmsStack.setTag(compound);

            spaceHelmetRed = CraftItemStack.asBukkitCopy(nmsStack);

            ItemMeta helmetMeta = spaceHelmetRed.getItemMeta();
            TextComponent name = Component.text("Red Astronaut Hat", NamedTextColor.DARK_RED)
                    .decoration(TextDecoration.BOLD, true)
                    .decoration(TextDecoration.ITALIC, false);

            ArrayList<Component> loreList = new ArrayList<>();
            TextComponent lore = Component.text("The vast void of space is harsh without this.");
            loreList.add(lore);

            helmetMeta.displayName(name);
            helmetMeta.lore(loreList);
            helmetMeta.setCustomModelData(1500000);

            helmetMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 3);

            spaceHelmetRed.setItemMeta(helmetMeta);

            itemRegistry.put("space_helmet_red", spaceHelmetRed);
            itemRegistryId.put(3, spaceHelmetRed);
        } catch (NullPointerException e) {
            ParallelUtils.log(Level.WARNING, "NullPointerException registering space helmet. " +
                    "Item will not work!");
        }

        ItemStack candy = new ItemStack(Material.COOKIE);
        try {
            ItemMeta candyMeta = candy.getItemMeta();
            TextComponent name = Component.text("Spooky Candy", NamedTextColor.DARK_PURPLE)
                    .decoration(TextDecoration.ITALIC, false);
            candyMeta.displayName(name);
            candyMeta.setCustomModelData(1000000);

            ArrayList<Component> lore = new ArrayList<>();
            lore.add(Component.text("Its flavor is unique, and", NamedTextColor.GOLD));
            lore.add(Component.text("always changing. Quite spooky!", NamedTextColor.GOLD));
            candyMeta.lore(lore);

            candyMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 4);

            candy.setItemMeta(candyMeta);

            itemRegistry.put("candy", candy);
            itemRegistryId.put(4, candy);
        } catch (NullPointerException e) {
            ParallelUtils.log(Level.WARNING,"NullPointerException registering candy. " +
                    "Item will not work!");
            e.printStackTrace();
        }

        ItemStack earlySupporterGlasses = new ItemStack(Material.PAPER);
        try {
            // Create a CustomHat NBT tag that will be applied to the item to prevent it from being lost upon death
            NamespacedKey hatKey = new NamespacedKey(plugin, "CustomHat");

            net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(earlySupporterGlasses);

            CompoundTag compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new CompoundTag();
            ListTag modifiers = new ListTag();

            CompoundTag armor = new CompoundTag();
            armor.put("AttributeName", StringTag.valueOf("generic.armor"));
            armor.put("Name", StringTag.valueOf("generic.armor"));
            armor.put("Amount", IntTag.valueOf(2));
            armor.put("Operation", IntTag.valueOf(0));
            armor.put("UUIDLeast", IntTag.valueOf(195734));
            armor.put("UUIDMost", IntTag.valueOf(9237));
            armor.put("Slot", StringTag.valueOf("head"));
            modifiers.add(armor);

//            CompoundTag toughness = new CompoundTag();
//            toughness.set("AttributeName", StringTag.put("generic.armor_toughness"));
//            toughness.set("Name", StringTag.put("generic.armor_toughness"));
//            toughness.set("Amount", IntTag.put(2));
//            toughness.set("Operation", IntTag.put(0));
//            toughness.set("UUIDLeast", IntTag.put(894654));
//            toughness.set("UUIDMost", IntTag.put(2872));
//            toughness.set("Slot", StringTag.put("head"));
//            modifiers.add(toughness);

            compound.put("AttributeModifiers", modifiers);
            nmsStack.setTag(compound);

            earlySupporterGlasses = CraftItemStack.asBukkitCopy(nmsStack);

            ItemMeta glassesMeta = earlySupporterGlasses.getItemMeta();
            TextComponent name = Component.text("Early Supporter Glasses", NamedTextColor.DARK_GREEN)
                    .decoration(TextDecoration.BOLD, true)
                    .decoration(TextDecoration.ITALIC, false);

            ArrayList<Component> loreList = new ArrayList<>();
            TextComponent lore = Component.text("Thanks for supporting the server!");
            loreList.add(lore);

            glassesMeta.displayName(name);
            glassesMeta.lore(loreList);
            glassesMeta.setCustomModelData(9000030);

            glassesMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 5);
            glassesMeta.getPersistentDataContainer().set(hatKey, PersistentDataType.INTEGER, 1);

            earlySupporterGlasses.setItemMeta(glassesMeta);

            itemRegistry.put("early_supporter_glasses", earlySupporterGlasses);
            itemRegistryId.put(5, earlySupporterGlasses);
        } catch (NullPointerException e) {
            ParallelUtils.log(Level.WARNING, "NullPointerException registering early supporter glasses. " +
                    "Item will not work!");
        }

        ItemStack teleporter = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        try {
            ItemMeta teleMeta = teleporter.getItemMeta();
            TextComponent name = Component.text("Pocket Teleporter", NamedTextColor.LIGHT_PURPLE)
                    .decoration(TextDecoration.ITALIC, false);
            teleMeta.displayName(name);
            teleMeta.setCustomModelData(1000001);

            if (teleMeta instanceof LeatherArmorMeta armorMeta) {
                armorMeta.setColor(Color.WHITE);
                armorMeta.addItemFlags(ItemFlag.HIDE_DYE);
            }

            ArrayList<Component> lore = new ArrayList<>();
            lore.add(Component.text("Right-click ", NamedTextColor.YELLOW)
                    .append(Component.text("to teleport between spawn", NamedTextColor.GRAY)).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.text("and your last location.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.text("Shift + right-click ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                    .append(Component.text("to reset your last location.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));
            teleMeta.lore(lore);

            NamespacedKey modifyKey = new NamespacedKey(plugin, "NoModify");

            teleMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 6);
            teleMeta.getPersistentDataContainer().set(modifyKey, PersistentDataType.INTEGER, 1);

            teleporter.setItemMeta(teleMeta);

            itemRegistry.put("pocket_teleporter", teleporter);
            itemRegistryId.put(6, teleporter);
        } catch (NullPointerException e) {
            ParallelUtils.log(Level.WARNING,"NullPointerException registering pocket_teleporter. " +
                    "Item will not work!");
            e.printStackTrace();
        }

        ItemStack totem = new ItemStack(Material.GOLDEN_HORSE_ARMOR);
        try {
            ItemMeta totemMeta = totem.getItemMeta();
            TextComponent name = Component.text("Totem of the Void", NamedTextColor.LIGHT_PURPLE)
                    .decoration(TextDecoration.ITALIC, false);
            totemMeta.displayName(name);
            totemMeta.setCustomModelData(1000000);

            ArrayList<Component> lore = new ArrayList<>();
            lore.add(Component.text("Protects you from dying in the void", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            totemMeta.lore(lore);

            totemMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 7);

            totem.setItemMeta(totemMeta);

            itemRegistry.put("totem_of_the_void", totem);
            itemRegistryId.put(7, totem);
        } catch (NullPointerException e) {
            ParallelUtils.log(Level.WARNING,"NullPointerException registering totem_of_the_void. " +
                    "Item will not work!");
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        posManager.unload();
    }

    @Override
    public @NotNull String getName() {
        return "ParallelItems";
    }

    /**
     * Getter for the itemRegistry
     * @param key key to search for
     * @return value
     */
    public ItemStack getItem(String key){
        return itemRegistry.get(key);
    }
}
