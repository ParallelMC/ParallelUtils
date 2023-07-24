package parallelmc.parallelutils.modules.parallelresources;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelClassLoader;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.ParallelUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;

public class ParallelResources extends ParallelModule {

	public static ParallelUtils puPlugin;


	private ResourceServer server;
	private Thread serverThread;

	private YamlConfiguration resourcesConfig = new YamlConfiguration();

	public ParallelResources(ParallelClassLoader classLoader, List<String> dependents) {
		super(classLoader, dependents);
	}

	@Override
	public void onLoad() {
		PluginManager manager = Bukkit.getPluginManager();
		Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

		if (plugin == null) {
			ParallelUtils.log(Level.SEVERE, "Unable to enable ParallelResources. Plugin " + Constants.PLUGIN_NAME + " does not exist!");
			return;
		}

		puPlugin = (ParallelUtils) plugin;

		if (!puPlugin.registerModule(this)) {
			ParallelUtils.log(Level.SEVERE, "Unable to register module ParallelResources! Module may already be registered. Quitting...");
			return;
		}

		// Read config to get base_url, port

		try {
			// Create resources directory if it does not exist
			File resourcesDir = new File(puPlugin.getDataFolder(), "resources/");
			if (!resourcesDir.exists()) {
				Files.createDirectory(resourcesDir.toPath());
			}

			File resourcesFile = new File(resourcesDir, "parallelresources.yml");

			// Set defaults
			if (!resourcesFile.exists()) {
				resourcesConfig.set("domain", "resources.parallelmc.org");
				resourcesConfig.set("port", 4444);
				resourcesConfig.set("https", false);
				resourcesConfig.set("https_keystore", null);
				resourcesConfig.set("https_keystore_pass", null);
				resourcesConfig.save(resourcesFile);
			} else {
				resourcesConfig.load(resourcesFile);
			}

			String domain = resourcesConfig.getString("domain", "resources.parallelmc.org");

			// Initialize server
			int port = resourcesConfig.getInt("port", 4444);
			boolean https = resourcesConfig.getBoolean("https", false);
			String keystore = resourcesConfig.getString("https_keystore", null);
			String keystore_pass = resourcesConfig.getString("https_keystore_pass", null);

			server = new ResourceServer(port, https, keystore != null ? new File(resourcesDir, keystore) : null, keystore_pass);

			String https_head = https ? "https://" : "http://";

			String base_url = https_head + domain + ":" + port + "/";

			// Load resources
			



		} catch (IOException e) {
			e.printStackTrace();
			ParallelUtils.log(Level.SEVERE, "IOException while loading ParallelResources! Quitting...");
		} catch (InvalidConfigurationException e) {
			throw new RuntimeException(e);
		}

		serverThread = new Thread(server);
		serverThread.start();
	}

	@Override
	public void onEnable() {

	}

	@Override
	public void onDisable() {

	}

	@Override
	public void onUnload() {
		serverThread.interrupt();
		serverThread = null;
		server.destruct();
		server = null;
		resourcesConfig = null;
	}

	@Override
	public @NotNull String getName() {
		return "ParallelResources";
	}
}
