package parallelmc.parallelutils.modules.bitsandbobs.minimodules;

import net.minecraft.nbt.CompoundTag;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
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

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Creates an empty list for all items to be prevented from dropping
        ArrayList<ItemStack> preventedDrops = new ArrayList<>();

        for (ItemStack item : event.getDrops()) {
            // TODO: This probably doesn't need to be dependent on item type. Just checking the tag is a bit more futureproof
            // TODO: The leather horse armor add is a band-aid because I'm lazy, will fix later
            if (item.getType() == Material.PAPER || item.getType() == Material.LEATHER_HORSE_ARMOR) {
                // TODO: Try to change this code to use item.getItemMeta().getPersistentDataContainer()
                // TODO: Make this 2-part check not jank - hopefully transition entirely to persistentdatacontainer
                NamespacedKey hatKey = new NamespacedKey(plugin, "CustomHat");
                PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
                if (container.has(hatKey, PersistentDataType.INTEGER)) {
                    preventedDrops.add(item);
                    continue;
                }

                net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item); // does this even work lol
                // Grabs the NMS items compound and checks if it's null
                CompoundTag compound = (nmsItem.hasTag()) ? nmsItem.getTag() : new CompoundTag();

                if (compound == null) continue;

                if (compound.contains("CustomHat")) {
                    preventedDrops.add(item);
                }
            } else if (item.getType() == Material.PLAYER_HEAD) {
                net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item); // does this even work lol
                // Grabs the NMS items compound and checks if it's null
                CompoundTag compound = (nmsItem.hasTag()) ? nmsItem.getTag() : new CompoundTag();

                if (compound == null) continue;

                if (compound.contains("CustomTrophy")) {
                    preventedDrops.add(item);
                }
            }
        }
        // If the list of special drops has items in it, add a new player entry to the logger hashmap and remove the
        // dropped items
        if (!preventedDrops.isEmpty()) {
            // Gets the UUID of the player
            UUID uuid = event.getEntity().getUniqueId();
            specialItemsLogger.put(uuid, preventedDrops);

            // Removes each special item from the original list of dropped items
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
            NamespacedKey hatKey = new NamespacedKey(plugin, "CustomHat");
            NamespacedKey modifyKey = new NamespacedKey(plugin, "NoModify");
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
            if (container.has(hatKey, PersistentDataType.INTEGER) || container.has(modifyKey, PersistentDataType.INTEGER)) {
                ingredients.setResult(null); // Sets the crafting output to null if a CustomHat tag is found
                break;
            }
            // Now we have to use NMS if the item doesn't have a PersistentDataContainer
            net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item); // does this even work lol
            // Grabs the NMS items compound and checks if it's null
            CompoundTag compound = (nmsItem.hasTag()) ? nmsItem.getTag() : new CompoundTag();
            if (compound == null) continue;

            if (compound.contains("CustomHat")) {
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
                            NamespacedKey hatKey = new NamespacedKey(plugin, "CustomHat");
                            NamespacedKey modifyKey = new NamespacedKey(plugin, "NoModify");
                            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                            if (container.has(hatKey, PersistentDataType.INTEGER) || container.has(modifyKey, PersistentDataType.INTEGER)) {
                                event.setCancelled(true);
                                return;
                            }

                            // Now we have to use NMS if the item doesn't have a PersistentDataContainer
                            net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item); // does this even work lol
                            // Grabs the NMS items compound and checks if it's null
                            CompoundTag compound = (nmsItem.hasTag()) ? nmsItem.getTag() : new CompoundTag();
                            if (compound == null) return;

                            if (compound.contains("CustomHat")) {
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }
}
