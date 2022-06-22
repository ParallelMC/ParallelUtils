package parallelmc.parallelutils.modules.custommobs.spawners;

import net.minecraft.world.entity.Mob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.custommobs.nmsmobs.EntityFireWisp;
import parallelmc.parallelutils.modules.custommobs.nmsmobs.EntityWisp;
import parallelmc.parallelutils.modules.custommobs.nmsmobs.SpawnReason;
import parallelmc.parallelutils.modules.custommobs.registry.SpawnerRegistry;
import parallelmc.parallelutils.util.DistanceTools;

import java.util.Collection;
import java.util.Random;
import java.util.logging.Level;

/**
 * The BukkitRunnable responsible for spawning entities through mob spawners
 */
public class SpawnTask extends BukkitRunnable {
	private final JavaPlugin plugin;
	private final String type;
	private final SpawnerOptions options;
	private final SpawnerData data;
	private final Location location;
	private int timer;

	private final Random random;

	@SuppressWarnings("FieldCanBeLocal")
	private final int MAX_TRIES = 10;

	/**
	 * Create a new SpawnTask with a given spawner type, location, and starting number of entities
	 *
	 * @param type       The type of spawner associated with this SpawnTask
	 * @param location   The Location of the associated spawner
	 * @param startCount The starting count of mobs associated with this spawner
	 */
	public SpawnTask(String type, Location location, int startCount) {
		PluginManager manager = Bukkit.getPluginManager();
		plugin = (JavaPlugin) manager.getPlugin(Constants.PLUGIN_NAME);

		if (plugin == null) {
			Parallelutils.log(Level.SEVERE, "Unable to create SpawnTask. Plugin " + Constants.PLUGIN_NAME + "does not exist!");
		}

		this.type = type;
		this.options = SpawnerRegistry.getInstance().getSpawnerOptions(type);
		this.location = location;
		this.data = SpawnerRegistry.getInstance().getSpawner(location);
		this.timer = 0;

		random = new Random();

		SpawnerRegistry.getInstance().addCount(location, startCount);
	}

	@Override
	public void run() {
		if (location.getWorld() == null) { // if the world is null, we shouldn't be running a spawner in it
			this.cancel();
			return;
		} else if (timer < options.warmup) { // If the warmup isn't over, add cooldown and skip this run
			timer += options.cooldown;
			return;
		}

		if (!options.checkForPlayers || playerInRange()) { // Either you don't need to check for players, or one is close
			for (int i = 0; i < options.mobsPerSpawn; i++) { // Run at least once
				if (SpawnerRegistry.getInstance().getMobCount(location) < options.maxMobs) { // Count from spawner < max
					boolean wasSuccessful = false;
					int tries = 0;

					while (tries < MAX_TRIES && !wasSuccessful) {
						// Create a random spawn location in radius
						Location spawnLocation = new Location(location.getWorld(),
								location.getX(), location.getY(), location.getZ());
						double randomX = random.nextInt((options.radiusH + 1)*2)-(options.radiusH + 1);
						double randomY = random.nextInt((options.radiusV + 1)*2)-(options.radiusV + 1);
						double randomZ = random.nextInt((options.radiusH + 1)*2)-(options.radiusH + 1);

						spawnLocation.add(randomX + 0.5, randomY, randomZ + 0.5);

						// Try to spawn entity there
						Mob setUpEntity = null;

						Block blockBelow = location.getWorld().getBlockAt(spawnLocation);

						if (blockBelow.isEmpty() &&
								location.getWorld().getBlockAt(spawnLocation.clone().add(0, 1, 0)).isEmpty() &&
								blockBelow.getLightLevel() >= options.minLightLevel && blockBelow.getLightLevel() <= options.maxLightLevel) {
							wasSuccessful = true;
							switch (type) {
								case "wisp" -> setUpEntity = EntityWisp.spawn(plugin, (CraftServer)
										plugin.getServer(), (CraftWorld) location.getWorld(), spawnLocation, SpawnReason.SPAWNER, location);
								case "fire_wisp" -> setUpEntity = EntityFireWisp.spawn(plugin, (CraftServer)
										plugin.getServer(), (CraftWorld) location.getWorld(), spawnLocation, SpawnReason.SPAWNER, location);
								default -> wasSuccessful = false;
							}
							if (setUpEntity != null) {
								SpawnerRegistry.getInstance().incrementMobCount(location);
							}

							if (setUpEntity != null && data.hasLeash()) {
								SpawnerRegistry.getInstance().addLeashedEntity(location, setUpEntity.getUUID().toString());
								if (SpawnerRegistry.getInstance().getLeashTaskID(location) == null) {
									BukkitTask task = new LeashTask(location).runTaskTimer(plugin, 0, 10);
									SpawnerRegistry.getInstance().addLeashTaskID(location, task.getTaskId());
								}
							}
						}

						tries++;
					}
				} else {
					break;
				}
			}
		}
	}

	private boolean playerInRange() {
		Collection<? extends Player> online = plugin.getServer().getOnlinePlayers();
		for (Player player : online) {
			if (DistanceTools.distance(location, player.getLocation()) < options.activationRange) {
				return true;
			}
		}
		return false;
	}
}
