package parallelmc.parallelutils.modules.parallelitems;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
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
    private JavaPlugin javaPlugin;
    private NamespacedKey customKey;

    public PlayerInteractListener(){
        PluginManager manager = Bukkit.getPluginManager();
        JavaPlugin plugin = (JavaPlugin) manager.getPlugin(Constants.PLUGIN_NAME);
        if (plugin == null) {
            Parallelutils.log(Level.SEVERE, "PLUGIN NOT FOUND. THIS IS A PROBLEM");
            return;
        }

        javaPlugin = plugin;
        customKey = new NamespacedKey(javaPlugin, "ParallelItem");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.hasItem()) {
            ItemStack item = event.getItem();

            if (item == null) {
                Parallelutils.log(Level.WARNING, "item null checking ParallelItem. This is bad!");
                return;
            }

            ItemMeta meta = item.getItemMeta();

            Integer val = meta.getPersistentDataContainer().get(customKey, PersistentDataType.INTEGER);
            if (val == null) {
                return;
            }

            switch (val) {
                // TODO: Abstract this out so that it's just like, ParallelItems.get(val).executeAction();
                case 1 -> { // this is enhanced_fertilizer! probably.
                    if(event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                        return;
                    }

                    if(item.getType() != Material.BONE_MEAL) {
                        Parallelutils.log(Level.WARNING, "Items with tag 'ParallelItems:1' are " +
                                "enhanced_fertilizer, but this is not the correct material. Something isn't right.");
                        return;
                    }

                    event.setCancelled(true);
                    Block origin = event.getClickedBlock();

                    if (origin == null) return;

                    BlockData blockData = origin.getBlockData();

                    // check if original block is ageable or sapling
                    if(!(blockData instanceof Ageable || blockData instanceof Sapling)) {
                        Parallelutils.log(Level.INFO, "Not ageable or sapling!");
                        Parallelutils.log(Level.INFO, origin.toString());
                        return;
                    }

                    int halfFR = (int) (FERTILIZER_RANGE / 2.0);
                    Location searchStart = origin.getLocation().add(-halfFR, 0 ,-halfFR);
                    World world = searchStart.getWorld();
                    boolean removed = false;
                    for(int i = 0; i < FERTILIZER_RANGE; i++){
                        for(int k = 0; k < FERTILIZER_RANGE; k++){
                            // apply bone meal. if this is the first time we've done it successfully, remove 1 from player
                            Block b = world.getBlockAt(searchStart.clone().add(i, 0, k));
                            BlockData bd = b.getBlockData();
                            if(bd instanceof Ageable || bd instanceof Sapling) {
                                boolean boneMealStatus = b.applyBoneMeal(BlockFace.UP);

                                Parallelutils.log(Level.INFO, "" + !removed);
                                Parallelutils.log(Level.INFO, "" + boneMealStatus);

                                if (!removed && boneMealStatus && !event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                                    Parallelutils.log(Level.INFO, "Removing item");

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

    @EventHandler(priority = EventPriority.HIGH)
    public void onHumanEat(FoodLevelChangeEvent event){
        ItemStack item = event.getItem();
        if(item != null){
            ItemMeta meta = item.getItemMeta();

            Integer val = meta.getPersistentDataContainer().get(customKey, PersistentDataType.INTEGER);
            if (val == null) {
                return;
            }

            switch(val){
                case 2 -> { //presumably, a baguette
                    if(item.getType() != Material.BREAD) {
                        Parallelutils.log(Level.WARNING, "Items with tag 'ParallelItems:2' are " +
                                "baguette, but this is not the correct material. Something isn't right.");
                        return;
                    }


                    if(event.getFoodLevel() >= 15 && event.getFoodLevel() < 20){
                        event.setFoodLevel(20);
                    }
                    else{
                        event.setFoodLevel(event.getFoodLevel() + 5);
                    }

                    HumanEntity entity = event.getEntity();
                    if(entity.getSaturation() >= event.getFoodLevel()){
                        return;
                    }
                    if(entity.getSaturation() >= event.getFoodLevel()-6.0){
                        entity.setSaturation(event.getFoodLevel());
                    }
                    else{
                        entity.setSaturation((float)(entity.getSaturation()+6.0));
                    }
                }
            }
        }
    }

    // Looting shears
    @EventHandler(priority = EventPriority.HIGH)
    public void playerShearEvent(PlayerShearEntityEvent event) {
        if (event.getEntity() instanceof Sheep) {
            // check for looting level
            Player player = event.getPlayer();
            if (player.getItemInHand().getType() == Material.SHEARS) {
                if (player.getItemInHand().getItemMeta().hasEnchant(Enchantment.LOOTING)) {
                    int level = player.getItemInHand().getItemMeta().getEnchantLevel(Enchantment.LOOTING);
                    event.setCancelled(true);
                    //Generate random number of wool to drop based on looting level
                    Random random = new Random();
                    int min = 1;
                    int max = 3 + level;
                    int numWool = random.nextInt((max-min)+1) + min;

                    // Make sheep drop their respective color of wool
                    Sheep sheep  = (Sheep) event.getEntity();
                    Dyecolor woolColor = sheep.getColor();
                    Material wool = Material.getMaterial(woolColor.toString() + "_WOOL");
                    if (wool != null) {
                        event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemStack(wool, numWool));
                    }
                }
            }
        }
    }
}
