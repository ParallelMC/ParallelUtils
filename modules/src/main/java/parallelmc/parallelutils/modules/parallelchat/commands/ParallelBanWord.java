package parallelmc.parallelutils.modules.parallelchat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

public class ParallelBanWord implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length < 1) {
            return false;
        }
        if (commandSender instanceof Player) {
            if (!commandSender.hasPermission("parallelutils.banword")) {
                return true;
            }
        }
        // since we allow the same words with different capitalization we don't have to use equalsIgnoreCase here
        if (ParallelChat.get().bannedWords.stream().anyMatch(x -> x.equals(args[0]))) {
            commandSender.sendMessage("The banned words list already contains the word '" + args[0] + "'");
            return true;
        }
        ParallelChat.get().bannedWords.add(args[0]);
        commandSender.sendMessage("Added '" + args[0] + "' to the banned words list.");
        return true;
    }
}