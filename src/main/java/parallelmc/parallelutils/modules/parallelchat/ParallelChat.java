package parallelmc.parallelutils.modules.parallelchat;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.parallelchat.events.ChatFormatterListener;
import parallelmc.parallelutils.modules.parallelchat.commands.ParallelFakeJoin;
import parallelmc.parallelutils.modules.parallelchat.commands.ParallelFakeLeave;

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
        puPlugin.getCommand("fakejoin").setExecutor(new ParallelFakeJoin());
        puPlugin.getCommand("fakeleave").setExecutor(new ParallelFakeLeave());
    }

    @Override
    public void onDisable() { }

    // probably a better place for this
    /**
     * Sends a chat message to a player
     * @param player The player to send the message to
     * @param message The message to send
     */
    public static void sendMessageTo(Player player, String message) {
        Component msg = Component.text("§3[§f§lP§3] §a" + message);
        player.sendMessage(msg);
    }
}
