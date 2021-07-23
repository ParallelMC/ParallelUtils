package parallelmc.parallelutils.modules.parallelchat;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.Parallelutils;

import java.util.logging.Level;

public class ParallelChat implements ParallelModule {
    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

        if (plugin == null) {
            Parallelutils.log(Level.SEVERE, "Unable to enable ParallelChat. Plugin " + Constants.PLUGIN_NAME
                    + " does not exist!");
            return;
        }

        Parallelutils puPlugin = (Parallelutils) plugin;

        if (!puPlugin.registerModule("ItemChat", this)) {
            Parallelutils.log(Level.SEVERE, "Unable to register module ParallelChat! " +
                    "Module may already be registered. Quitting...");
            return;
        }

    }

    @Override
    public void onDisable() { }
}
