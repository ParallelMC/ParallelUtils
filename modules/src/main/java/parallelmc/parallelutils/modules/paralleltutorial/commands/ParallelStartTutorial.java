package parallelmc.parallelutils.modules.paralleltutorial.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.paralleltutorial.ParallelTutorial;

import java.util.logging.Level;

public class ParallelStartTutorial implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player player) {
            if (!player.hasPermission("parallelutils.starttutorial")) {
                return true;
            }
        }
        if (args.length == 0)
            return false;
        Player player = commandSender.getServer().getPlayer(args[0]);
        if (player == null) {
            commandSender.sendMessage("Could not find player " + args[0]);
            return true;
        }
        if (!ParallelTutorial.get().HasTutorial(args[1])) {
            commandSender.sendMessage("Could not find tutorial " + args[1]);
            return true;
        }
        boolean debug = args.length == 3 && args[2].equals("-d");
        ParallelTutorial.get().RunTutorialFor(player, args[1], debug);
        return true;
    }
}
