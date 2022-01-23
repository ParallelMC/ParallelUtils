package parallelmc.parallelutils.modules.parallelchat.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import java.util.UUID;

public class ParallelReply implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player sender) {
            if (args.length < 1) {
                return false;
            }
            UUID lastMessaged = ParallelChat.get().playerLastMessaged.get(sender.getUniqueId());
            if (lastMessaged == null) {
                ParallelChat.sendParallelMessageTo(sender, "You have not messaged anyone recently! No one to reply to.");
                return true;
            }
            Player recipient = sender.getServer().getPlayer(lastMessaged);
            String msg = ParallelChat.getStringArg(args);
            Component msgTo = MiniMessage.miniMessage().deserialize("<white><bold>[<yellow>You</bold><dark_aqua> -> <yellow>" + recipient.getName() + "<white><bold>]</bold> <aqua>" + msg);
            Component msgFrom = MiniMessage.miniMessage().deserialize("<white><bold>[</bold><yellow>" + sender.getName() + "<dark_aqua> -> <yellow><bold>You<white>]</bold> <aqua>" + msg);
            Component socialSpy = MiniMessage.miniMessage().deserialize("<yellow>[<aqua>Social-Spy<yellow>] <dark_gray>" + sender.getName() + " -> " + recipient.getName() + ": <aqua>" + msg);

            sender.sendMessage(msgTo);
            recipient.sendMessage(msgFrom);

            UUID senderUUID = sender.getUniqueId();
            if (!sender.hasPermission("parallelutils.bypass.socialspy")) {
                // Social Spy
                ParallelChat.get().socialSpyUsers.forEach((u, o) -> {
                    if (u.equals(senderUUID)) return;
                    if (o.isSocialSpy()) {
                        // this kinda sucks but not much can be done
                        Player spyUser = sender.getServer().getPlayer(u);
                        if (spyUser != null) {
                            spyUser.sendMessage(socialSpy);
                        }
                    }
                });
            }

            ParallelChat.get().playerLastMessaged.put(senderUUID, recipient.getUniqueId());
            ParallelChat.get().playerLastMessaged.put(recipient.getUniqueId(), senderUUID);
        }
        return true;
    }
}
