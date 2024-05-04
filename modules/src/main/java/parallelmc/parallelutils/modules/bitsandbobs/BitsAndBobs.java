package parallelmc.parallelutils.modules.bitsandbobs;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelClassLoader;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.bitsandbobs.minimodules.*;
import parallelmc.parallelutils.modules.bitsandbobs.minimodules.togglepvp.OnPvp;
import parallelmc.parallelutils.modules.bitsandbobs.minimodules.togglepvp.TogglePvpCommand;
import parallelmc.parallelutils.modules.bitsandbobs.minimodules.togglepvp.TogglePvpManager;

import java.util.List;
import java.util.logging.Level;

public class BitsAndBobs extends ParallelModule {

    private TogglePvpManager pvpManager;

    public BitsAndBobs(ParallelClassLoader classLoader, List<String> dependents) {
        super(classLoader, dependents);
    }


    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

        if (plugin == null) {
            ParallelUtils.log(Level.SEVERE, "Unable to enable BitsAndBobs. Plugin " + Constants.PLUGIN_NAME + " does not exist!");
            return;
        }

        ParallelUtils puPlugin = (ParallelUtils) plugin;

        if (!puPlugin.registerModule(this)) {
            ParallelUtils.log(Level.SEVERE, "Unable to register module BitsAndBobs! Module may already be registered. Quitting...");
            return;
        }

        this.pvpManager = new TogglePvpManager(puPlugin);
        pvpManager.init();

        FileConfiguration config = puPlugin.getConfig();

        puPlugin.getCommand("togglepvp").setExecutor(new TogglePvpCommand());

        manager.registerEvents(new DoorKnocker(), plugin);
        manager.registerEvents(new SpecialItems(), plugin);
        manager.registerEvents(new SpeedyMinecarts(), plugin);
        manager.registerEvents(new OnPvp(), plugin);
        manager.registerEvents(new ShardLotto(), plugin);
        manager.registerEvents(new ChickenFeatherDrops(), plugin);

        if (config.getBoolean("disable-ender-chests", false)) {
            manager.registerEvents(new DisableEnderChest(), plugin);
        }

        if (config.getBoolean("prevent-spawner-mining", false)) {
            manager.registerEvents(new PreventSpawnerMining(), plugin);
        }

        if (config.getBoolean("enable-ziprails", true)) {
            manager.registerEvents(new Ziprails(), plugin);
        }
      
        if (config.getBoolean("enable-calling-bell", true)) {
            manager.registerEvents(new CallingBell(), plugin);
        }

        if (config.getBoolean("enable-sweethearts", true)) {
            manager.registerEvents(new Sweethearts(), plugin);
        }
    }

    @Override
    public void onDisable() {
        this.pvpManager.unload();
    }

    @Override
    public void onUnload() {

    }

    @NotNull
    @Override
    public String getName() {
        return "BitsAndBobs";
    }
}
