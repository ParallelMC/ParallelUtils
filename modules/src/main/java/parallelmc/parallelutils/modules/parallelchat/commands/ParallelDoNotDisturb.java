package parallelmc.parallelutils.modules.parallelchat.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

public class ParallelDoNotDisturb implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player sender) {
            if (ParallelChat.dndPlayers.containsKey(sender)) {
                sender.playerListName(ParallelChat.dndPlayers.get(sender));
                ParallelChat.dndPlayers.remove(sender);
                ParallelChat.sendParallelMessageTo(sender, "You are no longer in Do Not Disturb mode.");
            }
            else {
                // save their current tab name
                ParallelChat.dndPlayers.put(sender, sender.playerListName());
                sender.playerListName(sender.playerListName().append(Component.text(" DND", NamedTextColor.RED)));
                ParallelChat.sendParallelMessageTo(sender, "You are now in Do Not Disturb mode.");
            }
        }
        return true;
    }

}
