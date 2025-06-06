package parallelmc.parallelutils.modules.bitsandbobs.minimodules;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import parallelmc.parallelutils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


public class SpecialItems implements Listener {

    PluginManager manager = Bukkit.getPluginManager();
    Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

    private final HashMap<UUID, ArrayList<ItemStack>> specialItemsLogger = new HashMap<>();

    private final String[] SPECIAL_ITEMS = {"CustomHat", "CustomTrophy"};


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Creates an empty list for all items to be prevented from dropping
        ArrayList<ItemStack> preventedDrops = new ArrayList<>();

        for (ItemStack item : event.getDrops()) {
            // TODO: The NMS check down below won't be needed anymore if we start making every hat and trophy through PU
            NamespacedKey hatKeyOld = new NamespacedKey(plugin, "CustomHat");
            NamespacedKey hatKey = new NamespacedKey(plugin, "ParallelHat");
            PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
            if (container.has(hatKeyOld, PersistentDataType.INTEGER) || container.has(hatKey, PersistentDataType.STRING)) {
                preventedDrops.add(item);
                continue;
            }

            net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item); // does this even work lol
            // Grabs the custom data component (returns an empty component if it's null)
            CustomData customData = nmsItem.getComponents().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);

            if (customData.contains("CustomHat") || customData.contains("CustomTrophy")) {
                preventedDrops.add(item);
            }

        }

        // If the list of special drops has items in it, add a new player entry to the logger hashmap and remove the
        // items from dropping upon death
        if (!preventedDrops.isEmpty()) {
            // Gets the UUID of the player
            UUID uuid = event.getEntity().getUniqueId();
            specialItemsLogger.put(uuid, preventedDrops);

            // Removes each special item from dropping upon death
            for (ItemStack item : preventedDrops) {
                event.getDrops().remove(item);
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        // If the special items hashmap contains the player, then give them their special items back and remove them
        // from the hashmap
        if (specialItemsLogger.containsKey(event.getPlayer().getUniqueId())) {
            ArrayList<ItemStack> items = specialItemsLogger.get(event.getPlayer().getUniqueId());
            for (ItemStack item : items) {
                event.getPlayer().getInventory().addItem(item);
            }
            // Removes the player from the hashmap
            specialItemsLogger.remove(event.getPlayer().getUniqueId());
        }
    }

    // Prevents any item with a CustomHat or NoEdit tag from being used in a crafting recipe
    @EventHandler
    public void onPlayerCraft(PrepareItemCraftEvent event) {
        CraftingInventory ingredients = event.getInventory();
        for (ItemStack item: ingredients.getStorageContents()) {
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta == null) {  // itemMeta could be null, so we have to check this
                continue;
            }
            NamespacedKey hatKeyOld = new NamespacedKey(plugin, "CustomHat");
            NamespacedKey hatKey = new NamespacedKey(plugin, "ParallelHat");
            NamespacedKey dyeableKey = new NamespacedKey(plugin, "Dyeable");
            NamespacedKey modifyKey = new NamespacedKey(plugin, "NoModify");
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
            // skip this item if it has the dyeableKey
            if (container.has(dyeableKey, PersistentDataType.INTEGER)) {
                continue;
            }
            // otherwise, if it's a hat or has the NoModify key, cancel the recipe
            if (container.has(hatKeyOld, PersistentDataType.INTEGER)
                    || container.has(hatKey, PersistentDataType.STRING)
                    || container.has(modifyKey, PersistentDataType.INTEGER)) {
                ingredients.setResult(null);
                break;
            }
            // Now we have to use NMS if the item doesn't have a PersistentDataContainer
            net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item); // does this even work lol
            // Grabs the custom data component (returns an empty component if it's null)
            CustomData customData = nmsItem.getComponents().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);

            if (customData.contains("CustomHat")) {
                ingredients.setResult(null);
                break;
            }

        }
    }

    // Prevents players from dyeing any horse armor that has a CustomHat or NoEdit tag - probably could make a helper class in the future
    @EventHandler
    public void onArmorCauldronDye(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getType() == Material.WATER_CAULDRON) {
                ItemStack item = event.getItem();
                if (item != null) {
                    if (item.getType() == Material.LEATHER_HORSE_ARMOR) {
                        ItemMeta itemMeta = item.getItemMeta();
                        if (itemMeta != null) {  // itemMeta could be null, so we have to check this
                            NamespacedKey hatKeyOld = new NamespacedKey(plugin, "CustomHat");
                            NamespacedKey hatKey = new NamespacedKey(plugin, "ParallelHat");
                            NamespacedKey dyeableKey = new NamespacedKey(plugin, "Dyeable");
                            NamespacedKey modifyKey = new NamespacedKey(plugin, "NoModify");
                            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                            // skip this item if it has the dyeableKey
                            if (container.has(dyeableKey, PersistentDataType.INTEGER)) {
                                return;
                            }
                            // otherwise, if it's a hat or has the NoModify key, cancel the recipe
                            if (container.has(hatKeyOld, PersistentDataType.INTEGER)
                                    || container.has(hatKey, PersistentDataType.STRING)
                                    || container.has(modifyKey, PersistentDataType.INTEGER)) {
                                event.setCancelled(true);
                                return;
                            }

                            // Now we have to use NMS if the item doesn't have a PersistentDataContainer
                            net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item); // does this even work lol
                            // Grabs the custom data component (returns an empty component if it's null)
                            CustomData customData = nmsItem.getComponents().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);

                            if (customData.contains("CustomHat")) {
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }
}
