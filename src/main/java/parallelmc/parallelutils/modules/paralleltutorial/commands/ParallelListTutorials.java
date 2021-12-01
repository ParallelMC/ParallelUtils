package parallelmc.parallelutils.modules.paralleltutorial.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.paralleltutorial.ParallelTutorial;

public class ParallelListTutorials implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player player) {
            if (!player.hasPermission("parallelutils.listtutorials")) {
                return true;
            }
        }
        StringBuilder sb = new StringBuilder("Loaded tutorials:\n");
        ParallelTutorial.get().GetTutorials().forEach((s, i) -> {
            sb.append("- ");
            sb.append(s);
            sb.append("(");
            sb.append(i.size());
            sb.append(" instructions)\n");
        });
        commandSender.sendMessage(Component.text(sb.toString()));
        return true;
    }
}
