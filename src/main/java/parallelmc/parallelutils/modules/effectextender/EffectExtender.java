package parallelmc.parallelutils.modules.effectextender;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.effectextender.commands.ParallelEffectsCommand;

import java.util.logging.Level;

/**
 * A module to allow stacking of potions
 */
public class EffectExtender implements ParallelModule {

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

        if (plugin == null) {
            Parallelutils.log(Level.SEVERE, "Unable to enable EffectExtender. Plugin " + Constants.PLUGIN_NAME + " does not exist!");
            return;
        }

        Parallelutils puPlugin = (Parallelutils) plugin;

        manager.registerEvents(new EffectListener(), plugin);

        puPlugin.addCommand("effects", new ParallelEffectsCommand());

        Parallelutils.log(Parallelutils.LOG_LEVEL, "EntityPotionEffectEvent registered successfully.");
    }

    @Override
    public void onDisable() { }

}
