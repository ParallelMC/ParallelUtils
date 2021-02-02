package parallelmc.parallelutils.custommobs.registry;

import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.custommobs.spawners.SpawnerOptions;

import java.util.HashMap;
import java.util.logging.Level;

public class SpawnerRegistry {

	private HashMap<String, SpawnerOptions> spawners;

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
}
