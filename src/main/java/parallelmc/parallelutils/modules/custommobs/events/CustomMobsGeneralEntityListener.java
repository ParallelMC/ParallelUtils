package parallelmc.parallelutils.modules.custommobs.events;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.custommobs.CustomMobs;
import parallelmc.parallelutils.modules.custommobs.nmsmobs.EntityData;
import parallelmc.parallelutils.modules.custommobs.nmsmobs.SpawnReason;
import parallelmc.parallelutils.modules.custommobs.registry.EntityRegistry;
import parallelmc.parallelutils.modules.custommobs.registry.SpawnerRegistry;
import parallelmc.parallelutils.modules.custommobs.spawners.SpawnerData;

import java.util.logging.Level;

/**
 * For non-specific entity listeners
 **/
public class CustomMobsGeneralEntityListener implements Listener {

	// Handle despawning custom mobs and removing them from the registry
	@EventHandler
	public void onEntityDespawn(final EntityRemoveFromWorldEvent event) {
		if (Bukkit.isStopping()) return; // If we remove entities on shutdown, things get removed from the DB!

		CraftEntity entity = (CraftEntity) event.getEntity();
		String UUID = entity.getUniqueId().toString();

		if (entity.isPersistent() && !entity.isDead()) return; // This is required in 1.17+ because the event was changed

		if (EntityRegistry.getInstance().containsEntity(UUID)) {
			if (EntityRegistry.getInstance().getEntity(UUID).spawnReason == SpawnReason.SPAWNER) {
				Location spawner = EntityRegistry.getInstance().getEntity(UUID).spawnOrigin;
				SpawnerRegistry.getInstance().decrementMobCount(spawner);
				SpawnerData spawnerData = SpawnerRegistry.getInstance().getSpawner(spawner);

				if (spawnerData != null && spawnerData.hasLeash()) {
					SpawnerRegistry.getInstance().removeLeashedEntity(spawner, UUID);
				}
			}

			EntityRegistry.getInstance().removeEntity(UUID);
		}
	}

	@EventHandler
	public void onEntityAdd(final EntityAddToWorldEvent event) {
		if (Bukkit.isStopping()) return; // Probably not needed but doesn't hurt

		CraftEntity entity = (CraftEntity) event.getEntity();
		String UUID = entity.getUniqueId().toString();

		if (EntityRegistry.getInstance().containsEntity(UUID)) {
			EntityData entityData = EntityRegistry.getInstance().getEntity(UUID);
			if (entityData.entity == null) {
				CustomMobs.setupEntity(entityData.type, entity);
			}

			EntityRegistry.getInstance().updateEntity(UUID, entity.getHandle());
		}
	}
}
