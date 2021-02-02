package parallelmc.parallelutils.custommobs.registry;

import org.bukkit.Location;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.custommobs.spawners.SpawnerOptions;

import java.util.HashMap;
import java.util.logging.Level;

public class SpawnerRegistry {

	private HashMap<String, SpawnerOptions> spawners;

	private HashMap<Location, Integer> mobCounts;

	private static SpawnerRegistry registry;

	private SpawnerRegistry() {
		spawners = new HashMap<>();
	}

	public static SpawnerRegistry getInstance() {
		if (registry == null) {
			registry = new SpawnerRegistry();
		}

		return registry;
	}

	public void registerSpawner(String type, SpawnerOptions options){
		Parallelutils.log(Level.INFO, "Registering spawner for " + type);
		spawners.put(type, options);
	}

	public SpawnerOptions getSpawnerOptions(String type){ return spawners.get(type);}

	public void addCount(Location loc, int count){
		Parallelutils.log(Level.INFO, "Registering counter for " + loc.toString());
		mobCounts.put(loc, count);
	}

	public int getMobCount(Location loc){return mobCounts.get(loc);}

	public void setMobCount(Location loc, int count){mobCounts.replace(loc, count); }

	//TODO: add leash stuff to registry
}
