package parallelmc.parallelutils.modules.biometweaks.biomes;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import parallelmc.parallelutils.util.BukkitTools;
import parallelmc.parallelutils.util.RandomTools;


public class PaleGarden {

    public PaleGarden() {
        Plugin plugin = BukkitTools.getPlugin();
        if (plugin == null) return;

        plugin.getServer().getScheduler().runTaskTimer(plugin, this::gardenEffects, 0L, 20L);
    }

    public void gardenEffects() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Location location = player.getLocation();
            // Check that player is in pale garden and at y=60 at minimum
            if (location.getBlock().getBiome() == Biome.PALE_GARDEN && location.getY() >= 60) {
                // Sound effects
                player.playSound(location, Sound.BLOCK_CONDUIT_AMBIENT, SoundCategory.PLAYERS, 1, 1);
                // 1/20 chance of ambient cave sound
                if (RandomTools.betweenTwoNumbers(1, 20) == 1) {
                    player.playSound(location, Sound.AMBIENT_CAVE, SoundCategory.AMBIENT, 1, 1);
                }
                // 1/100 chance of thunder sound
                if (RandomTools.betweenTwoNumbers(1, 100) == 1) {
                    player.playSound(location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 1, 1);
                }

                // Particle effects
                player.spawnParticle(Particle.FALLING_WATER, location.getX(), location.getY() + 20, location.getZ(),
                        200, 10, 10, 10, 4);
            }
        }
    }

}
