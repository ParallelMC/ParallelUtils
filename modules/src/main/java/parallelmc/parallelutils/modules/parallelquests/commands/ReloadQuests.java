package parallelmc.parallelutils.modules.parallelquests.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelquests.ParallelQuests;

public class ReloadQuests implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (commandSender.isOp() || commandSender instanceof ConsoleCommandSender) {
            commandSender.sendMessage("Reloading quests, stuff may break!");
            ParallelQuests.get().reloadQuests();
            commandSender.sendMessage("Loaded " + ParallelQuests.get().getAllQuests().size() + " quests.");
        }
        return true;
    }
}
