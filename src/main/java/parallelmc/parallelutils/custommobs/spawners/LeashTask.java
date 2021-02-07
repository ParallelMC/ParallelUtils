package parallelmc.parallelutils.custommobs.spawners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftMob;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import parallelmc.parallelutils.custommobs.registry.SpawnerRegistry;
import parallelmc.parallelutils.util.DistanceTools;

import java.util.ArrayList;
import java.util.UUID;

public class LeashTask extends BukkitRunnable {

    private final JavaPlugin plugin;
    private final Location spawnerLocation;
    private final int leashRadius;
    private final SpawnerOptions options;

    public LeashTask(JavaPlugin plugin, Location spawnerLocation){
        this.plugin = plugin;
        this.spawnerLocation = spawnerLocation;
        String type = SpawnerRegistry.getInstance().getSpawner(spawnerLocation).getType();
        this.options = SpawnerRegistry.getInstance().getSpawnerOptions(type);
        this.leashRadius = options.leashRange;
    }

    @Override
    public void run() {
        ArrayList<String> mobs = SpawnerRegistry.getInstance().getLeashedEntityList(spawnerLocation);
        if(mobs == null || mobs.isEmpty()){
            this.cancel();
            return;
        }

        for(String uuid : mobs){
            CraftMob entity = (CraftMob) Bukkit.getEntity(UUID.fromString(uuid));
            if(entity == null){
                continue;
            }
            if(DistanceTools.distanceHorizontal(spawnerLocation, entity.getLocation()) > leashRadius){
                entity.teleport(spawnerLocation);
                if(options.resetHealthOnLeash){
                    entity.setHealth(entity.getMaxHealth());
                }
                if(options.resetThreatOnLeash){
                    entity.setTarget(null);
                }
            }
        }
    }
}
