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
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;

// TODO: Add proper versioning to prevent loading invalid configs/data
public final class ParallelUtils extends JavaPlugin {

	private static final String HEADER =
			"""
					#################
					# ParallelUtils #
					#################""";

	public static Level LOG_LEVEL = Level.INFO;

	FileConfiguration config;

	private DataSource dataSource;

	private final HashMap<String, ParallelModule> availableModules = new HashMap<>();

	private HashMap<String, ParallelModule> registeredModules;

	private HashMap<ParallelModule, ClassLoader> classloaders = new HashMap<>();
	private Commands commands;


	private boolean loadedModules = false;

	@Override
	public void onLoad() {
		registeredModules = new HashMap<>();

		loadModules();
		loadedModules = true;

		for (ParallelModule module : availableModules.values()) {
			try {
				module.onLoad();
				ParallelUtils.log(Level.INFO, "Loaded module " + module.getName());
			} catch (Exception e) {
				ParallelUtils.log(Level.SEVERE, "Unable to load module " + module.getName());
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
				ParallelUtils.log(Level.SEVERE, "Unable to establish data source. Quitting...");
				Bukkit.getPluginManager().disablePlugin(this);
				return;
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			ParallelUtils.log(Level.SEVERE, "Unable to establish data source. Quitting...");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		commands = new Commands();

		addCommand("help", new ParallelHelpCommand());
		addCommand("test", new ParallelTestCommand());
		addCommand("wait", new ParallelWaitCommand(this));
		addCommand("modules", new ParallelModulesCommand(this));
		addCommand("unload", new ParallelUnloadCommand(this));
		addCommand("reload", new ParallelReloadCommand(this));

		getCommand("parallelutils").setExecutor(commands);
		getCommand("parallelutils").setTabCompleter(commands);
		getCommand("pu").setExecutor(commands);
		getCommand("pu").setTabCompleter(commands);



		// Setup modules

		if (!loadedModules) {
			loadModules();
			loadedModules = true;
		}

		for (ParallelModule module : availableModules.values()) {
			try {
				module.onEnable();
				ParallelUtils.log(Level.INFO, "Enabled module " + module.getName());
			} catch (Exception e) {
				ParallelUtils.log(Level.SEVERE, "Error while enabling module " + module.getName());
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
				ParallelUtils.log(Level.SEVERE, "EXCEPTION WHILE DISABLING PARALLELUTILS. CAUGHT TO AVOID PROBLEMS");
				e.printStackTrace();
			}
		});
		registeredModules = new HashMap<>();
		loadedModules = false;
	}

	private void loadModules() {
		File modulesPath = new File(this.getDataFolder(), "modules");

		if (!modulesPath.isDirectory()) {
			ParallelUtils.log(Level.SEVERE, "MODULES DIRECTORY NOT FOUND");
			return;
		}

		File[] files = modulesPath.listFiles();

		if (files == null) {
			ParallelUtils.log(Level.SEVERE, "MODULES DIRECTORY NOT FOUND");
			return;
		}

		for (File file : files) {
			String fixedName = file.getName().replaceFirst("[.][^.]+$", "");

			loadModule(fixedName);
		}
	}

	public boolean loadModule(String name) {
		String formatted = name.toLowerCase() + ".jar";

		File modulesPath = new File(this.getDataFolder(), "modules");

		if (!modulesPath.isDirectory()) {
			ParallelUtils.log(Level.SEVERE, "MODULES DIRECTORY NOT FOUND");
			return false;
		}

		File file = new File(modulesPath, formatted);

		if (!file.exists() || file.isDirectory()) {
			ParallelUtils.log(Level.WARNING, "Module " + name + " not found!");
			return false;
		}

		try {
			URL jar = file.toURI().toURL();
			URLClassLoader classLoader = new URLClassLoader(new URL[]{jar}, this.getClass().getClassLoader());
			List<String> matches = new ArrayList<>();
			List<Class<? extends ParallelModule>> classes = new ArrayList<>();

			try (JarInputStream jarInputStream = new JarInputStream(jar.openStream())) {
				JarEntry entry;
				while ((entry = jarInputStream.getNextJarEntry()) != null) {
					String entryName = entry.getName();
					if (entryName.endsWith(".class"))
						matches.add(entryName.substring(0, entryName.lastIndexOf('.')).replace('/', '.'));
				}
			}

			for (String match : matches) {
				try {
					Class<?> clazz = classLoader.loadClass(match);
					if (ParallelModule.class.isAssignableFrom(clazz)) {
						classes.add(clazz.asSubclass(ParallelModule.class));
					}
				} catch (ClassNotFoundException e) {
					ParallelUtils.log(Level.SEVERE, "Error while loading module " + name);
					e.printStackTrace();
					return false;
				}
			}

			if (classes.size() == 0) {
				ParallelUtils.log(Level.SEVERE, "Error while loading module " + name);
				ParallelUtils.log(Level.SEVERE, "MODULE " + file.getName() + " DOES NOT CONTAIN A ParallelModule CLASS");
				classLoader.close();
				return false;
			}

			if (classes.size() > 1) {
				ParallelUtils.log(Level.SEVERE, "Error while loading module " + name);
				ParallelUtils.log(Level.SEVERE, "MODULE " + file.getName() + " CONTAINS MULTIPLE ParallelModule CLASSES");
				classLoader.close();
				return false;
			}

			ParallelModule module = classes.get(0).getDeclaredConstructor().newInstance();

			availableModules.put(module.getName(), module);
			ParallelUtils.log(Level.INFO, "Added module " + module.getName() + " to available modules");
		} catch (IOException | InvocationTargetException | InstantiationException | IllegalAccessException |
		         NoSuchMethodException e) {
			ParallelUtils.log(Level.SEVERE, "Error while loading module " + name);
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean unloadModule(String name) {
		ParallelModule module = availableModules.get(name);

		if (module == null) return false;

		if (!module.canUnload()) {
			ParallelUtils.log(Level.WARNING, "Module " + name + " not permitted to unload. Skipping...");
			return false;
		}

		boolean result = true;

		if (registeredModules.containsKey(name)) {
			result = disableModule(name);
		}

		if (!result) {
			ParallelUtils.log(Level.SEVERE, "Error occurred while unloading module " + name);
			return false;
		}

		try {
			module.onUnload();
		} catch (Exception e) {
			ParallelUtils.log(Level.SEVERE, "Error occurred while unloading module " + name);
			e.printStackTrace();
			// Explicitly say it's unloaded since it's less likely to cause problems
		}

		availableModules.remove(name);
		return true;
	}

	public void unloadModules() {
		List<String> tempModules = new ArrayList<>(availableModules.keySet());

		for (String name : tempModules) {
			unloadModule(name);
		}
	}

	public boolean isLoaded(String name) {
		return availableModules.containsKey(name);
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
	 * @param module The module object
	 * @return True if the module was successfully registered, false otherwise
	 */
	public boolean registerModule(ParallelModule module) {
		String name = module.getName();

		if (registeredModules.containsKey(name)) {
			return false;
		}

		ParallelUtils.log(Level.INFO, "Registered Module " + name);

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
	 * Returns a List of module names
	 * @return module nam,es
	 */
	public List<String> getModules() {
		return registeredModules.keySet().stream().toList();
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
	 * Removes a command from the commandmap
	 * @param name The name of the command to remove
	 * @return True if the command was removed successfully, false otherwise
	 */
	public boolean removeCommand(String name) {
		return commands.removeCommand(name);
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
