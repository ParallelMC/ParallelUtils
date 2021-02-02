package parallelmc.parallelutils.custommobs.spawners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import parallelmc.parallelutils.custommobs.registry.SpawnerRegistry;

import java.util.Collection;

public class SpawnTask extends BukkitRunnable {
    private final Plugin plugin;
    private final String type;
    private final SpawnerOptions options;
    private final Location location;

    public SpawnTask(Plugin plugin, String type, Location location){
        this.plugin = plugin;
        this.type = type;
        this.options = SpawnerRegistry.getInstance().getSpawnerOptions(type);
        this.location = location;
    }

    @Override
    public void run() {
        if(location.getWorld() == null){
            return;
        }
        else if(!options.checkForPlayers || playerInRange()){
            for(int i = 0; i < options.mobsPerSpawn; i++) {
                if (SpawnerRegistry.getInstance().getMobCount(location) < options.maxMobs) {
                    //TODO: try to spawn the mobs here, and register leash stuff
                }
                else{
                    break;
                }
            }
        }
    }

    private boolean playerInRange(){
        Collection<? extends Player> online = plugin.getServer().getOnlinePlayers();
        for(Player player : online){
            if(distance(location, player.getLocation()) < options.activationRange){
                return true;
            }
        }
        return false;
    }

    private double distance(Location loc1, Location loc2){
        if(loc1.getWorld().equals(loc2.getWorld())){
            double distancex = loc1.getX() - loc2.getX();
            double distancey = loc1.getY() - loc2.getY();
            double distancez = loc1.getZ() - loc2.getZ();
            distancex *= distancex;
            distancey *= distancey;
            distancez *= distancez;
            return Math.sqrt(distancex + distancey + distancez);
        }
        return Integer.MAX_VALUE;
    }
}
