package parallelmc.parallelutils.modules.charms;

import dev.esophose.playerparticles.api.PlayerParticlesAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.charms.commands.ApplyCharm;
import parallelmc.parallelutils.modules.charms.commands.GiveCharm;
import parallelmc.parallelutils.modules.charms.commands.ParticleTest;
import parallelmc.parallelutils.modules.charms.commands.RemoveCharm;
import parallelmc.parallelutils.modules.charms.data.*;
import parallelmc.parallelutils.modules.charms.data.impl.GenericEffectSettings;
import parallelmc.parallelutils.modules.charms.handlers.impl.*;
import parallelmc.parallelutils.modules.charms.listeners.*;
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

public class ParallelCharms implements ParallelModule {

	private final HashMap<HandlerType, ICharmHandler<? extends Event>> handlers;

	private final HashMap<String, CharmOptions> charmOptions;

	// Key is player UUID, value is list of Charms associated with the player
	private final HashMap<UUID, ArrayList<Charm>> appliedCharms;

	public ParallelCharms() {
		handlers = new HashMap<>();
		charmOptions = new HashMap<>();
		appliedCharms = new HashMap<>();
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

		PlayerParticlesAPI ppAPI = null;

		if (Bukkit.getPluginManager().isPluginEnabled("PlayerParticles")) {
			//PlayerParticles pp = (PlayerParticles) Bukkit.getPluginManager().getPlugin("PlayerParticles");

			ppAPI = PlayerParticlesAPI.getInstance();
		}

		// Register handlers
		if (!registerHandler(new CharmKillMessageHandler())) { Parallelutils.log(Level.WARNING, "Could not register MESSAGE_KILL"); }
		if (!registerHandler(new CharmStyleNameHandler())) { Parallelutils.log(Level.WARNING, "Could not register STYLE_NAME"); }
		if (!registerHandler(new CharmParticleHandler())) { Parallelutils.log(Level.WARNING, "Could not register PARTICLE"); }
		if (!registerHandler(new CharmLoreHandler())) { Parallelutils.log(Level.WARNING, "Could not register LORE"); }
		if (!registerHandler(new CharmTestRunnableHandler())) { Parallelutils.log(Level.WARNING, "Could not register TEST_RUNNABLE"); }
		if (!registerHandler(new CharmTestEventHandler())) { Parallelutils.log(Level.WARNING, "Could not register TEST_EVENT");}
		if (!registerHandler(new CharmTestApplyHandler())) { Parallelutils.log(Level.WARNING, "Could not register TEST_APPLY"); }
		if (ppAPI == null || !registerHandler(new CharmPlayerParticleHandler(puPlugin, this, ppAPI))) { Parallelutils.log(Level.WARNING, "Could not register PLAYER_PARTICLE");}

		// Register events
		manager.registerEvents(new PlayerJoinContainerListenerOverwrite(), puPlugin);
		manager.registerEvents(new PlayerKillListener(this), puPlugin);
		manager.registerEvents(new PlayerJoinListener(puPlugin, this), puPlugin);
		manager.registerEvents(new PlayerLeaveListener(puPlugin, this), puPlugin);
		manager.registerEvents(new PlayerSlotChangedListener(puPlugin, this), puPlugin);
		manager.registerEvents(new AnvilApplyCharmListener(puPlugin, this), puPlugin);
		manager.registerEvents(new PlayerHeldItemListener(puPlugin, this), puPlugin);


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

						String name = section.getString("name");
						if (name == null || name.equals("")) {
							Parallelutils.log(Level.WARNING, "Invalid Charm Option for option: " + s);
							Parallelutils.log(Level.WARNING, "Must have name");
							continue;
						}

						List<String> matStrList = section.getStringList("allowed-materials");
						Material[] matsList = matStrList.stream().map(Material::getMaterial).toArray(Material[]::new);

						String[] allowedPlayers = section.getStringList("allowed-players").toArray(String[]::new);
						String[] allowedPermissions = section.getStringList("allowed-permissions").toArray(String[]::new);

						Integer customModelData = section.getInt("custom-model-data");
						if (customModelData == 0) {
							customModelData = null;
						}

						Integer applicatorModelData = section.getInt("applicator-model-data");
						if (applicatorModelData == 0) {
							applicatorModelData = null;
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

									HandlerType handlerType = HandlerType.valueOf(handlerStr);

									IEffectSettings effectSettings = new SettingsFactory(handlerType).getSettings(settings);
									effects.put(handlerType, effectSettings);
								}
							}
						}

						CharmOptions charmOptions = new CharmOptions(uuid, name, matsList, allowedPlayers, allowedPermissions,
								effects, customModelData, applicatorModelData);
						this.charmOptions.put(name, charmOptions);

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

		puPlugin.addCommand("giveCharm", new GiveCharm(this, charmOptions));


		/* Stuff from testing
		if (charmOptions.size() > 1) {
			Charm testCharm = new Charm(this, charmOptions.get(1));
			Parallelutils.log(Level.INFO, charmOptions.get(1).toString());
			puPlugin.addCommand("applyCharm", new ApplyCharm(testCharm));
			puPlugin.addCommand("removeCharm", new RemoveCharm(testCharm));
		}
		 */
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
	public <T extends Event> ICharmHandler<T> getHandler(HandlerType type, @NotNull Class<T> event) {
		ICharmHandler<? extends Event> handler = handlers.get(type);
		if (handler == null) {
			Parallelutils.log(Level.WARNING, "Handler of type " + type.name() + " does not exist!!!");
		}
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


	public void addCharm(Player player, Charm charm) {
		UUID uuid = player.getUniqueId();

		ArrayList<Charm> charms = appliedCharms.get(uuid);

		if (charms == null) {
			charms = new ArrayList<>();
			charms.add(charm);
			appliedCharms.put(uuid, charms);
		} else {
			charms.add(charm);
		}
	}

	public Charm removeCharm(@NotNull Player player, @NotNull Charm charm) {
		ArrayList<Charm> charms = appliedCharms.get(player.getUniqueId());

		if (charms != null) {
			Charm removeCharm = null;
			for (Charm c : charms) {
				if (c.getUUID().equals(charm.getUUID())) {
					removeCharm = c;
					break;
				}
			}

			if (removeCharm != null) {
				charms.remove(removeCharm);
				return removeCharm;
			}
		}
		return null;
	}

	// NOTE: Yes, this looks weird. Yes, it's correct
	// No idea why I programmed it like this but the implementation should work so whatever
	public ArrayList<Charm> removeAllCharms(@NotNull Player player) {
		return appliedCharms.remove(player.getUniqueId());
	}
}
