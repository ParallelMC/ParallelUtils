package parallelmc.parallelutils.custommobs.nmsmobs;

import net.minecraft.server.v1_16_R3.Entity;
import org.bukkit.Location;

/**
 * A data structure encapsulating data associated with NMS mobs.
 */
public class EntityData {
	public String type = "";
	public Entity entity = null;
	public SpawnReason spawnReason = SpawnReason.UNKNOWN;
	public Location spawnOrigin = null;

	/**
	 * Construct an EntityData object of a specific entity type and the associated entity object.
	 * @param type The type of entity given by {@code entity}
	 * @param entity The entity object
	 */
	public EntityData(String type, Entity entity) {
		this.type = type;
		this.entity = entity;
	}

	/**
	 * Construct an EntityData object of a specific entity type and the associated entity object,
	 * with a specific spawn reason
	 * @param type The type of entity given by {@code entity}
	 * @param entity The entity object
	 * @param spawnReason The SpawnReason type for the given entity's spawn
	 */
	public EntityData(String type, Entity entity, SpawnReason spawnReason) {
		this(type, entity);
		this.spawnReason = spawnReason;
	}

	/**
	 * Construct an EntityData object of a specific entity type and the associated entity object,
	 * with a specific spawn reason and origin location
	 * @param type The type of entity given by {@code entity}
	 * @param entity The entity object
	 * @param spawnReason The SpawnReason type for the given entity's spawn
	 * @param spawnOrigin The location of what spawned the entity
	 */
	public EntityData(String type, Entity entity, SpawnReason spawnReason, Location spawnOrigin) {
		this(type, entity, spawnReason);
		this.spawnOrigin = spawnOrigin;
	}
}
