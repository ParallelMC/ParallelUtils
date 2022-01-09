package parallelmc.parallelutils.modules.parallelchat.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import java.util.UUID;

public class ParallelStaffChat implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player sender) {
            if (sender.hasPermission("parallelutils.staffchat")) {
                UUID uuid = sender.getUniqueId();
                if (args.length == 0) {
                    if (ParallelChat.get().getStaffChat().contains(uuid)) {
                        ParallelChat.get().removeFromStaffChat(sender);
                    }
                    else {
                        if (ParallelChat.get().getTeamChat().contains(uuid)) {
                            ParallelChat.get().removeFromTeamChat(sender);
                        }
                        ParallelChat.get().addToStaffChat(sender);
                    }
                }
            }
            else {
                return true;
            }
        }
        String msg = ParallelChat.getStringArg(args);
        ParallelChat.sendMessageToStaffChat(commandSender, Component.text(msg));
        return true;
    }

}