package parallelmc.parallelutils;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftZombie;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import parallelmc.parallelutils.commands.Commands;
import parallelmc.parallelutils.custommobs.*;

import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

public final class Parallelutils extends JavaPlugin implements Listener {

	public static Level LOG_LEVEL = Level.INFO;

	String baseDataFolder = this.getDataFolder().getAbsolutePath();
	FileConfiguration config = this.getConfig();

	public static CustomTypes mobTypes;

	public static Connection dbConn;

	@Override
	public void onLoad() {
		mobTypes = new CustomTypes();

		// More startup logic here
		short id = 54;
		try {
			mobTypes.addEntityType("wisp", EntityWisp.class, id);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onEnable() {
		// Plugin startup logic
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

		String jdbc, address, database, username="", password="";
		jdbc = config.getString("sql.jdbc");

		System.out.println(jdbc);

		if (jdbc == null || jdbc.trim().equals("")) {
			address = config.getString("sql.address");
			database = config.getString("sql.database");

			jdbc = "jdbc:mysql://" +address + "/" + database;
		}

		username = config.getString("sql.username");
		password = config.getString("sql.password");

		System.out.println(jdbc);

		try {
			openDatabaseConnection(jdbc, username, password);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		saveConfig();

		// Create the table if it doesn't exist
		try {
			Statement statement = dbConn.createStatement();
			statement.execute("CREATE TABLE IF NOT EXISTS WorldMobs" +
					"(" +
					"UUID VARCHAR(36)," +
					"Type VARCHAR(16)," +
					"World VARCHAR(32)," +
					"ChunkX INT," +
					"ChunkZ INT" +
					")");
			dbConn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Load mobs
		try {
			Statement statement = dbConn.createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM WorldMobs");
			while (result.next()) {
				String uuid = result.getString("UUID");
				String type = result.getString("Type");
				String world = result.getString("World");
				String chunkX = result.getString("ChunkX");
				String chunkZ = result.getString("ChunkZ");

				int worldX = 16 * Integer.parseInt(chunkX);
				int worldZ = 16 * Integer.parseInt(chunkZ);

				//Bukkit.getServer().createWorld(new WorldCreator(world)); // This loads the world

				if (!(new Location(Bukkit.getWorld(world), worldX, 70, worldZ)).getChunk().isLoaded())
				{
					(new Location(Bukkit.getWorld(world), worldX, 70, worldZ)).getChunk().load();
				}

				CraftEntity mob = (CraftEntity)Bukkit.getEntity(UUID.fromString(uuid));

				String entityType = "";
				EntityInsentient setupEntity = null;

				if (mob != null) {
					switch (type) {
						case "wisp":
							entityType = "wisp";
							setupEntity = NMSWisp.setup(this, (CraftZombie)mob);
							break;
						default:
							getLogger().warning("[ParallelUtils] Unknown entity type \"" + type + "\"");
					}
				} else {
					System.out.println("Mob is null!");
				}

				if (setupEntity != null) {
					Registry.registerEntity(uuid, entityType, setupEntity);
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Registry.registerParticles("wisp", new ParticleData(Particle.CLOUD, 50, 0.5, 1, 0));



		getServer().getPluginManager().registerEvents(this, this);

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

		// Update the database here
		try (PreparedStatement statement = dbConn.prepareStatement("INSERT INTO WorldMobs (UUID, Type, World, ChunkX, ChunkZ) VALUES (?, ?, ?, ?, ?)")) {
			int i = 0;

			for (EntityPair ep : Registry.getEntities()) {
				Entity e = ep.entity;
				CraftEntity craftEntity = e.getBukkitEntity();

				String uuid = craftEntity.getUniqueId().toString();

				Bukkit.getLogger().log(Level.INFO, "[ParallelUtils] Storing entity " + uuid);

				String type = ep.type;

				if (type == null) {
					Bukkit.getLogger().log(Level.ALL, "[ParallelUtils] Unknown entity type for entity " + uuid);
					continue;
				}

				String world = craftEntity.getWorld().getName();

				Chunk c = craftEntity.getChunk();

				statement.setString(1, uuid);
				statement.setString(2, type);
				statement.setString(3, world);
				statement.setInt(4, c.getX());
				statement.setInt(5, c.getZ());

				statement.addBatch();
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

	}

	@EventHandler
	public void onEntityDespawn(final EntityRemoveFromWorldEvent event) {
		CraftEntity entity = (CraftEntity)event.getEntity();
		if (Registry.containsEntity(entity.getUniqueId().toString())) {
			Bukkit.getLogger().log(Level.ALL, "[ParallelUtils] Removing entity " + entity.getUniqueId().toString() + " from world");
			Registry.removeEntity(entity.getUniqueId().toString());
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
		Player player = event.getEntity();
		EntityDamageEvent lastDamage = player.getLastDamageCause();
		if(lastDamage instanceof EntityDamageByEntityEvent){
			org.bukkit.entity.Entity killer = ((EntityDamageByEntityEvent) lastDamage).getDamager();
			if(Registry.containsEntity(killer.getUniqueId().toString())){
				EntityPair pair = Registry.getEntity(killer.getUniqueId().toString());
				switch(pair.type){
					case "wisp":
						event.setDeathMessage(player.getDisplayName() + " was slain by Wisp");
						break;
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
}
