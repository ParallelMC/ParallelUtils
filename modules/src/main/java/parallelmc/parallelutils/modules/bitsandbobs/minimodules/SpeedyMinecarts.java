package parallelmc.parallelutils.modules.bitsandbobs.minimodules;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelUtils;

import java.util.HashMap;
import java.util.logging.Level;

public class SpeedyMinecarts implements Listener {

    private static final double DEFAULT_MINECART_SPEED = 0.4d;
    private static final int SPEED_BOOST_SECONDS = 3;
    private final HashMap<Minecart, Integer> minecartList = new HashMap<>();
    private final Plugin plugin;

    public SpeedyMinecarts() {
        PluginManager manager = Bukkit.getPluginManager();
        plugin = manager.getPlugin(Constants.PLUGIN_NAME);
        if (plugin == null) {
            ParallelUtils.log(Level.SEVERE, "Unable to enable SpeedyMinecarts. Plugin " + Constants.PLUGIN_NAME
                    + " does not exist!");
        }
    }

    @EventHandler
    public void onMinecartMove(VehicleMoveEvent event) {
        if (event.getVehicle() instanceof Minecart minecart) {
            Location cartlocation = minecart.getLocation();

            // Checks if the block below the minecart is a powered rail
            if (minecart.getWorld().getBlockAt(cartlocation).getType() == Material.POWERED_RAIL) {

                // Checks if the block below the rail is a redstone block
                Location blockbelowRail = cartlocation.add(0, -1, 0);

                if (minecart.getWorld().getBlockAt(blockbelowRail).getType() == Material.REDSTONE_BLOCK) {

                    Vector cartVelocity = minecart.getVelocity();
                    // *SHOULD* set the minecart max speed to 20 m/s (default is 8 m/s)
                    minecart.setMaxSpeed(DEFAULT_MINECART_SPEED * 2.5);
                    cartVelocity.multiply(2.5d);

                    // Checks if the minecart is NOT in the map - if it's not, it'll add it to the map
                    // and remove the extra speed in 3 seconds IF no extra redstone block is driven over
                    if (!minecartList.containsKey(minecart)) {

                        // Adds the minecart to the hashmap
                        minecartList.put(minecart, SPEED_BOOST_SECONDS);
                        // Counts down the speed boost timer from 3 seconds to 0 seconds
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                int secondsRemaining = minecartList.get(minecart);
                                // If the timer runs out, remove the minecart from the hashmap, reset its speed, and
                                // cancel the runnable
                                if (secondsRemaining == 0) {
                                    minecartList.remove(minecart);
                                    minecart.setMaxSpeed(DEFAULT_MINECART_SPEED);
                                    cancel();
                                } else {
                                    secondsRemaining--;
                                    minecartList.put(minecart, secondsRemaining);
                                }
                            }
                        }.runTaskTimer(plugin, 20L, 20L);
                    }
                    // If another redstone block is driven over, the boost timer is reset to 3 seconds
                    else if (minecartList.containsKey(minecart)) {
                        minecartList.put(minecart, SPEED_BOOST_SECONDS);
                    }
                }
            }
        }
    }
}
