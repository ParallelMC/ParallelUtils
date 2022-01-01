package parallelmc.parallelutils.modules.paralleltutorial.commands;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.paralleltutorial.ParallelTutorial;

import java.util.logging.Level;

public class ParallelLeaveTutorial implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player player) {
            if (ParallelTutorial.playersInTutorial.containsKey(player)) {
                BukkitTask tutorial = ParallelTutorial.runningTutorials.get(player);
                if (tutorial != null) {
                    tutorial.cancel();
                    Location start = ParallelTutorial.playersInTutorial.get(player);
                    player.teleport(start);
                    player.setGameMode(GameMode.SURVIVAL);
                    ParallelTutorial.playersInTutorial.remove(player);
                    ParallelTutorial.runningTutorials.remove(player);
                    ParallelChat.sendParallelMessageTo(player, "Successfully exited the tutorial.");
                }
            }
            else {
                ParallelChat.sendParallelMessageTo(player, "You are not in a tutorial!");
            }
        }
        return true;
    }
}
