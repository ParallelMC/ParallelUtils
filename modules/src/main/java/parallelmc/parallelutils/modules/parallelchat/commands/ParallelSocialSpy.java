package parallelmc.parallelutils.modules.parallelchat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.parallelchat.SocialSpyOptions;

import java.util.UUID;


public class ParallelSocialSpy implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player sender) {
            if (sender.hasPermission("parallelutils.socialspy")) {
                UUID uuid = sender.getUniqueId();
                if (ParallelChat.get().socialSpyUsers.containsKey(uuid)) {
                    SocialSpyOptions options = ParallelChat.get().socialSpyUsers.get(uuid);
                    if (options.isSocialSpy()) {
                        options.setSocialSpy(false);
                        ParallelChat.get().socialSpyUsers.put(uuid, options);
                        ParallelChat.sendParallelMessageTo(sender, "<red>Disabled Social Spy");
                    }
                    else {
                        options.setSocialSpy(true);
                        ParallelChat.get().socialSpyUsers.put(uuid, options);
                        ParallelChat.sendParallelMessageTo(sender, "Enabled Social Spy");
                    }
                }
                else {
                    ParallelChat.get().socialSpyUsers.put(uuid, new SocialSpyOptions(true, false, false));
                    ParallelChat.sendParallelMessageTo(sender, "Enabled Social Spy");
                }
            }
        }
        return true;
    }
}
