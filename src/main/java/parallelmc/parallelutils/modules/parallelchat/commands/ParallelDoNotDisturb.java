package parallelmc.parallelutils.modules.parallelchat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import java.util.UUID;

public class ParallelDoNotDisturb implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player sender) {
            UUID uuid = sender.getUniqueId();
            if (ParallelChat.dndPlayers.contains(uuid)) {
                ParallelChat.dndPlayers.remove(uuid);
                ParallelChat.sendParallelMessageTo(sender, "You are no longer in Do Not Disturb mode.");
            }
            else {
                ParallelChat.dndPlayers.add(uuid);
                ParallelChat.sendParallelMessageTo(sender, "You are now in Do Not Disturb mode.");
            }
        }
        return true;
    }
}
