package parallelmc.parallelutils;

import net.minecraft.server.v1_16_R1.EntityPlayer;
import net.minecraft.server.v1_16_R1.EntityZombie;
import net.minecraft.server.v1_16_R1.PathfinderGoalMeleeAttack;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.loot.LootTable;
import org.bukkit.plugin.java.JavaPlugin;
import parallelmc.parallelutils.commands.Commands;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public final class Parallelutils extends JavaPlugin {

	String baseDataFolder = this.getDataFolder().getAbsolutePath();
	FileConfiguration config = this.getConfig();

	@Override
	public void onEnable() {
		// Plugin startup logic
		this.saveDefaultConfig();

		// Save the config
		config.options().copyDefaults(true);
		saveConfig();

		// More startup logic here



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
