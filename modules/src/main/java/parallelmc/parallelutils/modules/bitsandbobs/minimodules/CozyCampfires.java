package parallelmc.parallelutils.modules.bitsandbobs.minimodules;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CozyCampfires {

    private static final int RADIUS = 3;
    private static final PotionEffect ABSORPTION = new PotionEffect(PotionEffectType.ABSORPTION, 14400, 1, true, false);

    public void checkForCampfires() {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (isCampfireNearby(p.getLocation())) {
                p.addPotionEffect(ABSORPTION);
            }
        }
    }

    private boolean isCampfireNearby(Location location) {
        World world = location.getWorld();
        for (int x = -RADIUS; x <= RADIUS; x++) {
            for (int y = -RADIUS; y <= RADIUS; y++) {
                for (int z = -RADIUS; z <= RADIUS; z++) {
                    Block block = world.getBlockAt(add(location, x, y ,z));
                    if (block.getType() == Material.CAMPFIRE || block.getType() == Material.SOUL_CAMPFIRE) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // spigot add no make copy
    // this function make copy
    // :)
    private Location add(Location old, double x, double y, double z) {
        return old.clone().add(x, y ,z);
    }
}
