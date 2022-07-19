package parallelmc.parallelutils.modules.parallelitems;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.parallelitems.pocketteleporter.PlayerPositionManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * A module that adds custom items to Parallel
 */
public class ParallelItems implements ParallelModule {

    private final HashMap<String, ItemStack> itemRegistry = new HashMap<>();
    private final HashMap<Integer, ItemStack> itemRegistryId = new HashMap<>();
    private final HashMap<String, ParallelFish> fishRegistry = new HashMap<>();

    private final HashMap<Integer, ParallelFish> fishRegistryId = new HashMap<>();

    public static PlayerPositionManager posManager;

    private Parallelutils puPlugin;

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
        puPlugin.addCommand("givefish", new ParallelItemsGiveFishCommand());

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
            Parallelutils.log(Level.SEVERE, "PLUGIN NOT FOUND. THIS IS A PROBLEM");
            return;
        }

        this.puPlugin = (Parallelutils)plugin;

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
            Parallelutils.log(Level.WARNING, "NullPointerException registering early supporter glasses. " +
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
            Parallelutils.log(Level.WARNING,"NullPointerException registering pocket_teleporter. " +
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
            Parallelutils.log(Level.WARNING,"NullPointerException registering totem_of_the_void. " +
                    "Item will not work!");
            e.printStackTrace();
        }

        loadFish();
    }

    @Override
    public void onDisable() {
        posManager.unload();
    }

    /**
     * Getter for the itemRegistry
     * @param key key to search for
     * @return value
     */
    public ItemStack getItem(String key){
        return itemRegistry.get(key);
    }

    public ParallelFish getFish(String key) { return fishRegistry.get(key); }
    public ParallelFish getFishById(int id) { return fishRegistryId.get(id); }

    public HashMap<String, ParallelFish> getAllFish() { return fishRegistry; }

    public void loadFish() {
        int id = 1;
        NamespacedKey ikey = new NamespacedKey(puPlugin, "ParallelFish");
        File fishFile = new File(puPlugin.getDataFolder(), "fish.yml");
        FileConfiguration fishConfig = new YamlConfiguration();
        try {
            if (fishFile.createNewFile()) {
                Parallelutils.log(Level.WARNING, "fish.yml does not exist. Creating...");
            }
            fishConfig.load(fishFile);
        } catch (IOException e) {
            Parallelutils.log(Level.SEVERE, "Failed to create or read fish.yml\n" + e);
            return;
        } catch (Exception e) {
            Parallelutils.log(Level.SEVERE, "Failed to load fish.yml\n" + e);
            return;
        }
        for (String key : fishConfig.getKeys(false)) {
            try {
                ItemStack fish;
                int hunger = 0, saturation = 0;
                boolean consumable = fishConfig.getBoolean(key + ".consumable");
                if (consumable) {
                    fish = new ItemStack(Material.COD);
                    hunger = fishConfig.getInt(key + ".hunger");
                    saturation = fishConfig.getInt(key + ".saturation");
                    if (hunger == 0 || saturation == 0) {
                        Parallelutils.log(Level.WARNING, "Failed to load fish " + key + ", could not find hunger or saturation");
                        continue;
                    }
                }
                else
                    fish = new ItemStack(Material.PAPER);
                String fishName = fishConfig.getString(key + ".name");
                if (fishName == null) {
                    Parallelutils.log(Level.WARNING, "Failed to load fish " + key + ", could not find name");
                    continue;
                }
                // it's a bad idea to assume this but most of the cooked fish should have this, so it works for now
                // saves having to loop twice or add extra config stuff
                if (fishName.toLowerCase().contains("cooked")) {
                    fish = new ItemStack(Material.COOKED_COD);
                }
                ItemMeta fishMeta = fish.getItemMeta();
                fishMeta.displayName(Component.text(fishName, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
                int modelData = fishConfig.getInt(key + ".model_data");
                if (modelData == 0) {
                    Parallelutils.log(Level.WARNING, "Failed to load fish " + key + ", could not find model data");
                    continue;
                }
                List<String> desc = fishConfig.getStringList(key + ".description");
                if (desc.size() == 0) {
                    Parallelutils.log(Level.WARNING, "Failed to load fish " + key + ", could not find description");
                    continue;
                }
                List<Component> lore = new ArrayList<>();
                for (String s : desc) {
                    lore.add(MiniMessage.miniMessage().deserialize(s).decoration(TextDecoration.ITALIC, false));
                }
                fishMeta.lore(lore);
                fishMeta.setCustomModelData(modelData);
                fishMeta.getPersistentDataContainer().set(ikey, PersistentDataType.INTEGER, id);
                fish.setItemMeta(fishMeta);
                String cooked_key = fishConfig.getString(key + ".cooked_name");
                ParallelFish pFish = new ParallelFish(id, key, hunger, saturation, cooked_key, fish);
                fishRegistry.put(key, pFish);
                fishRegistryId.put(id, pFish);
                id++;

            } catch (NullPointerException e) {
                Parallelutils.log(Level.WARNING,"NullPointerException registering " + key +
                        ". Item will not work!");
                e.printStackTrace();
            }
        }
        Parallelutils.log(Level.WARNING, "[ParallelFish]: Loaded " + fishRegistry.size() + " fish");
    }
}
