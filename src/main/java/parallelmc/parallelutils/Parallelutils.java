package parallelmc.parallelutils;

import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftZombie;
import org.bukkit.plugin.java.JavaPlugin;
import parallelmc.parallelutils.commands.Commands;
import parallelmc.parallelutils.custommobs.events.CustomMobsEventRegistrar;
import parallelmc.parallelutils.custommobs.nmsmobs.EntityData;
import parallelmc.parallelutils.custommobs.nmsmobs.EntityWisp;
import parallelmc.parallelutils.custommobs.nmsmobs.SpawnReason;
import parallelmc.parallelutils.custommobs.particles.ParticleOptions;
import parallelmc.parallelutils.custommobs.registry.EntityRegistry;
import parallelmc.parallelutils.custommobs.registry.ParticleRegistry;

import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

public final class Parallelutils extends JavaPlugin {

	public static Level LOG_LEVEL = Level.INFO;

	String baseDataFolder = this.getDataFolder().getAbsolutePath();
	FileConfiguration config = this.getConfig();

	public static Connection dbConn;

	@Override
	public void onLoad() {

	}

	@Override
	public void onEnable() {
		// Plugin startup logic

		// Read config
		this.saveDefaultConfig();
		this.reloadConfig();

		int logLevel = config.getInt("debug", 2);

		switch (logLevel) {
			case 1:
				LOG_LEVEL = Level.ALL;
				break;
			case 2:
				LOG_LEVEL = Level.INFO;
				break;
			case 3:
				LOG_LEVEL = Level.WARNING;
				break;
			case 4:
				LOG_LEVEL = Level.SEVERE;
				break;
		}

		Bukkit.getLogger().setLevel(LOG_LEVEL);

		// Either get the database connection URL from the config or construct it from the config
		String jdbc, address, database, username="", password="";
		jdbc = config.getString("sql.jdbc");

		if (jdbc == null || jdbc.trim().equals("")) {
			address = config.getString("sql.address");
			database = config.getString("sql.database");

			jdbc = "jdbc:mysql://" +address + "/" + database;
		}

		username = config.getString("sql.username");
		password = config.getString("sql.password");

		saveConfig();

		// Connect to database

		try {
			openDatabaseConnection(jdbc, username, password);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		// Create the table if it doesn't exist
		try {
			Statement statement = dbConn.createStatement();
			statement.execute("create table if not exists WorldMobs\n" +
					"(\n" +
					"    UUID        varchar(36) not null,\n" +
					"    Type        varchar(16) not null,\n" +
					"    World       varchar(32) not null,\n" +
					"    ChunkX      int         not null,\n" +
					"    ChunkZ      int         not null,\n" +
					"    spawnReason varchar(32) not null,\n" +
					"    spawnerId   varchar(36) null,\n" +
					"    constraint WorldMobs_UUID_uindex\n" +
					"        unique (UUID)\n" +
					");");
			dbConn.commit();

			statement.execute("create table if not exists Spawners\n" +
					"(\n" +
					"    id       varchar(36) not null,\n" +
					"    type     varchar(16) not null,\n" +
					"    world    varchar(32) null,\n" +
					"    x        int         not null,\n" +
					"    y        int         not null,\n" +
					"    z        int         not null,\n" +
					"    hasLeash tinyint     not null,\n" +
					"    constraint Spawners_id_uindex\n" +
					"        unique (id)\n" +
					");");
			dbConn.commit();

			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Load spawners and mobs
		try {
			Statement statement = dbConn.createStatement();

			ResultSet spawnerResults = statement.executeQuery("SELECT * FROM Spawners");

			readSpawners(spawnerResults);

			ResultSet result = statement.executeQuery("SELECT * FROM WorldMobs");

			readMobs(result);

			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		ParticleRegistry.getInstance().registerParticles("wisp", new ParticleOptions(Particle.CLOUD, 50, 0.5, 1, 0));

		// Register events for the CustomMobs module
		CustomMobsEventRegistrar.registerEvents();

		// Setup commands
		Commands commands = new Commands(this);

		getCommand("parallelutils").setExecutor(commands);
		getCommand("parallelutils").setTabCompleter(commands);
		getCommand("pu").setExecutor(commands);
		getCommand("pu").setTabCompleter(commands);
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic

		// Clear the database
		// TODO: DON'T DELETE DATABASE IF YOU HAVEN'T FINISHED SETUP
		try {
			Statement removeStatement = dbConn.createStatement();
			removeStatement.execute("TRUNCATE TABLE WorldMobs");
			removeStatement.execute("TRUNCATE TABLE Spawners");
			dbConn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Insert all mobs that we care about into the database
		try (PreparedStatement statement = dbConn.prepareStatement("INSERT INTO WorldMobs (UUID, Type, World, ChunkX, ChunkZ) VALUES (?, ?, ?, ?, ?)")) {
			int i = 0;

			for (EntityData ep : EntityRegistry.getInstance().getEntities()) {
				Entity e = ep.entity;
				CraftEntity craftEntity = e.getBukkitEntity();

				String uuid = craftEntity.getUniqueId().toString();

				Parallelutils.log(Level.INFO, "Storing entity " + uuid);

				String type = ep.type;

				if (type == null) {
					Parallelutils.log(Level.WARNING, "Unknown entity type for entity " + uuid);
					continue;
				}

				String world = craftEntity.getWorld().getName();

				Chunk c = craftEntity.getChunk();

				statement.setString(1, uuid);
				statement.setString(2, type);
				statement.setString(3, world);
				statement.setInt(4, c.getX());
				statement.setInt(5, c.getZ());

				// This just lets us execute a bunch of changes at once
				statement.addBatch();

				// This is here because some implementations of MySQL are weird and don't like very large batches
				i++;
				if (i >= 1000) {
					statement.executeBatch();
					i = 0;
				}
			}

			statement.executeBatch();

			dbConn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// TODO: Insert spawners

		try {
			dbConn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void readSpawners(ResultSet result) throws SQLException {
		while (result.next()) {
			String id = result.getString("id");
			// TODO: Read spawners
		}
	}

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
			if (spawnerId != null) {
				PreparedStatement statement = dbConn.prepareStatement("SELECT * FROM Spawners WHERE id=%");

				statement.setString(0, spawnerId);

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
			}

			int worldX = 16 * Integer.parseInt(chunkX);
			int worldZ = 16 * Integer.parseInt(chunkZ);

			//Bukkit.getServer().createWorld(new WorldCreator(world)); // This loads the world

			Location location = new Location(Bukkit.getWorld(world), worldX, 70, worldZ);

			if (!location.getChunk().isLoaded())
			{
				location.getChunk().load();
			}

			CraftEntity mob = (CraftEntity)Bukkit.getEntity(UUID.fromString(uuid));

			String entityType = "";
			EntityInsentient setupEntity = null;

			if (mob != null) {
				switch (type) {
					case "wisp":
						entityType = "wisp";
						setupEntity = EntityWisp.setup(this, (CraftZombie)mob);
						break;
					default:
						Parallelutils.log(Level.WARNING, "Unknown entity type \"" + type + "\"");
				}
			} else {
				Parallelutils.log(Level.WARNING, "Mob is null! Report this to the devs! Expected UUID: " + uuid);
			}

			if (setupEntity != null) {
				if (spawnerLocation != null) {
					EntityRegistry.getInstance().registerEntity(uuid, entityType, setupEntity, spawnReason, spawnerLocation);
				} else {
					EntityRegistry.getInstance().registerEntity(uuid, entityType, setupEntity, spawnReason);
				}
			}
		}
	}

	private void openDatabaseConnection(String jdbc, String username, String password) throws SQLException, ClassNotFoundException {
		if (dbConn != null && !dbConn.isClosed()) {
			return;
		}
		Class.forName("com.mysql.jdbc.Driver");
		dbConn = DriverManager.getConnection(jdbc, username, password);
		dbConn.setAutoCommit(false);
	}


	public static void log(Level level, String message) {
		Bukkit.getLogger().log(level, "[ParallelUtils] " + message);
	}
}
