package parallelmc.parallelutils.modules.paralleltutorial.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.paralleltutorial.ParallelTutorial;
import parallelmc.parallelutils.modules.paralleltutorial.scripting.Instruction;

import java.util.ArrayList;

public class ParallelTutorialInfo implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player player) {
            if (!player.hasPermission("parallelutils.tutorialinfo")) {
                return true;
            }
        }
        if (args.length == 0)
            return false;
        if (!ParallelTutorial.get().HasTutorial(args[0])) {
            commandSender.sendMessage("Could not find tutorial " + args[0]);
        }
        ArrayList<Instruction> instructions = ParallelTutorial.get().GetTutorials().get(args[0]);
        StringBuilder sb = new StringBuilder("Tutorial " + args[0] + ": \n");
        sb.append(instructions.size());
        sb.append(" instructions:\n");
        instructions.forEach(i -> {
            sb.append(i.name());
            sb.append(' ');
            if (i.args() != null) {
                for (String s : i.args()) {
                    sb.append(s);
                    sb.append(' ');
                }
            }
            sb.append('\n');
        });
        commandSender.sendMessage(Component.text(sb.toString()));
        return true;
    }
}
