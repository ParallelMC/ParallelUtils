package parallelmc.parallelutils.modules.parallelresources;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
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
import parallelmc.parallelutils.modules.parallelresources.events.ResourcePackHandle;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static java.nio.file.FileVisitResult.CONTINUE;

public class ParallelResources extends ParallelModule {

	private static final String DEFAULT_WARNING = """
					<bold><yellow>Warning!</bold>
					<white>Parallel requires that you accept the resource pack to join the server.
					If you choose to reject this, you will be disconnected from the server.""";

	private static final List<String> DISALLOWED_WORLDS = List.of("base", "generated", "pre-squash");

	private File squashOut;

	private File modOut;

	public static ParallelUtils puPlugin;

	private ResourcePackHandle handler;
	private ResourceServer server;
	private Thread serverThread;

	private String base_url = "";

	private YamlConfiguration resourcesConfig = new YamlConfiguration();

	@Nullable
	private File packSquash = null;

	@Nullable
	private File packSquashConfig = null;

	private final HashMap<String, byte[]> resourceHashes = new HashMap<>();

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

			squashOut = new File(resourcesDir, "generated/");
			modOut = new File(resourcesDir, "pre-squash/");

			File resourcesFile = new File(resourcesDir, "parallelresources.yml");

			// Set defaults
			if (!resourcesFile.exists()) {
				resourcesConfig.set("domain", "resources.parallelmc.org");
				resourcesConfig.set("port", 4444);
				resourcesConfig.set("has_proxy", false);
				resourcesConfig.set("https", false);
				resourcesConfig.set("https_keystore", null);
				resourcesConfig.set("https_keystore_pass", null);
				resourcesConfig.set("warning_message", DEFAULT_WARNING);
				resourcesConfig.save(resourcesFile);
			} else {
				resourcesConfig.load(resourcesFile);
			}

			String domain = resourcesConfig.getString("domain", "resources.parallelmc.org");

			// Initialize server
			int port = resourcesConfig.getInt("port", 4444);
			boolean has_proxy = resourcesConfig.getBoolean("has_proxy", false);
			boolean https = resourcesConfig.getBoolean("https", false);
			String keystore = resourcesConfig.getString("https_keystore", null);
			String keystore_pass = resourcesConfig.getString("https_keystore_pass", null);
			String warning_message = resourcesConfig.getString("warning_message", DEFAULT_WARNING);
			Component warning_component = MiniMessage.miniMessage().deserialize(warning_message, TagResolver.standard());

			server = new ResourceServer(port, https, keystore != null ? new File(resourcesDir, keystore) : null, keystore_pass);

			// Use HTTPS if there's a proxy
			String https_head = (https|has_proxy) ? "https://" : "http://";

			// URL does not include port if there's a proxy
			if (has_proxy) {
				base_url = https_head + domain + "/";
			} else {
				base_url = https_head + domain + ":" + port + "/";
			}

			// Try loading packsquash

			File packSquashTemp = new File(resourcesDir, "packsquash");

			boolean hasSquash = false;

			if (packSquashTemp.exists() && !packSquashTemp.isDirectory()) {
				hasSquash = true;
			} else {
				packSquashTemp = new File(resourcesDir, "packsquash.exe");

				if (packSquashTemp.exists() && !packSquashTemp.isDirectory()) {
					hasSquash = true;
				} else {
					ParallelUtils.log(Level.WARNING, "packsquash binary not found in ParallelUtils/resources directory. Will not use");
				}
			}

			if (hasSquash) {
				File packSquashConfigTemp = new File(resourcesDir, "packsquash.toml");

				if (packSquashConfigTemp.exists()) {
					packSquash = packSquashTemp;
					packSquashConfig = packSquashConfigTemp;
				} else {
					ParallelUtils.log(Level.WARNING, "packsquash.toml not found in ParallelUtils/resources directory. Will not use");
				}
			}


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
						if (!DISALLOWED_WORLDS.contains(f.getName())) {
							// This is now treated as a resource pack
							resourceMods.add(f);
						}
					}
				}
			}

			if (!squashOut.exists()) {
				Files.createDirectory(squashOut.toPath());
			}
			// Don't delete files so packsquash can cache

			if (!modOut.exists()) {
				Files.createDirectory(modOut.toPath());
			} else {
				purgeDirectory(modOut);
			}

			List<File> packs;

			if (packSquash != null) {

				List<File> packsTemp = generatePacks(modOut, base_zip, resourceMods);
				// Copy the base pack here to get squashed
				File base_temp = new File(modOut, base_zip.getName());
				Files.copy(base_zip.toPath(), base_temp.toPath());

				packsTemp.add(base_temp);

				// Squash all the files
				packs = squashFiles(packsTemp, resourcesDir, squashOut);
			} else {
				// Just put them right in the final output directory
				packs = generatePacks(squashOut, base_zip, resourceMods);

				// Copy the base pack to its final location
				File base_final = new File(modOut, base_zip.getName());
				Files.copy(base_zip.toPath(), base_final.toPath());
				packs.add(base_final);
			}

			for (File f : packs) {
				String trimmed_name = f.getName().replace(".zip", "");
				server.addResource(trimmed_name, f);
				resourceHashes.put(trimmed_name, createSha1(f));
			}

			handler = new ResourcePackHandle(puPlugin, this, warning_component);

		} catch (IOException e) {
			e.printStackTrace();
			ParallelUtils.log(Level.SEVERE, "IOException while loading ParallelResources! Quitting...");
			return;
		} catch (Exception e) {
			e.printStackTrace();
			ParallelUtils.log(Level.SEVERE, "Exception while loading ParallelResources! Quitting...");
			return;
		}

		serverThread = new Thread(server);
		serverThread.start();
	}

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(handler, puPlugin);
	}

	@Override
	public void onDisable() {
		handler.disable();
	}

	@Override
	public void onUnload() {
		serverThread.interrupt();
		serverThread = null;
		server.destruct();
		server = null;
		resourcesConfig = null;
		handler = null;
	}

	@Override
	public @NotNull String getName() {
		return "ParallelResources";
	}

	@NotNull
	public List<File> generatePacks(@NotNull File outDir, @NotNull File base_zip, @NotNull List<File> mods) throws Exception {
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
	public File generatePack(@NotNull File outDir, @NotNull File base, @NotNull File mod) throws Exception {

		// Copy base to a new file
		String outName = mod.getName() + ".zip";
		File outFile = new File(outDir, outName);
		Path outPath = outFile.toPath();
		Files.copy(base.toPath(), outPath, StandardCopyOption.REPLACE_EXISTING);

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
						Path targetdir = target.resolve("/" + mod_path.relativize(dir));
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
						Files.copy(file, target.resolve("/" + mod_path.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
						return CONTINUE;
					}
				});
			}


		} catch (IOException e) {
			e.printStackTrace();
			Files.delete(outPath);
			return null;
		}

		resourceHashes.put(mod.getName(), createSha1(outFile));

		return outPath.toFile();
	}

	@NotNull
	private List<File> squashFiles(@NotNull List<File> inFiles, @NotNull File resourcesDir, @NotNull File outDir) throws IOException {
		Path tempDir = Files.createTempDirectory(resourcesDir.toPath(), "resource-");

		ArrayList<File> squashed = new ArrayList<>();

		if (packSquash == null || packSquashConfig == null) return squashed;

		// Only read settings once
		byte[] settings;
		try (FileInputStream fis = new FileInputStream(packSquashConfig)) {
			settings = fis.readAllBytes();
		}

		for (File f : inFiles) {
			File out = squash(f, tempDir, outDir, settings);

			if (out == null) {
				ParallelUtils.log(Level.WARNING, "Unable to squash pack " + f.getName() + ". Using unsquashed version");
				out = new File(outDir, f.getName());
				Files.copy(f.toPath(), out.toPath());
			}
			squashed.add(out);
		}

		purgeDirectory(tempDir.toFile());
		Files.deleteIfExists(tempDir);

		return squashed;
	}

	/**
	 * Adapted from <a href="https://gist.github.com/AlexTMjugador/7049bdecfe94c893c457d78084e0dfd6">...</a>
	 * Note! This is in fact blocking and takes time! We just need to deal with it. It's technically possible to do this
	 * in a separate thread, but it's not worth doing right now. This will just increase server start time.
	 * @param infile The input resource pack to squash as a .zip file
	 * @param tempDir The temp directory used to store unzipped packs
	 * @param outDir The final output directory for squashed packs
	 * @param settingsStream The stream of packsquash.toml settings
	 * @return The squashed pack, or null if a problem occurred
	 */
	@Nullable
	private File squash(@NotNull File infile, @NotNull Path tempDir, @NotNull File outDir, @NotNull byte[] settingsStream) {
		if (packSquash == null || packSquashConfig == null) return null;

		final ProcessBuilder processBuilder = new ProcessBuilder(packSquash.getAbsolutePath());

		// Create directory to unzip to
		Path unzipPack;
		try {
			unzipPack = Files.createTempDirectory(tempDir, infile.getName());

			unzipFile(infile, unzipPack.toFile());
		} catch (IOException e) {
			ParallelUtils.log(Level.WARNING, "Unable to create temp directory for squashing file");
			return null;
		}

		// Set working directory to the unzipped file
		processBuilder.directory(unzipPack.toFile());

		// Redirect input, output, and errors, so we can handle it
		processBuilder.redirectInput(ProcessBuilder.Redirect.PIPE);
		processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);
		processBuilder.redirectErrorStream(true);

		final Process packSquashProcess;
		try {
			packSquashProcess = processBuilder.start();
		} catch (final Exception exc) {
			ParallelUtils.log(Level.WARNING, "- An error occurred while starting the PackSquash process:");
			exc.printStackTrace();

			return null;
		}

		File outFile = new File(outDir, infile.getName());

		try (OutputStream packSquashInputStream = packSquashProcess.getOutputStream()) {

			// Working directory
			packSquashInputStream.write(
					("pack_directory = '.'" + System.lineSeparator()).getBytes(StandardCharsets.UTF_8)
			);

			// Output file
			packSquashInputStream.write(
					("output_file_path = '" + outFile.getAbsolutePath() + "'" + System.lineSeparator()).getBytes(StandardCharsets.UTF_8)
			);

			// Write remaining settings
			packSquashInputStream.write(settingsStream);
		} catch (IOException e) {
			ParallelUtils.log(Level.WARNING, "- An error occurred while communicating with the PackSquash process:");
			e.printStackTrace();
			return null;
		}


		// Get output
		//ParallelUtils.log(Level.INFO, "- PackSquash process output:");
		try (InputStream packSquashOutputStream = new BufferedInputStream(packSquashProcess.getInputStream())) {
			int outputByte;
			//noinspection StatementWithEmptyBody
			while ((outputByte = packSquashOutputStream.read()) != -1) {
				//System.out.write(outputByte);
			}
		} catch (final IOException exc) {
			// The pipe is broken. This may happen if the process gets killed or finishes without
			// signaling EOF in its output stream.
			// Even if that happens, it could be treated as a normal EOF, but here we print
			// the exception anyway
			ParallelUtils.log(Level.WARNING, "- An error occurred while reading the PackSquash process output:");
			exc.printStackTrace();
			return null;
		}

		try {
			ParallelUtils.log(Level.INFO, "- PackSquash finished squashing " + infile.getName() + " with code " + packSquashProcess.waitFor());
		} catch (final InterruptedException exc) {
			ParallelUtils.log(Level.WARNING, "- Thread interrupted while waiting for PackSquash to finish!");
			exc.printStackTrace();
			return null;
		}

		return outFile;

	}

	/**
	 * Adapted from <a href="https://www.baeldung.com/java-compress-and-uncompress">...</a>
	 * @param sourceZip The source zip to unzip
	 * @param outputDir The output directory
	 * @throws IOException if there is an error while unzipping
	 */
	private void unzipFile(@NotNull File sourceZip, @NotNull File outputDir) throws IOException {
		if (!sourceZip.exists() || sourceZip.isDirectory()) return;
		if (!outputDir.exists()) {
			Files.createDirectory(outputDir.toPath());
		}
		if (!outputDir.isDirectory()) return;

		byte[] buffer = new byte[1024];
		ZipInputStream zis = new ZipInputStream(new FileInputStream(sourceZip));
		ZipEntry zipEntry = zis.getNextEntry();
		while (zipEntry != null) {
			while (zipEntry != null) {
				File newFile = createFile(outputDir, zipEntry);
				if (zipEntry.isDirectory()) {
					if (!newFile.isDirectory() && !newFile.mkdirs()) {
						throw new IOException("Failed to create directory " + newFile);
					}
				} else {
					// fix for Windows-created archives
					File parent = newFile.getParentFile();
					if (!parent.isDirectory() && !parent.mkdirs()) {
						throw new IOException("Failed to create directory " + parent);
					}

					// write file content
					FileOutputStream fos = new FileOutputStream(newFile);
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();
				}
				zipEntry = zis.getNextEntry();
			}
		}
		zis.closeEntry();
		zis.close();
	}

	private static File createFile(File destinationDir, ZipEntry zipEntry) throws IOException {
		File destFile = new File(destinationDir, zipEntry.getName());

		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}

		return destFile;
	}

	private byte[] createSha1(File file) throws Exception  {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		try (InputStream fis = new FileInputStream(file)) {
			int n = 0;
			byte[] buffer = new byte[8192];
			while (n != -1) {
				n = fis.read(buffer);
				if (n > 0) {
					digest.update(buffer, 0, n);
				}
			}
		}
		return digest.digest();
	}

	private void purgeDirectory(@NotNull File dir) {
		File[] files = dir.listFiles();

		if (files == null) return;

		for (File file: files) {
			if (file.isDirectory())
				purgeDirectory(file);
			if (!file.delete()) {
				ParallelUtils.log(Level.WARNING, "Failed to delete pack " + file.getName());
			}
		}
	}

	@NotNull
	public String getResourceUrl(@NotNull String world) {
		return base_url + world + ".zip";
	}

	@Nullable
	public byte[] getHash(@NotNull String world) {
		return resourceHashes.get(world);
	}

}
