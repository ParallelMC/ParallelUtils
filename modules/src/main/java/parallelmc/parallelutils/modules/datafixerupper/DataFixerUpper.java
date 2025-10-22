package parallelmc.parallelutils.modules.datafixerupper;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelClassLoader;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.bitsandbobs.commands.Hat;
import parallelmc.parallelutils.modules.bitsandbobs.minimodules.*;
import parallelmc.parallelutils.modules.bitsandbobs.minimodules.togglepvp.OnPvp;
import parallelmc.parallelutils.modules.bitsandbobs.minimodules.togglepvp.TogglePvpCommand;
import parallelmc.parallelutils.modules.bitsandbobs.minimodules.togglepvp.TogglePvpManager;
import parallelmc.parallelutils.util.BukkitTools;

import java.util.List;
import java.util.logging.Level;

public class DataFixerUpper extends ParallelModule {

    public DataFixerUpper(ParallelClassLoader classLoader, List<String> dependents) {
        super(classLoader, dependents);
    }


    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        // PluginManager manager = Bukkit.getPluginManager();
        // Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);
        Plugin plugin = BukkitTools.getPlugin();
        if (plugin == null) {
            ParallelUtils.log(Level.SEVERE, "Unable to enable DataFixerUpper. Plugin " + Constants.PLUGIN_NAME + " does not exist!");
            return;
        }

        ParallelUtils puPlugin = (ParallelUtils) plugin;
        if (!puPlugin.registerModule(this)) {
            ParallelUtils.log(Level.SEVERE, "Unable to register module DataFixerUpper! Module may already be registered. Quitting...");
            return;
        }
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onUnload() {

    }

    @NotNull
    @Override
    public String getName() {
        return "DataFixerUpper";
    }
}
