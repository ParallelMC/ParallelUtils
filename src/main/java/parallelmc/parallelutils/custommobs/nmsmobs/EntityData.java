package parallelmc.parallelutils.custommobs.nmsmobs;

import net.minecraft.server.v1_16_R3.Entity;

public class EntityData {
	public String type = "";
	public Entity entity = null;
	public SpawnReason spawnReason = null;

	public EntityData(String type, Entity entity) {
		this.type = type;
		this.entity = entity;
	}

	public EntityData(String type, Entity entity, SpawnReason spawnReason) {
		this(type, entity);
		this.spawnReason = spawnReason;
	}
}
