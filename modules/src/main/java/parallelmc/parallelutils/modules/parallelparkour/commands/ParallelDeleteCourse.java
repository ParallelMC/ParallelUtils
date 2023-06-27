package parallelmc.parallelutils.modules.parallelparkour.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.parallelparkour.ParallelParkour;

import java.util.ArrayList;
import java.util.List;

public class ParallelDeleteCourse implements CommandExecutor, TabCompleter {
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

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return ParallelParkour.get().getAllLayoutNames();
        }
        return new ArrayList<>();
    }
}
