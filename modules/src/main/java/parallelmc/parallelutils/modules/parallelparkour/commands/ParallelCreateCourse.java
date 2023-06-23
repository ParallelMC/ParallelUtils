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
import parallelmc.parallelutils.modules.parallelparkour.ParkourLayout;

import java.util.ArrayList;
import java.util.List;


public class ParallelCreateCourse implements CommandExecutor, TabCompleter {
    // TODO: make start pressure plate restart the run
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (commandSender instanceof Player player) {
            if (!player.hasPermission("parallelutils.createcourse")) {
                return true;
            }
            if (args.length == 0)
                return false;
            if (ParallelParkour.get().parkourNameExists(args[0])) {
                ParallelChat.sendParallelMessageTo(player, "A course with the name " + args[0] + " already exists!");
                return true;
            }
            ParkourLayout layout = ParallelParkour.get().getParkourCreation(player);
            if (layout != null) {
                ParallelChat.sendParallelMessageTo(player, "You are already creating a course!");
            }
            else {
                boolean allowEffects = args.length <= 1 || !args[1].equalsIgnoreCase("false");
                ParallelChat.sendParallelMessageTo(player, "Now creating course named " + args[0] + "! Place down a Gold Pressure Plate to get started!");
                ParallelParkour.get().startCreatingParkour(player, args[0], allowEffects);
            }
        }
        return true;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 2) {
            return List.of("true", "false");
        }
        return new ArrayList<>();
    }
}
