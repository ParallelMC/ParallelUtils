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

public class ParallelMessage implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player sender) {
            if (args.length < 2)
                return false;
            Player recipient = sender.getServer().getPlayer(args[0]);
            if (recipient == null || !sender.canSee(recipient)) {
                ParallelChat.sendParallelMessageTo(sender, "Cannot find player " + args[0]);
                return true;
            }
            // tee hee
            args[0] = "";
            String msg = ParallelChat.getStringArg(args);
            Component msgTo = MiniMessage.get().parse("<white><bold>[<yellow>You</bold><dark_aqua> -> <yellow>" + recipient.getName() + "<white><bold>]</bold><aqua>" + msg);
            Component msgFrom = MiniMessage.get().parse("<white><bold>[</bold><yellow>" + sender.getName() + "<dark_aqua> -> <yellow><bold>You<white>]</bold><aqua>" + msg);
            Component socialSpy = MiniMessage.get().parse("<yellow>[<aqua>Social-Spy<yellow>] <dark_gray>" + sender.getName() + " -> " + recipient.getName() + ":<aqua>" + msg);
            sender.sendMessage(msgTo);
            recipient.sendMessage(msgFrom);

            UUID senderUUID = sender.getUniqueId();
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
            ParallelChat.get().playerLastMessaged.put(sender.getUniqueId(), recipient.getUniqueId());
            ParallelChat.get().playerLastMessaged.put(recipient.getUniqueId(), sender.getUniqueId());
        }
        return true;
    }
}
