package parallelmc.parallelutils.modules.parallelchat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import java.util.logging.Level;

public class ParallelReloadEmojis implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender.hasPermission("parallelutils.reloademojis")) {
            if (ParallelChat.get().emojiManager.loadEmojis()) {
                int emojis = ParallelChat.get().emojiManager.getEmojis().size();
                if (commandSender instanceof Player player) {
                    ParallelChat.sendParallelMessageTo(player, "Loaded " + emojis + " emojis.");
                }
                else {
                    ParallelUtils.log(Level.WARNING, "Loaded " + emojis + " emojis.");
                }
            }
            else {
                if (commandSender instanceof Player player) {
                    ParallelChat.sendParallelMessageTo(player, "Failed to load emojis! Check the console for an error.");
                }
            }
        }
        return true;
    }
}
