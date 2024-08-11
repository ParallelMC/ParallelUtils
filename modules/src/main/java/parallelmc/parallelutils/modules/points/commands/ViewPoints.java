package parallelmc.parallelutils.modules.points.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.points.Points;

public class ViewPoints implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (commandSender instanceof Player player) {
            int points = Points.get().getPlayerPoints(player);
            ParallelChat.sendParallelMessageTo(player, "You currently have " + points + " advancement points!");
        }
        return true;
    }
}
