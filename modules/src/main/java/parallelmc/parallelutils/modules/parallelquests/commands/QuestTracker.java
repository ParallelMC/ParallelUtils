package parallelmc.parallelutils.modules.parallelquests.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelquests.gui.QuestTrackerInventory;
import parallelmc.parallelutils.util.GUIManager;

public class QuestTracker implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player player) {
            GUIManager.get().openInventoryForPlayer(player, new QuestTrackerInventory());
            return true;
        }
        return false;
    }
}
