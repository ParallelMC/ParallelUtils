package parallelmc.parallelutils.modules.parallelchat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import java.util.logging.Level;

public class ParallelMuteChat implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player sender) {
            if (!sender.hasPermission("parallelutils.mutechat")) {
                return true;
            }
        }
        if (ParallelChat.get().isChatDisabled) {
            for (Player p : commandSender.getServer().getOnlinePlayers()) {
                ParallelChat.sendParallelMessageTo(p, "Chat has been enabled by " + commandSender.getName());
            }
            ParallelUtils.log(Level.WARNING, "Chat unmuted by " + commandSender.getName());
            ParallelChat.get().setChatDisabled(false);
        }
        else {
            for (Player p : commandSender.getServer().getOnlinePlayers()) {
                ParallelChat.sendParallelMessageTo(p, "<red>Chat has been disabled by " + commandSender.getName());
            }
            ParallelUtils.log(Level.WARNING, "Chat muted by " + commandSender.getName());
            ParallelChat.get().setChatDisabled(true);
        }
        return true;
    }
}
