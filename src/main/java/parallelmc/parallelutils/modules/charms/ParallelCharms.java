package parallelmc.parallelutils.modules.charms;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.commands.ParallelCommand;
import parallelmc.parallelutils.modules.charms.commands.ApplyCharm;
import parallelmc.parallelutils.modules.charms.commands.RemoveCharm;
import parallelmc.parallelutils.modules.charms.data.*;
import parallelmc.parallelutils.modules.charms.events.PlayerKillListener;
import parallelmc.parallelutils.modules.charms.handlers.CharmKillMessageHandler;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.handlers.ICharmHandler;
import parallelmc.parallelutils.modules.charms.helper.EncapsulatedType;
import parallelmc.parallelutils.modules.charms.helper.Types;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Stream;

public class ParallelCharms implements ParallelModule {

	private final HashMap<HandlerType, ICharmHandler<? extends Event>> handlers;

	private final ArrayList<CharmOptions> charmOptions;

	public ParallelCharms() {
		handlers = new HashMap<>();
		charmOptions = new ArrayList<>();
	}

	@Override
	public void onEnable() {
		PluginManager manager = Bukkit.getPluginManager();
		Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

		if (plugin == null) {
			Parallelutils.log(Level.SEVERE, "Unable to enable ParallelCharms. Plugin " + Constants.PLUGIN_NAME
					+ " does not exist!");
			return;
		}

		Parallelutils puPlugin = (Parallelutils) plugin;

		if (!puPlugin.registerModule("ParallelCharms", this)) {
			Parallelutils.log(Level.SEVERE, "Unable to register module ParallelCharms! " +
					"Module may already be registered. Quitting...");
			return;
		}

		// Register handlers
		if (!registerHandler(new CharmKillMessageHandler())) { Parallelutils.log(Level.WARNING, "Could not register event!"); }

		// Register events
		manager.registerEvents(new PlayerKillListener(this), puPlugin);


		// Read Options files
		try {
			Path charmsFolder = Path.of(puPlugin.getDataFolder() + "/charms");
			if (!Files.exists(charmsFolder)) {
				Parallelutils.log(Level.WARNING, "Charms folder does not exist. Creating...");
				Files.createDirectory(charmsFolder);
			}

			Path optionsFile = Path.of(puPlugin.getDataFolder() + "/charms/options.yml");
			if (!Files.exists(optionsFile)) {
				Parallelutils.log(Level.WARNING, "Charms Options file does not exist. Creating...");
				Files.createFile(optionsFile);
			}

			FileConfiguration optionsConfig = new YamlConfiguration();
			optionsConfig.load(optionsFile.toFile());

			Map<String, Object> vals = optionsConfig.getValues(false);

			for (String s : vals.keySet()) {
				Object o = vals.get(s);
				if (o instanceof ConfigurationSection section) {
					try {
						// This is a single charm option
						String uuidStr = section.getString("uuid");
						if (uuidStr == null) {
							Parallelutils.log(Level.WARNING, "Invalid Charm Option for option: " + s);
							continue;
						}
						UUID uuid = UUID.fromString(uuidStr);

						List<String> matStrList = section.getStringList("allowed-materials");
						Material[] matsList = matStrList.stream().map(Material::valueOf).toArray(Material[]::new);

						String[] allowedPlayers = section.getStringList("allowed-players").toArray(String[]::new);
						String[] allowedPermissions = section.getStringList("allowed-permissions").toArray(String[]::new);

						Integer customModelData = section.getInt("custom-model-data");
						if (customModelData == 0) {
							customModelData = null;
						}

						HashMap<HandlerType, IEffectSettings> effects = new HashMap<>();
						ConfigurationSection effectsSection = section.getConfigurationSection("effects");
						if (effectsSection != null) {
							Map<String, Object> effectPairs = effectsSection.getValues(false);

							for (String handlerStr : effectPairs.keySet()) {
								Object effectSettingsObj = effectPairs.get(handlerStr);
								if (effectSettingsObj instanceof ConfigurationSection settingsSection) {
									Map<String, Object> settingPairs = settingsSection.getValues(false);

									HashMap<String, EncapsulatedType> settings = new HashMap<>();

									for (String settingName : settingPairs.keySet()) {
										Object settingVal = settingPairs.get(settingName);
										if (settingVal instanceof ConfigurationSection settingValSec) {
											String typeStr = settingValSec.getString("type");
											if (typeStr != null) {
												Types type = Types.valueOf(typeStr);
												switch (type) {
													case BYTE, INT -> settings.put(settingName,
															new EncapsulatedType(type, settingValSec.getInt("val")));
													case LONG -> settings.put(settingName,
															new EncapsulatedType(type, settingValSec.getLong("val")));
													case DOUBLE -> settings.put(settingName,
															new EncapsulatedType(type, settingValSec.getDouble("val")));
													case STRING -> settings.put(settingName,
															new EncapsulatedType(type, settingValSec.getString("val")));
												}
											}
										}
									}

									GenericEffectSettings effectSettings = new GenericEffectSettings(settings);
									HandlerType handlerType = HandlerType.valueOf(handlerStr);
									effects.put(handlerType, effectSettings);
								}
							}
						}

						CharmOptions charmOptions = new CharmOptions(uuid, matsList, allowedPlayers, allowedPermissions,
								effects, customModelData);
						this.charmOptions.add(charmOptions);

					} catch (IllegalArgumentException e) {
						Parallelutils.log(Level.WARNING, "Cannot parse charm settings!");
						e.printStackTrace();
					}
				}
			}


		} catch (IOException e) {
			Parallelutils.log(Level.WARNING, "Unable to load charm options!");
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			Parallelutils.log(Level.WARNING, "Invalid charm options configuration!");
			e.printStackTrace();
		}


		// TODO: Remove before release
		HashMap<HandlerType, IEffectSettings> effects = new HashMap<>();

		effects.put(HandlerType.MESSAGE_KILL, new BasicMessageEffectSettings("<rainbow>Kill Message Succeeded"));

		CharmOptions testOptions = new CharmOptions(UUID.randomUUID(), null, null,
				null, effects, 123456);
		Charm testCharm = new Charm(testOptions);

		puPlugin.addCommand("applyCharm", new ApplyCharm(testCharm));
		puPlugin.addCommand("removeCharm", new RemoveCharm(testCharm));
	}

	@Override
	public void onDisable() {
	}

	/**
	 * Registers a charm handler
	 * @param handler The handler to register
	 * @return True when the handler type is new, false otherwise
	 */
	public boolean registerHandler(ICharmHandler<?> handler) {
		HandlerType type = handler.getHandlerType();

		if (handlers.containsKey(type)) {
			return false;
		}

		handlers.put(type, handler);
		return true;
	}

	/**
	 * Gets the handler associated with the type or null
	 * @return The handler or null
	 */
	@Nullable
	public <T extends Event> ICharmHandler<T> getHandler(HandlerType type, Class<T> event) {
		ICharmHandler<? extends Event> handler = handlers.get(type);
		try {
			if (handler.getEventType().equals(event)) {
				return (ICharmHandler<T>) handler;
			}
		} catch (Exception e) {
			Parallelutils.log(Level.SEVERE, "UNABLE TO CAST HANDLER!!!");
			e.printStackTrace();
		}
		return null;
	}
}