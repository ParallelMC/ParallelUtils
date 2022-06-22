package parallelmc.parallelutils.modules.custommobs.particles;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.custommobs.nmsmobs.EntityData;
import parallelmc.parallelutils.modules.custommobs.registry.EntityRegistry;
import parallelmc.parallelutils.modules.custommobs.registry.ParticleRegistry;

import java.util.Collection;
import java.util.logging.Level;

/**
 * A BukkitRunnable that spawns Particles at specific intervals on mobs
 */
public class ParticleTask extends BukkitRunnable {

	private final JavaPlugin plugin;

	public ParticleTask() {
		PluginManager manager = Bukkit.getPluginManager();
		plugin = (JavaPlugin) manager.getPlugin(Constants.PLUGIN_NAME);

		if (plugin == null) {
			Parallelutils.log(Level.SEVERE, "Unable to create SpawnTask. Plugin " + Constants.PLUGIN_NAME + "does not exist!");
			throw new IllegalPluginAccessException("Unable to create SpawnTask. Plugin " + Constants.PLUGIN_NAME + "does not exist!");
		}
	}

	@Override
	public void run() {
		Collection<EntityData> pairs = EntityRegistry.getInstance().getEntities();
		if (pairs.isEmpty()) {
			ParticleRegistry.getInstance().particleTaskRunning = false;
			this.cancel();
		}
		for (EntityData pair : pairs) {
			if (pair.entity == null) {
				continue;
			}
			ParticleOptions data = ParticleRegistry.getInstance().getParticleOptions(pair.type);
			if (data != null) {
				World world = pair.entity.getBukkitEntity().getWorld();

				//if (pair.entity.getBukkitEntity().isTicking()) { // TODO: Check this
					world.spawnParticle(data.particle, pair.entity.getBukkitEntity().getLocation(), data.amount,
							data.hSpread, data.vSpread, data.hSpread, data.speed);
				//}
			}
		}
	}
}
