package parallelmc.parallelutils;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.sun.media.jfxmedia.logging.Logger;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityZombie;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftZombie;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import parallelmc.parallelutils.commands.Commands;
import parallelmc.parallelutils.custommobs.CustomTypes;
import parallelmc.parallelutils.custommobs.EntityWisp;
import parallelmc.parallelutils.custommobs.NMSWisp;

import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

public final class Parallelutils extends JavaPlugin {

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

				EntityInsentient setupEntity = null;

				if (mob != null) {
					switch (type) {
						case "wisp":
							setupEntity = NMSWisp.setup(this, (CraftZombie)mob);
							break;
						default:
							getLogger().warning("[ParallelUtils] Unknown entity type \"" + type + "\"");
					}
				} else {
					System.out.println("Mob is null!");
				}

				if (setupEntity != null) {
					Registry.registerEntity(uuid, setupEntity);
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}


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

		// Handle all entities here
	}

	@EventHandler
	public void onChunkUnload(final ChunkUnloadEvent event) {
		// Handle named and named entities here
	}

	@EventHandler
	public void onEntityDespawn(final EntityRemoveFromWorldEvent event) {

	}

	private void openDatabaseConnection(String jdbc, String username, String password) throws SQLException, ClassNotFoundException {
		if (dbConn != null && !dbConn.isClosed()) {
			return;
		}
		Class.forName("com.mysql.jdbc.Driver");
		dbConn = DriverManager.getConnection(jdbc, username, password);
	}
}
