package parallelmc.parallelutils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import parallelmc.parallelutils.commands.Commands;
import parallelmc.parallelutils.custommobs.CustomTypes;
import parallelmc.parallelutils.custommobs.EntityWisp;

public final class Parallelutils extends JavaPlugin {

	String baseDataFolder = this.getDataFolder().getAbsolutePath();
	FileConfiguration config = this.getConfig();

	public static CustomTypes mobTypes;

	@Override
	public void onEnable() {
		mobTypes = new CustomTypes();
		// Plugin startup logic
		this.saveDefaultConfig();

		// Save the config
		config.options().copyDefaults(true);
		saveConfig();

		// More startup logic here
		short id = 1234;
		try {
			mobTypes.addEntityType("wisp", EntityWisp.class, id);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(mobTypes.getType("wisp"));

		// Setup commands
		Commands commands = new Commands(this);

		getCommand("parallelutils").setExecutor(commands);
		getCommand("pu").setExecutor(commands);
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic
	}
}
