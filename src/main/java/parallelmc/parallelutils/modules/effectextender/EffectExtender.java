package parallelmc.parallelutils.modules.effectextender;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.Parallelutils;

import java.util.logging.Level;

public class EffectExtender implements ParallelModule {

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

        if (plugin == null) {
            Parallelutils.log(Level.SEVERE, "Unable to enable EffectExtender. Plugin " + Constants.PLUGIN_NAME + " does not exist!");
            return;
        }

        manager.registerEvents(new EffectListener(), plugin);
        Parallelutils.log(Parallelutils.LOG_LEVEL, "EntityPotionEffectEvent registered successfully.");
    }

    @Override
    public void onDisable() { }

}
