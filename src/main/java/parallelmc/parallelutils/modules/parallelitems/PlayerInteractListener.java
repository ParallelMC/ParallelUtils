package parallelmc.parallelutils.modules.parallelitems;

import net.kyori.adventure.key.Key;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.logging.Level;

/**
 * Listens for Player interaction events
 */
public class PlayerInteractListener implements Listener {

    private final int FERTILIZER_RANGE = 5;
    private final PotionEffect[] CANDY_EFFECTS = {new PotionEffect(PotionEffectType.BLINDNESS, 100, 0),
            new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 120, 0),
            new PotionEffect(PotionEffectType.GLOWING, 200, 0),
            new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0),
            new PotionEffect(PotionEffectType.INVISIBILITY, 200, 0),
            new PotionEffect(PotionEffectType.JUMP, 200, 0),
            new PotionEffect(PotionEffectType.LEVITATION, 100, 0),
            new PotionEffect(PotionEffectType.REGENERATION, 160, 0),
            new PotionEffect(PotionEffectType.SPEED, 200, 0),
            new PotionEffect(PotionEffectType.WATER_BREATHING, 120, 0)};
    private JavaPlugin javaPlugin;
    private NamespacedKey customKey;

    public final HashMap<Player, Location> lastSafePosition = new HashMap<>();
    private BukkitTask positionSaver;
    private final HashSet<Player> attemptingToSave = new HashSet<>();

    public PlayerInteractListener(){
        PluginManager manager = Bukkit.getPluginManager();
        JavaPlugin plugin = (JavaPlugin) manager.getPlugin(Constants.PLUGIN_NAME);
        if (plugin == null) {
            Parallelutils.log(Level.SEVERE, "PLUGIN NOT FOUND. THIS IS A PROBLEM");
            return;
        }

        javaPlugin = plugin;
        customKey = new NamespacedKey(javaPlugin, "ParallelItem");
        positionSaver = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : javaPlugin.getServer().getOnlinePlayers()) {
                    boolean itemFound = false;
                    ItemStack offhand = p.getInventory().getItemInOffHand();
                    if (offhand.getType() == Material.GOLDEN_HORSE_ARMOR) {
                        Integer val = offhand.getItemMeta().getPersistentDataContainer().get(customKey, PersistentDataType.INTEGER);
                        if (val != null) {
                            if (val == 7) {
                                itemFound = true;
                                Location current = p.getLocation();
                                if (ParallelItems.posManager.isPositionSafe(current.getBlock())) {
                                    lastSafePosition.put(p, current);
                                }
                            }
                        }
                    }
                    // if it isn't in their offhand check the rest of their inventory (includes mainhand)
                    if (!itemFound) {
                        for (ItemStack i : p.getInventory().all(Material.GOLDEN_HORSE_ARMOR).values()) {
                            Integer val = i.getItemMeta().getPersistentDataContainer().get(customKey, PersistentDataType.INTEGER);
                            if (val == null) continue;
                            // if they have at least one totem
                            if (val == 7) {
                                itemFound = true;
                                Location current = p.getLocation();
                                if (ParallelItems.posManager.isPositionSafe(current.getBlock())) {
                                    lastSafePosition.put(p, current);
                                }
                                // even if the player's current position is not safe still break out of the inner loop
                                break;
                            }
                        }
                        // if they still don't have a totem then remove their position
                        if (!itemFound) {
                            lastSafePosition.remove(p);
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(javaPlugin, 0, 10);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Feather falling on boots cancels player crop trampling
        if (event.getAction() == Action.PHYSICAL) {
            Block block = event.getClickedBlock();
            if (block != null && block.getType() == Material.FARMLAND) {
                ItemStack boots = event.getPlayer().getInventory().getBoots();
                if (boots != null) {
                    if (boots.getItemMeta().hasEnchant(Enchantment.PROTECTION_FALL)) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }

        if (event.hasItem()) {
            ItemStack item = event.getItem();

            if (item == null) {
                Parallelutils.log(Level.WARNING, "item null checking ParallelItem. This is bad!");
                return;
            }

            ItemMeta meta = item.getItemMeta();

            if (meta == null) {
                return;
            }

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
                case 6 -> {
                    if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                        return;
                    }

                    if(item.getType() != Material.LEATHER_HORSE_ARMOR) {
                        Parallelutils.log(Level.WARNING, "Items with tag 'ParallelItems:6' are " +
                                "pocket_teleporter, but this is not the correct material. Something isn't right.");
                        return;
                    }

                    event.setCancelled(true);
                    ParallelItems.posManager.attemptTeleport(event.getPlayer(), event.getItem());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractWithEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Horse) {
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();
            Integer val;
            if (meta == null) {
                item = player.getInventory().getItemInOffHand();
                meta = item.getItemMeta();
                if (meta == null)
                    return;
            }
            val = meta.getPersistentDataContainer().get(customKey, PersistentDataType.INTEGER);
            if (val == null)
                return;
            if (val == 6 || val == 7) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onHumanEat(FoodLevelChangeEvent event) {
        ItemStack item = event.getItem();
        if(item != null) {
            ItemMeta meta = item.getItemMeta();

            Integer val = meta.getPersistentDataContainer().get(customKey, PersistentDataType.INTEGER);
            if (val == null) {
                return;
            }

            switch(val) {
                case 2 -> { // presumably, a baguette
                    if(item.getType() != Material.BREAD) {
                        Parallelutils.log(Level.WARNING, "Items with tag 'ParallelItems:2' are " +
                                "baguette, but this is not the correct material. Something isn't right.");
                        return;
                    }


                    if(event.getFoodLevel() >= 15 && event.getFoodLevel() < 20) {
                        event.setFoodLevel(20);
                    } else {
                        event.setFoodLevel(event.getFoodLevel() + 5);
                    }

                    HumanEntity entity = event.getEntity();
                    if(entity.getSaturation() >= event.getFoodLevel()) {
                        return;
                    }
                    if(entity.getSaturation() >= event.getFoodLevel()-6.0) {
                        entity.setSaturation(event.getFoodLevel());
                    } else {
                        entity.setSaturation((float)(entity.getSaturation()+6.0));
                    }
                }
                case 4 -> { //candy! most likely.
                    //doesn't have a material check so we can make Other Candies with /give
                    Random random = new Random();
                    CANDY_EFFECTS[random.nextInt(CANDY_EFFECTS.length)].apply(event.getEntity());
                }
            }
        }
    }

    // Looting shears
    @EventHandler(priority = EventPriority.HIGH)
    public void playerShearEvent(PlayerShearEntityEvent event) {
        if (event.getEntity() instanceof Sheep sheep) {
            // check for looting level
            ItemStack shears = event.getItem();
            if (shears.getType() == Material.SHEARS) {
                if (shears.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_MOBS)) {
                    int level = shears.getItemMeta().getEnchantLevel(Enchantment.LOOT_BONUS_MOBS);

                    // Generate random number of wool to drop based on looting level
                    Random random = new Random();
                    int max = 3 + level;
                    int numWool = random.nextInt((max-level)+1) + level;

                    // Make sheep drop their respective color of wool
                    DyeColor woolColor = sheep.getColor();
                    if (woolColor == null) {
                        woolColor = DyeColor.WHITE;
                    }

                    Material wool = Material.getMaterial(woolColor + "_" + "WOOL");

                    //Reset the old shearing event and execute our new one
                    if (wool != null) {
                        event.setCancelled(true);
                        sheep.setSheared(true);
                        event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemStack(wool, numWool));

                        Player player = event.getPlayer();
                        player.playSound(sheep.getLocation(),Sound.ENTITY_SHEEP_SHEAR, 1f, 1f);

                        // TODO: Implement unbreaking here

                        if (!player.getGameMode().equals(GameMode.CREATIVE)) {

                            ItemStack shearsInventory = player.getInventory().getItem(event.getHand());

                            if (shearsInventory != null) {
                                ItemMeta shearsMeta = shearsInventory.getItemMeta();

                                if (shearsMeta instanceof Damageable shearsDamageable) {
                                    int newDamage = shearsDamageable.getDamage() + 1;
                                    shearsDamageable.setDamage(newDamage);

                                    if (shearsDamageable.getDamage() < 0) {
                                        shearsInventory.setAmount(0);
                                    } else {
                                        shearsInventory.setItemMeta(shearsMeta);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (event.getEntity() instanceof MushroomCow) {
            // check for looting level
            ItemStack shears = event.getItem();
            if (shears.getType() == Material.SHEARS) {
                if (shears.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_MOBS)) {
                    int level = shears.getItemMeta().getEnchantLevel(Enchantment.LOOT_BONUS_MOBS);

                    // make mooshroom drop the extra 1-3 respective type of mushroom
                    MushroomCow mooshroom = (MushroomCow) event.getEntity();
                    MushroomCow.Variant variant = mooshroom.getVariant();

                    Material itemMat;

                    if (variant == MushroomCow.Variant.RED) {
                        itemMat = Material.RED_MUSHROOM;
                    } else  {
                        itemMat = Material.BROWN_MUSHROOM;
                    }

                    event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(),
                            new ItemStack(itemMat, level));
                }
            }
        }
    }

    // Pocket Teleporter checks

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        Integer val = item.getItemMeta().getPersistentDataContainer().get(customKey, PersistentDataType.INTEGER);
        if (val == null) {
            return;
        }
        if (val == 6) {
            if(item.getType() != Material.LEATHER_HORSE_ARMOR) {
                Parallelutils.log(Level.WARNING, "Items with tag 'ParallelItems:6' are " +
                        "pocket_teleporter, but this is not the correct material. Something isn't right.");
                return;
            }
            ParallelItems.posManager.cancelTeleport(event.getPlayer(), "drop");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTakeDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            ParallelItems.posManager.cancelTeleport(player, "damage");
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                if (positionSaver == null || positionSaver.isCancelled()) {
                    Parallelutils.log(Level.SEVERE, "Position saver is not running! Totem of the Void is dysfunctional!");
                    return;
                }
                // sanity check in case the below code takes a while to run for whatever reason
                // and they get damaged again
                if (attemptingToSave.contains(player)) {
                    event.setCancelled(true);
                    return;
                }
                // only run if they have a saved position
                if (!lastSafePosition.containsKey(player)) {
                    return;
                }
                // only run if the player is about to die from the void
                if (player.getHealth() - event.getDamage() <= 0) {
                    ItemStack item = player.getInventory().getItemInMainHand();
                    ItemMeta meta = item.getItemMeta();
                    Integer val;
                    if (meta == null) {
                        item = player.getInventory().getItemInOffHand();
                        meta = item.getItemMeta();
                        if (meta == null)
                            return;
                    }
                    val = meta.getPersistentDataContainer().get(customKey, PersistentDataType.INTEGER);
                    if (val == null)
                        return;
                    if (val == 7) {
                        if (item.getType() != Material.GOLDEN_HORSE_ARMOR) {
                            Parallelutils.log(Level.WARNING, "Items with tag 'ParallelItems:7' are " +
                                    "totem_of_the_void, but this is not the correct material. Something isn't right.");
                            return;
                        }
                        item.subtract();
                        event.setCancelled(true);
                        player.setHealth(20);
                        // give player temporary invulnerability to prevent further void damage
                        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 4));
                        // ugly line but sound has to follow the player
                        player.playSound(net.kyori.adventure.sound.Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "item.totem.use"), net.kyori.adventure.sound.Sound.Source.MASTER, 1f, 1f), net.kyori.adventure.sound.Sound.Emitter.self());
                        // close player elytra so the teleport works correctly
                        player.setGliding(false);
                        attemptingToSave.add(player);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (player.teleport(lastSafePosition.get(player))) {
                                    // cancel leftover fall damage
                                    player.setFallDistance(0);
                                    ParallelChat.sendParallelMessageTo(player, "Your <light_purple>Totem of the Void<green> saved you from the void!");
                                    attemptingToSave.remove(player);
                                    this.cancel();
                                }
                                // refresh every tick just in case
                                player.setHealth(20);
                                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 4));
                            }
                        }.runTaskTimer(javaPlugin, 0, 1);
                    }
                }
            }
        }
    }


    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        ParallelItems.posManager.cancelTeleport(event.getPlayer(), "disconnect");
    }
}
