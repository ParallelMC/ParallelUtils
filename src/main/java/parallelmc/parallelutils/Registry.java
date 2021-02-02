package parallelmc.parallelutils;

import net.minecraft.server.v1_16_R3.Entity;
import parallelmc.parallelutils.custommobs.nmsmobs.EntityPair;
import parallelmc.parallelutils.custommobs.particles.ParticleOptions;
import parallelmc.parallelutils.custommobs.spawners.SpawnerOptions;

import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;

public class Registry {

	private HashMap<String, EntityPair> entities;

	private HashMap<String, ParticleOptions> entityParticles;

	private HashMap<String, SpawnerOptions> spawners;

	private static Registry registry;

	private Registry() {
		entities = new HashMap<>();
		entityParticles = new HashMap<>();
	}

	public static Registry getInstance() {
		if (registry == null) {
			registry = new Registry();
		}

		return registry;
	}

	public void registerEntity(String uuid, String type, Entity entity) {
		Parallelutils.log(Level.INFO, "Registering entity " + uuid);
		entities.put(uuid, new EntityPair(type, entity));
	}

	public void registerParticles(String type, ParticleOptions options) {
		Parallelutils.log(Level.INFO, "Registering particles for " + type);
		entityParticles.put(type, options);
	}

	public void registerSpawner(String type, SpawnerOptions options){
		Parallelutils.log(Level.INFO, "Registering spawner for " + type);
		spawners.put(type, options);
	}

	public SpawnerOptions getSpawnerOptions(String type){ return spawners.get(type);}

	public ParticleOptions getParticleOptions(String type){ return entityParticles.get(type);}

	public EntityPair getEntity(String uuid) {
		return entities.get(uuid);
	}

	public Collection<String> getUUIDs() {
		return entities.keySet();
	}

	public Collection<EntityPair> getEntities() {
		return entities.values();
	}

	public EntityPair removeEntity(String uuid) {
		Parallelutils.log(Level.INFO, "Removing entity " + uuid);
		return entities.remove(uuid);
	}

	public boolean containsEntity(String uuid) {
		Parallelutils.log(Level.ALL, "Does contain " + uuid);
		return entities.containsKey(uuid);
	}

	public boolean particleTaskRunning = false;
}
