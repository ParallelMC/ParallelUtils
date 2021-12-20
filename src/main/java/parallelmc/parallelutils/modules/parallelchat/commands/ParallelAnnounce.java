package parallelmc.parallelutils.modules.parallelchat.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

public class ParallelAnnounce implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length < 1) {
            return false;
        }
        if (commandSender instanceof Player sender) {
            if (!sender.hasPermission("parallelutils.announce")) {
                return true;
            }
        }
        String announce = ParallelChat.get().announceMsg;
        // allow console to run command
        Component msg = MiniMessage.get().parse(announce, Template.of("message", ParallelChat.getStringArg(args)));
        for (Player p : commandSender.getServer().getOnlinePlayers()) {
            p.sendMessage(msg);
        }
        return true;
    }
}