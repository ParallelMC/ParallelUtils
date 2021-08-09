package parallelmc.parallelutils.modules.parallelitems;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
        } catch (NullPointerException e) {
            Parallelutils.log(Level.WARNING,"NullPointerException registering enhanced_fertilizer. " +
                    "Item will not work!");
            e.printStackTrace();
        }

        itemRegistry.put("enhanced_fertilizer", aoeBonemeal);

        ItemStack baguette = new ItemStack(Material.BREAD);
        try {
            ItemMeta breada = baguette.getItemMeta();
            TextComponent name = Component.text("Baguette", NamedTextColor.WHITE)
                    .decoration(TextDecoration.ITALIC, false);
            breada.displayName(name);
            breada.setCustomModelData(1000000);

            breada.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 2);

            baguette.setItemMeta(breada);
        } catch (NullPointerException e) {
            Parallelutils.log(Level.WARNING,"NullPointerException registering baguette. " +
                    "Item will not work!");
            e.printStackTrace();
        }

        itemRegistry.put("baguette", baguette);
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
