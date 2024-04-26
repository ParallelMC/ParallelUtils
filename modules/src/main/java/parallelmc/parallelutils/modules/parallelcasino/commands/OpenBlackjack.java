package parallelmc.parallelutils.modules.parallelcasino.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelcasino.ParallelCasino;
import parallelmc.parallelutils.modules.parallelcasino.games.blackjack.BlackjackInventory;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.util.GUIManager;


public class OpenBlackjack implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if (commandSender instanceof Player player) {
            if (ParallelCasino.get().isPlayerInGame(player)) {
                ParallelChat.sendParallelMessageTo(player, "You are already playing a casino game!");
                return true;
            }
            GUIManager.get().openInventoryForPlayer(player, new BlackjackInventory());
            ParallelCasino.get().addPlayerToGame(player);
            return true;
        }
        return true;
    }
}