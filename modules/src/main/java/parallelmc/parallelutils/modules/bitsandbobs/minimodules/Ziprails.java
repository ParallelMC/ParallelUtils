package parallelmc.parallelutils.modules.bitsandbobs.minimodules;

import org.bukkit.*;
import org.bukkit.block.data.Attachable;
import org.bukkit.entity.Minecart;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelUtils;

import java.util.logging.Level;

public class Ziprails implements Listener {

    private static final double MINECART_DEFAULT_MAX_SPEED = 0.4d;
    NamespacedKey key;

    public Ziprails() {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);
        if (plugin == null) {
            ParallelUtils.log(Level.SEVERE, "Unable to enable Ziprails. Plugin " + Constants.PLUGIN_NAME
                    + " does not exist!");
            return;
        }

        key = new NamespacedKey(plugin, "isZipping");

        plugin.getServer().getScheduler().runTaskTimer(plugin, this::checkForZiprails, 0L, 4L);
    }

    public void checkForZiprails() {
        for (World world : Bukkit.getWorlds()) {
            // Should only be returning a list of loaded minecarts
            for (Minecart minecart : world.getEntitiesByClass(Minecart.class)) {
                // Check if minecart is moving
                Vector cartVelocity = minecart.getVelocity();
                if (cartVelocity.isZero()) {
                    continue;
                }

                Location cartLocation = minecart.getLocation();
                // Check if minecart is on ziprail
                Location blockAboveCart = cartLocation.clone().add(0, 1, 0);
                Material blockMaterial = world.getBlockAt(blockAboveCart).getType();
                if (blockMaterial == Material.TRIPWIRE || blockMaterial == Material.TRIPWIRE_HOOK) {
                    Attachable blockdata = (Attachable) world.getBlockAt(blockAboveCart).getBlockData();
                    // Cases for when the block above the minecart is an attachable ziprail
                    if (blockdata.isAttached()) {
                        // If the minecart isn't zipping, link it to the ziprail
                        if (!isZipping(minecart)) {
                            linkMinecart(minecart, cartLocation);
                        }
                        setMaxXZVelocity(minecart, cartVelocity);
                    }
                } else {
                    if (isZipping(minecart)) {
                        unlinkMinecart(minecart);
                    }
                }
            }
        }
    }

    public boolean isZipping(Minecart minecart) {
        return minecart.getPersistentDataContainer().has(key, PersistentDataType.INTEGER);
    }

    public void linkMinecart(Minecart minecart, Location cartLocation) {
        minecart.setGravity(false);

        // Makes sure that all ziprail cars are at the same y level
        Location newCartLocation = cartLocation.clone();
        newCartLocation.setY(Math.floor(cartLocation.getY() + 0.36250001192093)); // this is the y value GM4 uses lol
        minecart.teleport(newCartLocation);

        minecart.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
        minecart.getWorld().playSound(cartLocation, Sound.ENTITY_ITEM_FRAME_PLACE, SoundCategory.NEUTRAL, 1, 1);
    }

    public void unlinkMinecart(Minecart minecart) {
        minecart.setGravity(true);
        minecart.getPersistentDataContainer().remove(key);
    }

    public void setMaxXZVelocity(Minecart minecart, Vector cartVelocity) {
        // A normalized vector is used here to make sure the cart remains going the right way along the x or z axis
        Vector normalized = cartVelocity.normalize();

        if (cartVelocity.getX() != 0) {
            cartVelocity.setX(normalized.getX() * MINECART_DEFAULT_MAX_SPEED);
            cartVelocity.setZ(0);
        }
        if (cartVelocity.getZ() != 0) {
            cartVelocity.setZ(normalized.getZ() * MINECART_DEFAULT_MAX_SPEED);
            cartVelocity.setX(0);
        }

        minecart.setVelocity(cartVelocity);
    }
}