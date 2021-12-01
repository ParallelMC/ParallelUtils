package parallelmc.parallelutils.modules.parallelchat.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;


public class ParallelFakeJoin implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player sender) {
            if (sender.hasPermission("parallelutils.fakelogin")) {
                String username;
                if (args.length == 0) {
                    username = sender.getName();
                }
                else {
                    if (sender.getServer().getPlayer(args[0]) == null) {
                        ParallelChat.sendParallelMessageTo(sender, String.format("Could not find player %s", args[0]));
                        return true;
                    }
                    username = args[0];
                }
                Component text = Component.text(String.format("%s joined the game", username), NamedTextColor.YELLOW);
                sender.getServer().getOnlinePlayers().forEach((player) -> {
                    player.sendMessage(text);
                });
            }
        }
        return true;
    }
}
