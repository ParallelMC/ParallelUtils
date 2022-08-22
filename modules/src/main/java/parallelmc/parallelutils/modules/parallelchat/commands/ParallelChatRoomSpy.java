package parallelmc.parallelutils.modules.parallelchat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.parallelchat.SocialSpyOptions;

import java.util.UUID;

public class ParallelChatRoomSpy implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player sender) {
            if (sender.hasPermission("parallelutils.chatrooms.chatroomspy")) {
                UUID uuid = sender.getUniqueId();
                if (ParallelChat.get().socialSpyUsers.containsKey(uuid)) {
                    SocialSpyOptions options = ParallelChat.get().socialSpyUsers.get(uuid);
                    if (options.isChatRoomSpy()) {
                        options.setChatRoomSpy(false);
                        ParallelChat.get().socialSpyUsers.put(uuid, options);
                        ParallelChat.sendParallelMessageTo(sender, "<red>Disabled ChatRoom Spy");
                    }
                    else {
                        options.setChatRoomSpy(true);
                        ParallelChat.get().socialSpyUsers.put(uuid, options);
                        ParallelChat.sendParallelMessageTo(sender, "Enabled ChatRoom Spy");
                    }
                }
                else {
                    ParallelChat.get().socialSpyUsers.put(uuid, new SocialSpyOptions(false,false, true));
                    ParallelChat.sendParallelMessageTo(sender, "Enabled ChatRoom Spy");
                }
            }
        }
        return true;
    }
}
