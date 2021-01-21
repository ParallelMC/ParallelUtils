package parallelmc.parallelutils;

import com.google.gson.Gson;
import net.minecraft.server.v1_16_R3.EntityZombie;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftZombie;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import parallelmc.parallelutils.commands.Commands;
import parallelmc.parallelutils.custommobs.CustomTypes;
import parallelmc.parallelutils.custommobs.EntityWisp;
import parallelmc.parallelutils.custommobs.NMSWisp;

import java.io.File;
import java.sql.*;
import java.util.UUID;

public final class Parallelutils extends JavaPlugin {

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

		// Save the config
		config.options().copyDefaults(true);

		String jdbc, address, database, username="", password="";
		jdbc = config.getString("sql.jdbc");

		if (jdbc == null || jdbc.trim().equals("")) {
			address = config.getString("sql.address");
			database = config.getString("sql.database");
			username = config.getString("sql.username");
			password = config.getString("sql.password");

			jdbc = "jdbc:mysql://" +address + "/" + database;
		}

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
					"UUID VARCHAR(36)" +
					"Type VARCHAR(16)" +
					"World VARCHAR(32)" +
					"ChunkX INT" +
					"ChunkZ INT" +
					")");

			dbConn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Load mobs
		try {
			Statement statement = dbConn.createStatement();
			ResultSet result = statement.executeQuery("SELECT * IN WorldMobs");
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

				Entity mob = Bukkit.getEntity(UUID.fromString(uuid));

				if (mob != null) {
					switch (type) {
						case "wisp":
							EntityWisp.setupNBT(this, (CraftZombie)mob);

							EntityZombie wisp = ((CraftZombie) mob).getHandle();
							NMSWisp.initPathfinder(wisp);
							break;
						default:
							getLogger().warning("[ParallelUtils] Unknown entity type \"" + type + "\"");
					}
				} else {
					System.out.println("Mob is null!");
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
	}

	private void openDatabaseConnection(String jdbc, String username, String password) throws SQLException, ClassNotFoundException {
		if (dbConn != null && !dbConn.isClosed()) {
			return;
		}
		Class.forName("com.mysql.jdbc.Driver");
		dbConn = DriverManager.getConnection(jdbc, username, password);
	}
}
