package parallelmc.parallelutils.custommobs.registry;

import net.minecraft.server.v1_16_R3.Entity;
import org.bukkit.Location;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.custommobs.nmsmobs.EntityData;
import parallelmc.parallelutils.custommobs.nmsmobs.SpawnReason;

import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * This singleton class is responsible for keeping track of entities with their UUID and their EntityData object
 */
public class EntityRegistry {

	private HashMap<String, EntityData> entities;

	private static EntityRegistry registry;

	private EntityRegistry() {
		entities = new HashMap<>();
	}

	/**
	 * Retrieve the singleton instance of this class
	 * @return The instance of this class
	 */
	public static EntityRegistry getInstance() {
		if (registry == null) {
			registry = new EntityRegistry();
		}

		return registry;
	}

	/**
	 * Register an entity with the Registry to keep track of it using it's UUID, type, and Entity object
	 * @param uuid The UUID of the entity being registered
	 * @param type The type of the entity being registered
	 * @param entity The Bukkit entity object of the entity being registered
	 */
	public void registerEntity(String uuid, String type, Entity entity) {
		Parallelutils.log(Level.INFO, "Registering entity " + uuid);
		entities.put(uuid, new EntityData(type, entity));
	}

	/**
	 * Register an entity with the Registry to keep track of it using it's UUID, type, and Entity object for a
	 * specified reason
	 * @param uuid The UUID of the entity being registered
	 * @param type The type of the entity being registered
	 * @param entity The Bukkit entity object of the entity being registered
	 * @param reason The reason this entity was spawned
	 */
	public void registerEntity(String uuid, String type, Entity entity, SpawnReason reason) {
		Parallelutils.log(Level.INFO, "Registering entity " + uuid);
		entities.put(uuid, new EntityData(type, entity, reason));
	}

	/**
	 * Register an entity with the Registry to keep track of it using it's UUID, type, and Entity object for a
	 * specified reason at a specified location
	 * @param uuid The UUID of the entity being registered
	 * @param type The type of the entity being registered
	 * @param entity The Bukkit entity object of the entity being registered
	 * @param reason The reason this entity was spawned
	 * @param origin The original location this entity was spawned at
	 */
	public void registerEntity(String uuid, String type, Entity entity, SpawnReason reason, Location origin) {
		Parallelutils.log(Level.INFO, "Registering entity " + uuid);
		entities.put(uuid, new EntityData(type, entity, reason, origin));
	}

	/**
	 * Return the entity data object associated with an entity's UUID
	 * @param uuid The UUID of the requested entity
	 * @return The EntityData of the specified entity
	 */
	public EntityData getEntity(String uuid) {
		return entities.get(uuid);
	}

	/**
	 * Return the UUIDs of all registered entities
	 * @return A collection of all the registered entities' UUIDs
	 */
	public Collection<String> getUUIDs() {
		return entities.keySet();
	}

	/**
	 * Return the EntityData objects of all registered entities
	 * @return A collection of all the registered entities' EntityData objects
	 */
	public Collection<EntityData> getEntities() {
		return entities.values();
	}

	/**
	 * Unregister the specified entity using it's UUID
	 * @param uuid The UUID of the entity to unregister
	 * @return Returns the EntityData object associated with the given UUID. Returns null if no entity with the given UUID was registered
	 */
	public EntityData removeEntity(String uuid) {
		Parallelutils.log(Level.INFO, "Removing entity " + uuid);
		return entities.remove(uuid);
	}

	/**
	 * Checks if the given entity is currently registered
	 * @param uuid The UUID of the entity to check
	 * @return Returns true if the given entity is in the registry
	 */
	public boolean containsEntity(String uuid) {
		Parallelutils.log(Level.INFO, "Does contain " + uuid);
		return entities.containsKey(uuid);
	}
}
