package parallelmc.parallelutils.modules.custommobs.spawners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftMob;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.custommobs.registry.SpawnerRegistry;
import parallelmc.parallelutils.util.DistanceTools;

import java.util.Collection;
import java.util.UUID;
import java.util.logging.Level;

/**
 * This BukkitRunnable keeps mobs leashed to spawners when they have a leash enabled
 */
public class LeashTask extends BukkitRunnable {

	private final JavaPlugin plugin;
	private final Location spawnerLocation;
	private final int leashRadius;
	private final SpawnerOptions options;

	/**
	 * Create a new LeashTask for a spawner at a given location
	 * @param spawnerLocation The location of the spawner
	 */
	public LeashTask(Location spawnerLocation) {
		PluginManager manager = Bukkit.getPluginManager();
		plugin = (JavaPlugin) manager.getPlugin(Constants.PLUGIN_NAME);

		if (plugin == null) {
			Parallelutils.log(Level.SEVERE, "Unable to create LeashTask. Plugin " + Constants.PLUGIN_NAME + "does not exist!");
			throw new IllegalPluginAccessException("Unable to create LeashTask. Plugin " + Constants.PLUGIN_NAME + "does not exist!");
		}

		this.spawnerLocation = spawnerLocation;
		String type = SpawnerRegistry.getInstance().getSpawner(spawnerLocation).getType();
		this.options = SpawnerRegistry.getInstance().getSpawnerOptions(type);
		this.leashRadius = options.leashRange;
	}

	@Override
	public void run() {
		Collection<String> mobs = SpawnerRegistry.getInstance().getLeashedEntities(spawnerLocation);
		if (mobs == null || mobs.isEmpty()) {
			SpawnerRegistry.getInstance().removeLeashTaskID(spawnerLocation);
			this.cancel();
			return;
		}

		for (String uuid : mobs) {
			CraftMob entity = (CraftMob) Bukkit.getEntity(UUID.fromString(uuid));
			if (entity == null) {
				continue;
			}
			if (DistanceTools.distanceHorizontal(spawnerLocation, entity.getLocation()) > leashRadius) {
				CraftLivingEntity target = entity.getTarget();
				entity.teleport(spawnerLocation);
				if (options.resetHealthOnLeash) {
					entity.setHealth(entity.getMaxHealth());
				}
				if (options.resetThreatOnLeash) {
					entity.setTarget(null);
				} else {
					entity.setTarget(target);
				}
			}
		}
	}
}
