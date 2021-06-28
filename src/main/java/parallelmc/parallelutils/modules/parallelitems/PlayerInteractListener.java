package parallelmc.parallelutils.modules.parallelitems;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.entity.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.Parallelutils;

import java.util.logging.Level;

/**
 * Listens for Player interaction events
 */
public class PlayerInteractListener implements Listener {

    private final int FERTILIZER_RANGE = 5;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.hasItem()) {
            ItemStack item = event.getItem();

            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                Parallelutils.log(Level.WARNING, "ItemMeta null checking ParallelItem. This is bad!");
                return;
            }

            PluginManager manager = Bukkit.getPluginManager();
            JavaPlugin plugin = (JavaPlugin) manager.getPlugin(Constants.PLUGIN_NAME);
            if (plugin == null) {
                Parallelutils.log(Level.SEVERE, "PLUGIN NOT FOUND. THIS IS A PROBLEM");
                return;
            }

            //check which fancy item this is
            NamespacedKey key = new NamespacedKey(plugin, "ParallelItem");
            Integer val = meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
            if (val == null) {
                return;
            }
            switch (val) {
                case 1 -> { //this is enhanced_fertilizer! probably.
                    if(event.getAction() != Action.RIGHT_CLICK_BLOCK){
                        return;
                    }
                    if(item.getType() != Material.BONE_MEAL){
                        Parallelutils.log(Level.WARNING, "Items with tag 'ParallelItems:1' are " +
                                "enhanced_fertilizer, but this is not the correct material. Something isn't right.");
                        return;
                    }
                    event.setCancelled(true);
                    Block origin = event.getClickedBlock();
                    //check if original block is ageable or sapling
                    if(!(origin instanceof Ageable || origin instanceof Sapling)){
                        return;
                    }

                    int halfFR = (int) (FERTILIZER_RANGE / 2.0);
                    Location searchStart = origin.getLocation().add(-halfFR, 0 ,-halfFR);
                    World world = searchStart.getWorld();
                    boolean removed = false;
                    for(int i = 0; i < FERTILIZER_RANGE; i++){
                        for(int k = 0; k < FERTILIZER_RANGE; k++){
                            //apply bone meal. if this is the first time we've done it successfully, remove 1 from player
                            Block b = world.getBlockAt(searchStart.add(i, 0, k));
                            if(b instanceof Ageable || b instanceof Sapling) {
                                if (!removed && b.applyBoneMeal(BlockFace.SELF)) {
                                    item.subtract();
                                    removed = true;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
