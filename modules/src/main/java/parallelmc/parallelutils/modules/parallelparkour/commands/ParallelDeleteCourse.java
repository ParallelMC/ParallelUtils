package parallelmc.parallelutils.modules.parallelparkour.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.parallelparkour.ParallelParkour;

public class ParallelDeleteCourse implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (commandSender instanceof Player player) {
            if (!player.hasPermission("parallelutils.deletecourse")) {
                return true;
            }
            if (args.length == 0)
                return false;

            if (ParallelParkour.get().parkourNameExists(args[0])) {
                ParallelParkour.get().deleteParkour(args[0]);
                ParallelChat.sendParallelMessageTo(player, "Course " + args[0] + " has been deleted.");
                // TODO: automatically remove the pressure plates too?
            }
            else {
                ParallelChat.sendParallelMessageTo(player, "Course " + args[0] + " does not exist!");
            }
        }

        return true;
    }
}
