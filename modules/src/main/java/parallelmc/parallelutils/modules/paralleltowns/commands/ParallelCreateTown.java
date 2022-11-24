package parallelmc.parallelutils.modules.paralleltowns.commands;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.paralleltowns.ParallelTowns;

import java.util.List;

public class ParallelCreateTown extends TownCommand {

    private final String USAGE = "/town create <name>";

    public ParallelCreateTown() {
        super("create", "Creates a new town");
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull Command command, @NotNull String[] args) {
        if (args.length != 2) {
            player.sendMessage(USAGE);
            return false;
        }
        if (ParallelTowns.get().getPlayerTown(player) != null) {
            ParallelChat.sendParallelMessageTo(player, "You are already in a town!");
        }
        else {
            // TODO: check if a town exists
            ParallelTowns.get().addTown(player, args[1]);
            ParallelChat.sendParallelMessageTo(player, "Created new town: " + args[1]);
        }
        return true;
    }

    @Override
    public List<String> getTabComplete(@NotNull Player player, @NotNull String[] args) {
        return null;
    }
}
