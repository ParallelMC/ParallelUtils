package parallelmc.parallelutils;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import parallelmc.parallelutils.commands.*;
import parallelmc.parallelutils.versionchecker.UpdateChecker;

import javax.annotation.Nullable;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

// TODO: Add proper versioning to prevent loading invalid configs/data
public final class Parallelutils extends JavaPlugin {

	private static final String HEADER =
			"""
					#################
					# ParallelUtils #
					#################""";

	public static Level LOG_LEVEL = Level.INFO;

	FileConfiguration config;

	private DataSource dataSource;

	private List<ParallelModule> availableModules = new ArrayList<>();

	private HashMap<String, ParallelModule> registeredModules;
	private Commands commands;


	private boolean loadedModules = false;

	@Override
	public void onLoad() {
		registeredModules = new HashMap<>();

		loadModules();
		loadedModules = true;

		for (ParallelModule module : availableModules) {
			try {
				module.onLoad();
			} catch (Exception e) {
				Parallelutils.log(Level.SEVERE, "Unable to load module " + module.getName());
			}
		}
	}

	@Override
	public void onEnable() {
		// Plugin startup logic

		config = this.getConfig();

		config.options().copyDefaults(true);
		config.options().copyHeader(true);
		config.options().header(HEADER);

		// Read config
		this.saveDefaultConfig();

		int logLevel = config.getInt("debug", 2);

		switch (logLevel) {
			case 1 -> LOG_LEVEL = Level.ALL;
			case 2 -> LOG_LEVEL = Level.INFO;
			case 3 -> LOG_LEVEL = Level.WARNING;
			case 4 -> LOG_LEVEL = Level.SEVERE;
		}

		Bukkit.getLogger().setLevel(LOG_LEVEL);


		// Check version
		String github_token = config.getString("github_token");

		if (github_token != null && !github_token.trim().equals("") && !github_token.trim().equals("githubApiToken")) {
			// Actually check version
			UpdateChecker checker = new UpdateChecker(github_token);
			Version latestVersion = checker.getLatestVersion();

			if (latestVersion != null) {
				int comp = latestVersion.compareTo(Constants.VERSION);

				if (comp > 0) {
					log(Level.WARNING, "There is a new version of ParallelUtils available for download at https://github.com/ParallelMC/ParallelUtils/releases/latest");
				} else if (comp == 0) {
					log(Level.WARNING, "You are running the latest version of ParallelUtils");
				} else {
					log(Level.WARNING, "You are running a dev version of ParallelUtils. If this is on a production server, something is broken!");
				}
			}
		} else {
			log(Level.WARNING, "github_token not found in config. Will not check for updates.");
		}

		// Either get the database connection URL from the config or construct it from the config
		String address, database;

		address = config.getString("sql.address");
		database = config.getString("sql.database");

		String username = config.getString("sql.username");
		String password = config.getString("sql.password");

		if (address == null) { // TODO: Make this not quit when the database is invalid
			log(Level.SEVERE, "Could not retrieve database address. Stopping");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		String[] parts = address.split(":");

		String host = address;
		int port = 3306;

		if (parts.length == 2) {
			host = parts[0];
			String portStr = address.split(":")[1].trim();

			try {
				port = Integer.parseInt(portStr);
			} catch (NumberFormatException e) {
				log(Level.WARNING, "Invalid address string. Using default port");
				port = 3306;
			}
		}

		// Connect to database

		try {
			if (!createDataSource(host, port, database, username, password)) {
				Parallelutils.log(Level.SEVERE, "Unable to establish data source. Quitting...");
				Bukkit.getPluginManager().disablePlugin(this);
				return;
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			Parallelutils.log(Level.SEVERE, "Unable to establish data source. Quitting...");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		commands = new Commands();

		addCommand("help", new ParallelHelpCommand());
		addCommand("test", new ParallelTestCommand());
		addCommand("wait", new ParallelWaitCommand(this));

		getCommand("parallelutils").setExecutor(commands);
		getCommand("parallelutils").setTabCompleter(commands);
		getCommand("pu").setExecutor(commands);
		getCommand("pu").setTabCompleter(commands);



		// Setup modules

		if (!loadedModules) {
			loadModules();
			loadedModules = true;
		}

		for (ParallelModule module : availableModules) {
			try {
				module.onEnable();
			} catch (Exception e) {
				Parallelutils.log(Level.SEVERE, "Error while enabling module " + module.getName());
			}
		}
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic

		// Clean up modules
		registeredModules.forEach((name, module) -> {
			try {
				module.onDisable();
			} catch (Exception e) {
				Parallelutils.log(Level.SEVERE, "EXCEPTION WHILE DISABLING PARALLELUTILS. CAUGHT TO AVOID PROBLEMS");
				e.printStackTrace();
			}
		});
		registeredModules = new HashMap<>();
		loadedModules = false;
	}

	private void loadModules() {
		File modulesPath = new File("modules/");

		if (!modulesPath.isDirectory()) {
			Parallelutils.log(Level.SEVERE, "MODULES DIRECTORY NOT FOUND");
			return;
		}

		File[] files = modulesPath.listFiles();

		if (files == null) {
			Parallelutils.log(Level.SEVERE, "MODULES DIRECTORY NOT FOUND");
			return;
		}

		for (File file : files) {
			try (URLClassLoader child = new URLClassLoader(
					new URL[]{file.toURI().toURL()},
					this.getClass().getClassLoader()
			)) {
				Field f = child.getClass().getDeclaredField("classes");
				f.setAccessible(true);

				List<Class<?>> classes = (List<Class<?>>) f.get(child);

				for (Class<?> c : classes) {
					try {
						Annotation annotation = c.getAnnotation(Module.class);
						if (annotation != null) {
							// This class is the Module class
							Class<? extends ParallelModule> moduleClass = (Class<? extends ParallelModule>) c;
							ParallelModule module = moduleClass.getConstructor().newInstance();

							availableModules.add(module);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (IOException | NoSuchFieldException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}


	/**
	 * Opens a database connection with the given details and stores it to dbConn
	 * @param host The host address of the database
	 * @param port The port of the database
	 * @param database The database of the database
	 * @param username The username used to connect
	 * @param password The password used to connect
	 * @throws SQLException if a database access error occurs
	 * @throws ClassNotFoundException if the Driver class cannot be found
	 * @return True if the data source was successfully created and tested
	 */
	private boolean createDataSource(String host, int port, String database, String username, String password)
			throws SQLException, ClassNotFoundException {

		MysqlDataSource dataSource = new MysqlConnectionPoolDataSource();

		dataSource.setServerName(host);
		dataSource.setPort(port);
		dataSource.setDatabaseName(database);
		dataSource.setUser(username);
		dataSource.setPassword(password);
		dataSource.setAutoReconnect(true); // Hopefully this fixes the database issues...
		dataSource.setMaxReconnects(1);
		dataSource.setConnectTimeout(5000);

		this.dataSource = dataSource;

		Connection conn = testAndGetConnection(dataSource);

		return conn != null;
	}

	/**
	 * Creates a new database Connection from the given DataSource and tests if it is valid
	 * @param dataSource The DataSource to create from
	 * @return The Connection created from the DataSource
	 * @throws SQLException Thrown when DataSource#getConnection encounters a database access error
	 */
	@Nullable
	private Connection testAndGetConnection(DataSource dataSource) throws SQLException {
		Connection conn = dataSource.getConnection();

		if (!conn.isValid(1000)) {
			log(Level.WARNING, "Unable to establish database connection");
			return null;
		}
		return conn;
	}

	/**
	 * Returns a DB connection object for ParallelUtils
	 * @return the DB Connection object
	 */
	@Nullable
	public Connection getDbConn() {
		try {
			Connection connection = testAndGetConnection(dataSource);

			if (connection == null) return null;

			connection.setAutoCommit(false);
			return connection;
		} catch (SQLException throwables) {
			log(Level.WARNING, "Unable to retrieve database Connection. SQL Exception");
			throwables.printStackTrace();
			return null;
		}
	}

	/**
	 * Registers a ParallelModule with ParallelUtils
	 * @param name The name of the module
	 * @param module The module object
	 * @return True if the module was successfully registered, false otherwise
	 */
	public boolean registerModule(ParallelModule module) {
		String name = module.getName();
		
		if (registeredModules.containsKey(name)) {
			return false;
		}

		Parallelutils.log(Level.INFO, "Registered Module " + name);

		registeredModules.put(name, module);
		return true;
	}

	/**
	 * Gets registered module by name
	 * @param name
	 * @return module
	 */
	public ParallelModule getModule(String name){
		return registeredModules.get(name);
	}

	/**
	 * Disables a ParallelUtils module
	 * @param name The name of the module to disable
	 * @return True if the module was disabled successfully, false otherwise
	 */
	public boolean disableModule(String name) {
		ParallelModule module = getModule(name);

		if (name != null) {
			module.onDisable();
			registeredModules.remove(name);
			return true;
		}
		return false;
	}

	/**
	 * Wrapper for {@code parallelmc.parallelutils.commands.Commands.addCommand}
	 * Adds a new command to the commandmap
	 * @param name The name of the command
	 * @param command The command to be run when the name is called
	 * @return Returns true when the command was added successfully, false if the command already exists.
	 */
	public boolean addCommand(String name, ParallelCommand command) {
		return commands.addCommand(name, command);
	}

	/**
	 * Wrapper for {@code parallelmc.parallelutils.commands.Commands.getCommands}
	 * @return A deep copy of the command map
	 */
	public Map<String, ParallelCommand> getCommands() {
		return commands.getCommands();
	}

	/**
	 * A helper method to log a message at a specific log level with the prefix "[ParallelUtils] "
	 * @param level The level to log the message at
	 * @param message The message to log
	 */
	public static void log(Level level, String message) {
		Bukkit.getLogger().log(level, "[ParallelUtils] " + message);
	}
}
