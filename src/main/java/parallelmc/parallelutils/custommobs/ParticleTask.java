package parallelmc.parallelutils.custommobs;

import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.Registry;

import java.util.Collection;

public class ParticleTask extends BukkitRunnable {

    private final Plugin plugin;

    public ParticleTask(Plugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Collection<EntityPair> pairs = Registry.getEntities();
        if (pairs.isEmpty()){
            this.cancel();
        }
        for(EntityPair pair : pairs){
            if(pair.entity == null) {
                continue;
            }
            ParticleData data = Registry.getParticleData(pair.type);
            if(data != null){
                World world = (org.bukkit.World) pair.entity.getWorld();
                world.spawnParticle(data.particle, pair.entity.getBukkitEntity().getLocation(), data.amount,
                        data.hSpread, data.vSpread, data.hSpread, data.speed);
            }
            else{
                continue;
            }
        }
    }
}
