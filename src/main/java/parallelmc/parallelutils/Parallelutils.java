package parallelmc.parallelutils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftZombie;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import parallelmc.parallelutils.commands.Commands;
import parallelmc.parallelutils.custommobs.CustomTypes;
import parallelmc.parallelutils.custommobs.EntityWisp;
import parallelmc.parallelutils.custommobs.NMSWisp;

import java.util.UUID;

public final class Parallelutils extends JavaPlugin {

	String baseDataFolder = this.getDataFolder().getAbsolutePath();
	FileConfiguration config = this.getConfig();

	public static CustomTypes mobTypes;

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
		saveConfig();

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
}
