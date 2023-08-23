package parallelmc.parallelutils.modules.npcshops.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.npcshops.NPCShops;

public class OpenMaggieShop implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (commandSender.isOp() || commandSender instanceof ConsoleCommandSender) {
            if (args.length == 0)
                return false;
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                commandSender.sendMessage("Could not find player " + args[0]);
                return true;
            }
            NPCShops.get().getMaggieShop().openShopFor(player);
        }
        return true;
    }
}
