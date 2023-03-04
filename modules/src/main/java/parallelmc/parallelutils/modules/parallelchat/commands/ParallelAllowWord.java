package parallelmc.parallelutils.modules.parallelchat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

public class ParallelAllowWord implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length < 1) {
            return false;
        }
        if (commandSender instanceof Player) {
            if (!commandSender.hasPermission("parallelutils.allowword")) {
                return true;
            }
        }
        // since we allow the same words with different capitalization we don't have to use equalsIgnoreCase here
        if (ParallelChat.get().bannedWords.stream().noneMatch(x -> x.equals(args[0]))) {
            commandSender.sendMessage("The word '" + args[0] + "' is already allowed.");
            return true;
        }
        ParallelChat.get().bannedWords.remove(args[0]);
        commandSender.sendMessage("Removed '" + args[0] + "' from the banned words list.");
        return true;
    }
}