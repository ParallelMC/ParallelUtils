package parallelmc.parallelutils.modules.parallelparkour.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.parallelparkour.ParallelParkour;
import parallelmc.parallelutils.modules.parallelparkour.ParkourLayout;


public class ParallelCreateParkour implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (commandSender instanceof Player player) {
            if (!player.hasPermission("parallelutils.createparkour")) {
                return true;
            }
            if (args.length == 0)
                return false;
            if (ParallelParkour.get().parkourNameExists(args[0])) {
                ParallelChat.sendParallelMessageTo(player, "A parkour with the name " + args[0] + " already exists!");
            }
            ParkourLayout layout = ParallelParkour.get().getParkourCreation(player);
            if (layout != null) {
                ParallelChat.sendParallelMessageTo(player, "You are already creating a parkour!");
            }
            else {
                ParallelChat.sendParallelMessageTo(player, "Now creating a parkour named " + args[0] + "! Place down a Gold Pressure Plate to get started!");
                ParallelParkour.get().startCreatingParkour(player, args[0]);
            }
        }
        return true;
    }
}
