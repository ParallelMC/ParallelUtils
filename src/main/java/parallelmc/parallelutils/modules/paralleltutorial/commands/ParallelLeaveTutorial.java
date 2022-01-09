package parallelmc.parallelutils.modules.paralleltutorial.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.paralleltutorial.ParallelTutorial;

public class ParallelLeaveTutorial implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player player) {
            if (ParallelTutorial.runningTutorials.containsKey(player)) {
                BukkitTask tutorial = ParallelTutorial.runningTutorials.get(player);
                if (tutorial != null) {
                    tutorial.cancel();
                    ParallelTutorial.get().endTutorialFor(player);
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
