package parallelmc.parallelutils.modules.points.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.points.Points;

public class RecalculatePoints implements CommandExecutor {
    private final ParallelUtils puPlugin;

    public RecalculatePoints(ParallelUtils puPlugin) {
        this.puPlugin = puPlugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (commandSender.isOp() || commandSender instanceof ConsoleCommandSender
                || (commandSender instanceof Player player && player.hasPermission("parallelutils.recalculatepoints"))) {
            commandSender.sendMessage("Recalculating advancement points for all players, this might take a while...");
            Bukkit.getScheduler().runTaskAsynchronously(puPlugin, () -> {
                long start = System.currentTimeMillis();
                int result = Points.get().recalculatePlayerPoints();
                long end = System.currentTimeMillis();
                if (result == -1) {
                    commandSender.sendMessage("Failed to recalculate, see the console for any errors.");
                }
                else {
                    commandSender.sendMessage("Recalculated advancement points for " + result + " players in " + (end - start) + "ms");
                }
            });
        }
        return true;
    }
}
