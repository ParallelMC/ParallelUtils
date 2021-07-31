package parallelmc.parallelutils.modules.custommobs;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftZombie;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitTask;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.custommobs.commands.ParallelCreateSpawnerCommand;
import parallelmc.parallelutils.modules.custommobs.commands.ParallelDeleteSpawnerCommand;
import parallelmc.parallelutils.modules.custommobs.commands.ParallelListSpawnersCommand;
import parallelmc.parallelutils.modules.custommobs.commands.ParallelSummonCommand;
import parallelmc.parallelutils.modules.custommobs.events.CustomMobsEventRegistrar;
import parallelmc.parallelutils.modules.custommobs.nmsmobs.EntityData;
import parallelmc.parallelutils.modules.custommobs.nmsmobs.EntityFireWisp;
import parallelmc.parallelutils.modules.custommobs.nmsmobs.EntityWisp;
import parallelmc.parallelutils.modules.custommobs.nmsmobs.SpawnReason;
import parallelmc.parallelutils.modules.custommobs.particles.ParticleOptions;
import parallelmc.parallelutils.modules.custommobs.registry.EntityRegistry;
import parallelmc.parallelutils.modules.custommobs.registry.ParticleRegistry;
import parallelmc.parallelutils.modules.custommobs.registry.SpawnerRegistry;
import parallelmc.parallelutils.modules.custommobs.spawners.LeashTask;
import parallelmc.parallelutils.modules.custommobs.spawners.SpawnTask;
import parallelmc.parallelutils.modules.custommobs.spawners.SpawnerData;
import parallelmc.parallelutils.modules.custommobs.spawners.SpawnerOptions;

import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

public class CustomMobs implements ParallelModule {

	private static Parallelutils puPlugin;

	public void onEnable() {
		PluginManager manager = Bukkit.getPluginManager();
		Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

		if (plugin == null) {
			Parallelutils.log(Level.SEVERE, "Unable to enable CustomMobs. Plugin " + Constants.PLUGIN_NAME + " does not exist!");
			return;
		}

		puPlugin = (Parallelutils) plugin;

		if (!puPlugin.registerModule("CustomMobs", this)) {
			Parallelutils.log(Level.SEVERE, "Unable to register module CustomMobs! Module may already be registered. Quitting...");
			return;
		}

		SpawnerRegistry.getInstance().registerSpawnerType("wisp", new SpawnerOptions(0, 0, 8,
				1, 400, 0, true, 40, 0, 15, 16,
				false, false));
		SpawnerRegistry.getInstance().registerSpawnerType("fire_wisp", new SpawnerOptions(0, 0, 8,
				1, 400, 0, true, 40, 0, 15, 16,
				false, false));


		// Get dbConn

		// Create the table if it doesn't exist
		try (Connection conn = puPlugin.getDbConn()){
			if (conn == null) {
				Parallelutils.log(Level.WARNING, "Unable to establish connection to database. Disabling");
				puPlugin.disableModule("CustomMobs");
				return;
			} else {
				Statement statement = conn.createStatement();
				statement.setQueryTimeout(15);
				statement.execute("""
					create table if not exists WorldMobs
					(
					    UUID        varchar(36) not null,
					    Type        varchar(16) not null,
					    World       varchar(32) not null,
					    ChunkX      int         not null,
					    ChunkZ      int         not null,
					    spawnReason varchar(32) not null,
					    spawnerId   varchar(36) null,
					    constraint WorldMobs_UUID_uindex
					        unique (UUID),
					    PRIMARY KEY (UUID)
					);""");
				conn.commit();

				statement.execute("""
						create table if not exists Spawners
						(
						    id       varchar(36) not null,
						    type     varchar(16) not null,
						    world    varchar(32) null,
						    x        int         not null,
						    y        int         not null,
						    z        int         not null,
						    hasLeash tinyint     not null,
						    constraint Spawners_id_uindex
						        unique (id),
					        PRIMARY KEY (id)
						);""");
				conn.commit();

				statement.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Load spawners and mobs
		try (Connection conn = puPlugin.getDbConn()){
			if (conn == null) {
				Parallelutils.log(Level.WARNING, "Unable to establish connection to database. Disabling");
				puPlugin.disableModule("CustomMobs");
				return;
			} else {
				Statement statement = conn.createStatement();
				statement.setQueryTimeout(15);

				ResultSet spawnerResults = statement.executeQuery("SELECT * FROM Spawners");

				readSpawners(spawnerResults);

				ResultSet result = statement.executeQuery("SELECT * FROM WorldMobs");

				readMobs(result);

				statement.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		ParticleRegistry.getInstance().registerParticles("wisp", new ParticleOptions
				(Particle.CLOUD, 50, 0.5, 1, 0));
		ParticleRegistry.getInstance().registerParticles("fire_wisp", new ParticleOptions
				(Particle.LAVA, 40, 0.25, 0.5, 0));


		// Register events for the CustomMobs module
		CustomMobsEventRegistrar.registerEvents();

		// Setup commands
		puPlugin.addCommand("summon", new ParallelSummonCommand());
		puPlugin.addCommand("createspawner", new ParallelCreateSpawnerCommand());
		puPlugin.addCommand("listspawners", new ParallelListSpawnersCommand());
		puPlugin.addCommand("deletespawner", new ParallelDeleteSpawnerCommand());

	}

	public void onDisable() {

	}

	/**
	 * A helper method to parse the ResultSet from SQL and register the spawner data
	 *
	 * @param result The ResultSet to parse
	 * @throws SQLException if a database access error occurs or this method is called on a closed result set
	 */
	private void readSpawners(ResultSet result) throws SQLException {
		while (result.next()) {
			String id = result.getString("id");
			String type = result.getString("type");
			String world = result.getString("world");
			int x = result.getInt("x");
			int y = result.getInt("y");
			int z = result.getInt("z");
			boolean hasLeash = result.getBoolean("hasLeash");

			Location location = new Location(puPlugin.getServer().getWorld(world), x, y, z);

			SpawnerRegistry.getInstance().registerSpawner(id, type, location, hasLeash);

			// TODO: Replace puPlugin with `this` when in separate plugins
			BukkitTask task = new SpawnTask(type, location, 0)
					.runTaskTimer(puPlugin, 0, SpawnerRegistry.getInstance().
							getSpawnerOptions(type).cooldown);
			SpawnerRegistry.getInstance().addSpawnTaskID(location, task.getTaskId());
		}
	}

	/**
	 * A helper method to parse the ResultSet from SQL and register the mob data
	 *
	 * @param result The ResultSet to parse
	 * @throws SQLException if a database access error occurs or this method is called on a closed result set
	 */
	private void readMobs(ResultSet result) throws SQLException {
		while (result.next()) {
			String uuid = result.getString("UUID");
			String type = result.getString("Type");
			String world = result.getString("World");
			String chunkX = result.getString("ChunkX");
			String chunkZ = result.getString("ChunkZ");
			SpawnReason spawnReason = SpawnReason.valueOf(result.getString("spawnReason"));
			String spawnerId = result.getString("spawnerId");

			Location spawnerLocation = null;
			if (spawnReason == SpawnReason.SPAWNER) {
				try (Connection conn = puPlugin.getDbConn()) {
					if (conn == null) throw new SQLException("Unable to establish connection!");

					PreparedStatement statement = conn.prepareStatement("SELECT * FROM Spawners WHERE id=?");

					statement.setString(1, spawnerId);

					ResultSet spawnerResults = statement.executeQuery();
					if (!spawnerResults.next()) {
						Parallelutils.log(Level.WARNING, "Invalid spawner id " + spawnerId);
						continue;
					}

					String spawnerWorld = spawnerResults.getString("world");
					int spawnerX = spawnerResults.getInt("x");
					int spawnerY = spawnerResults.getInt("y");
					int spawnerZ = spawnerResults.getInt("z");

					spawnerLocation = new Location(Bukkit.getWorld(spawnerWorld), spawnerX, spawnerY, spawnerZ);

					statement.close();
				} catch (SQLException e) {
					Parallelutils.log(Level.WARNING, "Unable to read spawner for mob from database!");
					e.printStackTrace();
				}
			}

			int worldX = 16 * Integer.parseInt(chunkX);
			int worldZ = 16 * Integer.parseInt(chunkZ);

			//Bukkit.getServer().createWorld(new WorldCreator(world)); // This loads the world

			Location location = new Location(Bukkit.getWorld(world), worldX, 70, worldZ);

			if (!location.getChunk().isLoaded()) {
				location.getChunk().load();
			}

			// This may all be useless in 1.17
			CraftEntity mob = (CraftEntity) Bukkit.getEntity(UUID.fromString(uuid));

			EntityInsentient setupEntity = null;

			if (mob != null) {
				setupEntity = setupEntity(type, mob);
			}


			if (spawnerLocation != null) {
				EntityRegistry.getInstance().registerEntity(uuid, type, setupEntity, spawnReason, spawnerLocation);
				SpawnerRegistry.getInstance().incrementMobCount(spawnerLocation);

				if (SpawnerRegistry.getInstance().getSpawner(spawnerLocation).hasLeash()) {
					SpawnerRegistry.getInstance().addLeashedEntity(spawnerLocation, uuid);

					if (SpawnerRegistry.getInstance().getLeashTaskID(spawnerLocation) == null) {
						BukkitTask task = new LeashTask(spawnerLocation).runTaskTimer(puPlugin, 0, 10);
						SpawnerRegistry.getInstance().addLeashTaskID(spawnerLocation, task.getTaskId());
					}
				}
			} else {
				EntityRegistry.getInstance().registerEntity(uuid, type, setupEntity, spawnReason);
			}

		}
	}

	public static EntityInsentient setupEntity(String type, CraftEntity mob) {
		switch (type) {
			case "wisp" -> {
				return EntityWisp.setup(puPlugin, (CraftZombie) mob);
			}
			case "fire_wisp" -> {
				return EntityFireWisp.setup(puPlugin, (CraftZombie) mob);
			}
			default -> Parallelutils.log(Level.WARNING, "Unknown entity type \"" + type + "\"");
		}
		return null;
	}
}
