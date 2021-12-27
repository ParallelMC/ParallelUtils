package parallelmc.parallelutils.modules.parallelitems;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.Parallelutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * A module that adds custom items to Parallel
 */
public class ParallelItems implements ParallelModule {

    private final HashMap<String, ItemStack> itemRegistry = new HashMap<>();
    private final HashMap<Integer, ItemStack> itemRegistryId = new HashMap<>();

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

        if (plugin == null) {
            Parallelutils.log(Level.SEVERE, "Unable to enable ParallelItems. Plugin " + Constants.PLUGIN_NAME
                    + " does not exist!");
            return;
        }

        Parallelutils puPlugin = (Parallelutils) plugin;

        if (!puPlugin.registerModule("ParallelItems", this)) {
            Parallelutils.log(Level.SEVERE, "Unable to register module ParallelItems! " +
                    "Module may already be registered. Quitting...");
            return;
        }

        registerItems();
        ParallelItemsEventRegistrar.registerEvents();
        puPlugin.addCommand("give", new ParallelItemsGiveCommand());
    }

    /**
     * Method to register ItemStacks for each type of ParallelItem
     */
    private void registerItems() {
        PluginManager manager = Bukkit.getPluginManager();
        JavaPlugin plugin = (JavaPlugin) manager.getPlugin(Constants.PLUGIN_NAME);
        if (plugin == null) {
            Parallelutils.log(Level.SEVERE, "PLUGIN NOT FOUND. THIS IS A PROBLEM");
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
            Parallelutils.log(Level.WARNING,"NullPointerException registering enhanced_fertilizer. " +
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
            Parallelutils.log(Level.WARNING,"NullPointerException registering baguette. " +
                    "Item will not work!");
            e.printStackTrace();
        }

        ItemStack spaceHelmetRed = new ItemStack(Material.PAPER);
        try {
            net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(spaceHelmetRed);

            NBTTagCompound compound = (nmsStack.r()) ? nmsStack.s() : new NBTTagCompound();
            NBTTagList modifiers = new NBTTagList();

            NBTTagCompound armor = new NBTTagCompound();
            armor.a("AttributeName", NBTTagString.a("generic.armor"));
            armor.a("Name", NBTTagString.a("generic.armor"));
            armor.a("Amount", NBTTagInt.a(2));
            armor.a("Operation", NBTTagInt.a(0));
            armor.a("UUIDLeast", NBTTagInt.a(894654));
            armor.a("UUIDMost", NBTTagInt.a(2872));
            armor.a("Slot", NBTTagString.a("head"));
            modifiers.add(armor);

            NBTTagCompound toughness = new NBTTagCompound();
            toughness.a("AttributeName", NBTTagString.a("generic.armor_toughness"));
            toughness.a("Name", NBTTagString.a("generic.armor_toughness"));
            toughness.a("Amount", NBTTagInt.a(2));
            toughness.a("Operation", NBTTagInt.a(0));
            toughness.a("UUIDLeast", NBTTagInt.a(894654));
            toughness.a("UUIDMost", NBTTagInt.a(2872));
            toughness.a("Slot", NBTTagString.a("head"));
            modifiers.add(toughness);

            compound.a("AttributeModifiers", modifiers);
            nmsStack.c(compound);

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
            Parallelutils.log(Level.WARNING, "NullPointerException registering space helmet. " +
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
            Parallelutils.log(Level.WARNING,"NullPointerException registering candy. " +
                    "Item will not work!");
            e.printStackTrace();
        }

        ItemStack earlySupporterGlasses = new ItemStack(Material.PAPER);
        try {
            // Create a CustomHat NBT tag that will be applied to the item to prevent it from being lost upon death
            NamespacedKey hatKey = new NamespacedKey(plugin, "CustomHat");

            net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(earlySupporterGlasses);

            NBTTagCompound compound = (nmsStack.r()) ? nmsStack.s() : new NBTTagCompound();
            NBTTagList modifiers = new NBTTagList();

            NBTTagCompound armor = new NBTTagCompound();
            armor.a("AttributeName", NBTTagString.a("generic.armor"));
            armor.a("Name", NBTTagString.a("generic.armor"));
            armor.a("Amount", NBTTagInt.a(2));
            armor.a("Operation", NBTTagInt.a(0));
            armor.a("UUIDLeast", NBTTagInt.a(195734));
            armor.a("UUIDMost", NBTTagInt.a(9237));
            armor.a("Slot", NBTTagString.a("head"));
            modifiers.add(armor);

//            NBTTagCompound toughness = new NBTTagCompound();
//            toughness.set("AttributeName", NBTTagString.a("generic.armor_toughness"));
//            toughness.set("Name", NBTTagString.a("generic.armor_toughness"));
//            toughness.set("Amount", NBTTagInt.a(2));
//            toughness.set("Operation", NBTTagInt.a(0));
//            toughness.set("UUIDLeast", NBTTagInt.a(894654));
//            toughness.set("UUIDMost", NBTTagInt.a(2872));
//            toughness.set("Slot", NBTTagString.a("head"));
//            modifiers.add(toughness);

            compound.a("AttributeModifiers", modifiers);
            nmsStack.c(compound);

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
            Parallelutils.log(Level.WARNING, "NullPointerException registering early supporter glasses. " +
                    "Item will not work!");
        }

    }

    @Override
    public void onDisable() {

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
