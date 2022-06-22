package parallelmc.parallelutils.modules.paralleltutorial.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.paralleltutorial.ParallelTutorial;

public class ParallelReloadTutorials implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player player) {
            if (!player.hasPermission("parallelutils.reloadtutorials")) {
                return true;
            }
        }
        if (ParallelTutorial.get().runningTutorials.size() > 0) {
            commandSender.sendMessage("Cannot reload tutorials while there are players in a tutorial!");
            return true;
        }
        ParallelTutorial.get().LoadTutorials();
        commandSender.sendMessage("Loaded " + ParallelTutorial.get().GetTutorials().size() + " tutorials.");
        return true;
    }
}
