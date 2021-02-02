package parallelmc.parallelutils.custommobs.particles;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import parallelmc.parallelutils.Registry;
import parallelmc.parallelutils.custommobs.nmsmobs.EntityPair;

import java.util.Collection;

public class ParticleTask extends BukkitRunnable {

    private final Plugin plugin;

    public ParticleTask(Plugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Collection<EntityPair> pairs = Registry.getInstance().getEntities();
        if (pairs.isEmpty()){
            Registry.getInstance().particleTaskRunning = false;
            this.cancel();
        }
        for(EntityPair pair : pairs){
            if(pair.entity == null) {
                continue;
            }
            ParticleOptions data = Registry.getInstance().getParticleOptions(pair.type);
            if(data != null){
                World world = pair.entity.getBukkitEntity().getWorld();
                world.spawnParticle(data.particle, pair.entity.getBukkitEntity().getLocation(), data.amount,
                        data.hSpread, data.vSpread, data.hSpread, data.speed);
            }
        }
    }
}
