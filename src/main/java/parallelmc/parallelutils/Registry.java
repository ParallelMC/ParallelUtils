package parallelmc.parallelutils;

import net.minecraft.server.v1_16_R3.Entity;
import parallelmc.parallelutils.custommobs.nmsmobs.EntityPair;
import parallelmc.parallelutils.custommobs.ParticleData;

import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;

public class Registry {

	private static HashMap<String, EntityPair> entities = new HashMap<>();

	private static HashMap<String, ParticleData> entityParticles = new HashMap<>();

	public static void registerEntity(String uuid, String type, Entity entity) {
		Parallelutils.log(Level.INFO, "Registering entity " + uuid);
		entities.put(uuid, new EntityPair(type, entity));
	}

	public static void registerParticles(String type,ParticleData data) {
		Parallelutils.log(Level.INFO, "Registering particles for " + type);
		entityParticles.put(type, data);
	}

	public static ParticleData getParticleData(String type){ return entityParticles.get(type);}

	public static EntityPair getEntity(String uuid) {
		return entities.get(uuid);
	}

	public static Collection<String> getUUIDs() {
		return entities.keySet();
	}

	public static Collection<EntityPair> getEntities() {
		return entities.values();
	}

	public static EntityPair removeEntity(String uuid) {
		Parallelutils.log(Level.INFO, "Removing entity " + uuid);
		return entities.remove(uuid);
	}

	public static boolean containsEntity(String uuid) {
		Parallelutils.log(Level.ALL, "Does contain " + uuid);
		return entities.containsKey(uuid);
	}

	public static boolean particleTaskRunning = false;
}
