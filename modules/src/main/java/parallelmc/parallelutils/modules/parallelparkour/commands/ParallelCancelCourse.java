package parallelmc.parallelutils.modules.parallelparkour.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.parallelparkour.ParallelParkour;
import parallelmc.parallelutils.modules.parallelparkour.ParkourLayout;

public class ParallelCancelCourse implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (commandSender instanceof Player player) {
            if (!player.hasPermission("parallelutils.cancelcourse"))
                return true;

            ParkourLayout layout = ParallelParkour.get().getParkourCreation(player);
            if (layout == null) {
                ParallelChat.sendParallelMessageTo(player, "You are not currently creating a course!");
                return true;
            }
            ParallelParkour.get().cancelParkourCreation(player);
            ParallelChat.sendParallelMessageTo(player, "Cancelled course creation. Make sure to break any pressure plates you may have placed!");
        }
        return true;
    }
}
