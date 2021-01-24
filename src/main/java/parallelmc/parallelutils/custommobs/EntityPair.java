package parallelmc.parallelutils.custommobs;

import net.minecraft.server.v1_16_R3.Entity;

public class EntityPair {
	public String type = "";
	public Entity entity = null;

	public EntityPair(String type, Entity entity) {
		this.type = type;
		this.entity = entity;
	}
}
