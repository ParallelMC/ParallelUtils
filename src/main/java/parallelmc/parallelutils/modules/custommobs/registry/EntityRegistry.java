package parallelmc.parallelutils.modules.custommobs.registry;

import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.custommobs.nmsmobs.EntityData;
import parallelmc.parallelutils.modules.custommobs.nmsmobs.SpawnReason;
import parallelmc.parallelutils.modules.custommobs.spawners.SpawnerData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * This singleton class is responsible for keeping track of entities with their UUID and their EntityData object
 */
public class EntityRegistry {

	private final HashMap<String, EntityData> entities;

	private static EntityRegistry registry;

	private final Parallelutils puPlugin;

	private EntityRegistry() {
		entities = new HashMap<>();

		PluginManager manager = Bukkit.getPluginManager();
		Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

		if (plugin == null) {
			Parallelutils.log(Level.SEVERE, "Unable to get ParallelUtils. Plugin " + Constants.PLUGIN_NAME + " does not exist!");
			puPlugin = null;
			return;
		}

		puPlugin = (Parallelutils) plugin;
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
		updateEntityDatabase(uuid);
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
		updateEntityDatabase(uuid);
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
		updateEntityDatabase(uuid);
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
		deleteEntityDatabase(uuid);
		return entities.remove(uuid);
	}

	/**
	 * Updates an entity with a new entity object
	 * @param uuid The uuid of the entity to update
	 * @param entity The new Entity object of the entity
	 * @return True if the entity was updated successfully. False if the entity does not exist or it otherwise failed to update
	 */
	public boolean updateEntity(String uuid, Entity entity) {
		EntityData entityData = entities.get(uuid);
		if (entityData == null) return false;

		entityData.entity = entity;

		updateEntityDatabase(uuid);

		return true;
	}

	/**
	 * Checks if the given entity is currently registered
	 * @param uuid The UUID of the entity to check
	 * @return Returns true if the given entity is in the registry
	 */
	public boolean containsEntity(String uuid) {
		return entities.containsKey(uuid);
	}


	/**
	 * Adds/updates the entity in the database
	 * @param uuid The UUID of the entity to update
	 */
	private void updateEntityDatabase(String uuid) {
		EntityData ed = getEntity(uuid);

		if (ed == null) return;

		// Note: If this causes problems, move everything except the actual sql outside of the runnable
		Bukkit.getScheduler().runTaskAsynchronously(puPlugin, new Runnable() {
			@Override
			public void run() {
				try (Connection conn = puPlugin.getDbConn()) {
					if (conn == null) throw new SQLException("Unable to establish connection!");

					// Insert data if UUID not exist, otherwise update
					PreparedStatement statement = conn.prepareStatement(
							"REPLACE WorldMobs(UUID, Type, World, ChunkX, ChunkZ, spawnReason, spawnerId) " +
									"VALUES(?, ?, ?, ?, ?, ?, ?) "
					);

					Entity entity = ed.entity;

					if (entity == null) {
						Parallelutils.log(Level.WARNING, "Entity is null. Will not update!");
						statement.close();
						conn.close();
						return;
					}

					CraftEntity craftEntity = entity.getBukkitEntity();

					String uuid = craftEntity.getUniqueId().toString();

					String type = ed.type;

					if (type == null) {
						Parallelutils.log(Level.WARNING, "Unknown entity type for entity " + uuid);
						return;
					}

					String world = craftEntity.getWorld().getName();

					Chunk c = craftEntity.getChunk();

					SpawnReason reason = ed.spawnReason;

					statement.setString(1, uuid);
					statement.setString(2, type);
					statement.setString(3, world);
					statement.setInt(4, c.getX());
					statement.setInt(5, c.getZ());
					statement.setString(6, reason.name());

					if (reason == SpawnReason.SPAWNER) {
						SpawnerData data = SpawnerRegistry.getInstance().getSpawner(ed.spawnOrigin);

						if (data != null) {
							String spawnerId = data.getUuid();
							statement.setString(7, spawnerId);
						} else {
							Parallelutils.log(Level.INFO, "Spawner does not exist. Ignoring");
							statement.setString(6, SpawnReason.UNKNOWN.name());
						}
					} else {
						statement.setString(7, null);
					}

					statement.execute();

					conn.commit();

					statement.close();
				} catch (SQLException e) {
					Parallelutils.log(Level.WARNING, "Unable to update entity in database!");
					e.printStackTrace();
				}
			}
		});
	}

	private void deleteEntityDatabase(String uuid) {
		Bukkit.getScheduler().runTaskAsynchronously(puPlugin, new Runnable() {
			@Override
			public void run() {
				try (Connection conn = puPlugin.getDbConn()) {
					if (conn == null) throw new SQLException("Unable to establish connection!");

					PreparedStatement statement = conn.prepareStatement(
							"DELETE FROM WorldMobs WHERE UUID = ?"
					);

					statement.setString(1, uuid);

					statement.execute();

					conn.commit();

					statement.close();
				} catch (SQLException e) {
					Parallelutils.log(Level.WARNING, "Unable to delete entity from database!");
					e.printStackTrace();
				}
			}
		});
	}
}
