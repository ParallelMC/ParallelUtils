package parallelmc.parallelutils.modules.bitsandbobs.minimodules;

import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelUtils;

import java.util.*;
import java.util.logging.Level;

public class CallingBell implements Listener {

    private static final HashMap<UUID, ArrayList<World>> traderSummoners = new HashMap<>();
    private static final String[] OVERWORLD_TYPE_WORLDS = Constants.OVERWORLD_TYPE_WORLDS;
    private static final int DAY_CHECK_INTERVAL = 1200;

    public CallingBell() {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);
        if (plugin == null) {
            ParallelUtils.log(Level.SEVERE, "Unable to enable CallingBell. Plugin " + Constants.PLUGIN_NAME
                    + " does not exist!");
            return;
        }

        // If this minute-long timer is too long, we can reduce it
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::dailyListReset, 0L, DAY_CHECK_INTERVAL);
    }

    @EventHandler
    public void onPlayerRightClickBell(PlayerInteractEvent event) {
        // Check if player clicked a bell holding an emerald
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        if (!event.getClickedBlock().getType().equals(Material.BELL)) {
            return;
        }

        PlayerInventory inventory = player.getInventory();
        if (!inventory.getItemInMainHand().getType().equals(Material.EMERALD) &&
                !inventory.getItemInOffHand().getType().equals(Material.EMERALD)) {
            return;
        }

        // Check if player right-clicked
        if (!event.getAction().isRightClick()) {
            return;
        }

        // Check if player is in an overworld-type world
        Location bellLocation = block.getLocation();
        World world = bellLocation.getWorld();
        if (!Arrays.asList(OVERWORLD_TYPE_WORLDS).contains(world.getName())) {
            return;
        }

        // Check if player hasn't summoned the villager yet in this world
        // If they haven't, remove emerald and summon trader
        // If they have, add them and their world into (or back into) the hashmap
        UUID playerUUID = player.getUniqueId();
        ArrayList<World> worldsOnCooldown = new ArrayList<>();
        if (traderSummoners.containsKey(playerUUID)) {
            worldsOnCooldown = traderSummoners.get(playerUUID);
            if (worldsOnCooldown.contains(world)) {
                return;
            }
        }
        worldsOnCooldown.add(world);
        traderSummoners.put(playerUUID, worldsOnCooldown);

        inventory.remove(new ItemStack(Material.EMERALD, 1));

        // This is done to center the spawning trader on the bell - it also overwrites the original location
        bellLocation.add(0.5, 1, 0.5);
        world.spawnEntity(bellLocation, EntityType.WANDERING_TRADER);

        // Add particles and sound
        world.spawnParticle(Particle.VILLAGER_HAPPY,bellLocation.getX(), bellLocation.getY() + 0.5,
                bellLocation.getZ(), 5, 0.3, 0.3, 0.3);
        world.spawnParticle(Particle.SMOKE_LARGE, bellLocation.getX(), bellLocation.getY() + 1,
                bellLocation.getZ(), 10, 0.25, 0.5, 0.25, 0);
        world.playSound(bellLocation, Sound.ENTITY_WANDERING_TRADER_REAPPEARED, SoundCategory.NEUTRAL,
                1, 1);

        // Check/give advancement
        awardAdvancement(player);

    }

    public void dailyListReset() {
        ArrayList<World> worldsToReset = new ArrayList<>();
        // Check each world
        for (String worldString : OVERWORLD_TYPE_WORLDS) {
            // Check if world is null
            World world = Bukkit.getWorld(worldString);
            if (world == null) {
                ParallelUtils.log(Level.WARNING, "World " + worldString +
                        " is registered as an overworld-type world but the world is not loaded!");
                continue;
            }

            // Perform day check on the world - if it's a new day within the interval, add the world to the
            // worldsToReset list
            long worldTime = world.getTime();
            if (worldTime >= 0 && worldTime < DAY_CHECK_INTERVAL) {
                worldsToReset.add(world);
            }
        }

        // For each traderSummoners entry, remove all reset worlds from each player's worldsOnCooldown list
        // If there are no worlds left in the list after that, remove the player from the traderSummoners hashmap
        for (Map.Entry<UUID, ArrayList<World>> entry : traderSummoners.entrySet()) {
            UUID playerUUID = entry.getKey();
            ArrayList<World> worldsOnCooldown = entry.getValue();
            worldsOnCooldown.removeAll(worldsToReset);
            if (worldsOnCooldown.isEmpty()) {
                traderSummoners.remove(playerUUID);
            } else {
                traderSummoners.put(playerUUID, worldsOnCooldown);
            }
        }
    }

    public void awardAdvancement(Player player) {
        Advancement a = Bukkit.getAdvancement(new NamespacedKey("platy",
                "village/calling_bell"));
        if (a != null) {
            AdvancementProgress avp = player.getAdvancementProgress(a);
            if (!avp.isDone()) {
                for (String criteria : avp.getRemainingCriteria()) {
                    avp.awardCriteria(criteria);
                }
            }
        }
    }
}
