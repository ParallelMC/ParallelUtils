package parallelmc.parallelutils.modules.biometweaks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelClassLoader;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.biometweaks.biomes.PaleGarden;

import java.util.List;
import java.util.logging.Level;

public class BiomeTweaks extends ParallelModule {

    public BiomeTweaks(ParallelClassLoader classLoader, List<String> dependents) {
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
            ParallelUtils.log(Level.SEVERE, "Unable to enable BiomeTweaks. Plugin " + Constants.PLUGIN_NAME + " does not exist!");
            return;
        }

        ParallelUtils puPlugin = (ParallelUtils) plugin;

        if (!puPlugin.registerModule(this)) {
            ParallelUtils.log(Level.SEVERE, "Unable to register module BiomeTweaks! Module may already be registered. Quitting...");
            return;
        }

        // Initialize individual biomes
        PaleGarden paleGarden = new PaleGarden();
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onUnload() {

    }

    @Override
    public @NotNull String getName() {
        return "BiomeTweaks";
    }

}
