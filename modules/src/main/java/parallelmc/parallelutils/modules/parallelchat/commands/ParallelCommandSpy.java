package parallelmc.parallelutils.modules.parallelchat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.parallelchat.SocialSpyOptions;

import java.util.UUID;

public class ParallelCommandSpy implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player sender) {
            if (sender.hasPermission("parallelutils.commandspy")) {
                UUID uuid = sender.getUniqueId();
                if (ParallelChat.get().socialSpyUsers.containsKey(uuid)) {
                    SocialSpyOptions options = ParallelChat.get().socialSpyUsers.get(uuid);
                    if (options.isCmdSpy()) {
                        options.setCmdSpy(false);
                        ParallelChat.get().socialSpyUsers.put(uuid, options);
                        ParallelChat.sendParallelMessageTo(sender, "<red>Disabled Command Spy");
                    }
                    else {
                        options.setCmdSpy(true);
                        ParallelChat.get().socialSpyUsers.put(uuid, options);
                        ParallelChat.sendParallelMessageTo(sender, "Enabled Command Spy");
                    }
                }
                else {
                    ParallelChat.get().socialSpyUsers.put(uuid, new SocialSpyOptions(false, true));
                    ParallelChat.sendParallelMessageTo(sender, "Enabled Command Spy");
                }
            }
        }
        return true;
    }
}
