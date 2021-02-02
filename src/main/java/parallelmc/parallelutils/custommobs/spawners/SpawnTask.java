package parallelmc.parallelutils.custommobs.spawners;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import parallelmc.parallelutils.Registry;

public class SpawnTask extends BukkitRunnable {
    private final Plugin plugin;
    private final String type;
    private final SpawnerOptions options;
    private int mobCount;

    public SpawnTask(Plugin plugin, String type, int mobCount){
        this.plugin = plugin;
        this.type = type;
        this.options = Registry.getInstance().getSpawnerOptions(type);
        this.mobCount = mobCount;
    }

    @Override
    public void run() {

    }
}
