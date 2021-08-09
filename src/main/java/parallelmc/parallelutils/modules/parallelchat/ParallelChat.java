package parallelmc.parallelutils.modules.parallelchat;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.parallelchat.events.ChatFormatterListener;
import parallelmc.parallelutils.modules.parallelchat.events.OnChatMessage;

import java.util.logging.Level;

public class ParallelChat implements ParallelModule {

    private final ChatOptions options;

    public ParallelChat(ChatOptions options) {
        this.options = options;
        Parallelutils.log(Level.INFO, options.getFormat());
    }

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

        if (!puPlugin.registerModule("ParallelChat", this)) {
            Parallelutils.log(Level.SEVERE, "Unable to register module ParallelChat! " +
                    "Module may already be registered. Quitting...");
            return;
        }

        //manager.registerEvents(new OnChatMessage(), puPlugin);
        manager.registerEvents(new ChatFormatterListener(options), puPlugin);
    }

    @Override
    public void onDisable() { }
}
