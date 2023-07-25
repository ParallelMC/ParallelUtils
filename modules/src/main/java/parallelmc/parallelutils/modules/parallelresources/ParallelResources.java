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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static java.nio.file.FileVisitResult.CONTINUE;

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

			File base_zip = new File(resourcesDir, "base.zip");

			if (!base_zip.exists()) {
				ParallelUtils.log(Level.WARNING, "Base zip does not exist! Will not continue");
				return;
			}

			File[] files = resourcesDir.listFiles();

			ArrayList<File> resourceMods = new ArrayList<>();

			if (files != null) {
				for (File f : files) {
					if (f.isDirectory()) {
						// This is now treated as a resource pack
						resourceMods.add(f);
					}
				}
			}

			File outDir = new File(resourcesDir, "generated/");

			if (!outDir.exists()) {
				Files.createDirectory(outDir.toPath());
			}

			List<File> packs = generatePacks(outDir, base_zip, resourceMods);

			server.addResource("base", base_zip);

			for (File f : packs) {
				server.addResource(f.getName().split("\\.")[0], f);
			}

		} catch (IOException e) {
			e.printStackTrace();
			ParallelUtils.log(Level.SEVERE, "IOException while loading ParallelResources! Quitting...");
			return;
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

	@NotNull
	public List<File> generatePacks(@NotNull File outDir, @NotNull File base_zip, @NotNull List<File> mods) throws IOException {
		// Load base into memory
		ArrayList<File> files = new ArrayList<>();

		for (File f : mods) {
			File out = generatePack(outDir, base_zip, f);

			if (out == null) {
				ParallelUtils.log(Level.WARNING, "Unable to generate pack for mod " + f.getName());
			} else {
				files.add(out);
			}
		}

		return files;
	}

	@Nullable
	public File generatePack(@NotNull File outDir, @NotNull File base, @NotNull File mod) throws IOException {

		// Copy base to a new file
		String outName = mod.getName() + ".zip";
		Path outPath = new File(outDir, outName).toPath();
		Files.copy(base.toPath(), outPath);

		// In future ops, catch IOException to delete generated file
		try {
			// Open zip file as a file system, so we can edit files in it
			URI uri = URI.create("jar:" + outPath.toUri());

			Path mod_path = mod.toPath();

			try (FileSystem fs = FileSystems.newFileSystem(uri, new HashMap<>())) {

				Path target = fs.getPath("");

				// Walk the file tree of the mod, creating relative paths and copying files into the zip file system
				Files.walkFileTree(mod_path, new SimpleFileVisitor<>() {
					@Override
					public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
							throws IOException
					{
						Path targetdir = target.resolve(mod_path.relativize(dir));
						try {
							Files.copy(dir, targetdir);
						} catch (FileAlreadyExistsException e) {
							if (!Files.isDirectory(targetdir))
								throw e;
						}
						return CONTINUE;
					}

					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
							throws IOException
					{
						Files.copy(file, target.resolve(mod_path.relativize(file)));
						return CONTINUE;
					}
				});
			}
		} catch (IOException e) {
			e.printStackTrace();
			Files.delete(outPath);
			return null;
		}

		return outPath.toFile();
	}

}
