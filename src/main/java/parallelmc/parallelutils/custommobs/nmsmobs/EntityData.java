package parallelmc.parallelutils.custommobs.nmsmobs;

import net.minecraft.server.v1_16_R3.Entity;
import org.bukkit.Location;

public class EntityData {
	public String type = "";
	public Entity entity = null;
	public SpawnReason spawnReason = SpawnReason.UNKNOWN;
	public Location spawnOrigin = null;

	public EntityData(String type, Entity entity) {
		this.type = type;
		this.entity = entity;
	}

	public EntityData(String type, Entity entity, SpawnReason spawnReason) {
		this(type, entity);
		this.spawnReason = spawnReason;
	}

	public EntityData(String type, Entity entity, SpawnReason spawnReason, Location spawnOrigin) {
		this(type, entity, spawnReason);
		this.spawnOrigin = spawnOrigin;
	}
}
