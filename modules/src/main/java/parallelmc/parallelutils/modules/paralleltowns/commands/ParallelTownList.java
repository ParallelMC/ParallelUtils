package parallelmc.parallelutils.modules.paralleltowns.commands;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.paralleltowns.ParallelTowns;

import java.util.ArrayList;
import java.util.List;

public class ParallelTownList extends TownCommand {
    private final String USAGE = "/town list";

    public ParallelTownList() {
        super("list", "Opens the town list");
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull Command command, String[] args) {
        if (args.length != 1) {
            player.sendMessage(USAGE);
            return false;
        }
        ParallelTowns.get().guiManager.openTownListMenuForPlayer(player);
        return true;
    }

    @Override
    public List<String> getTabComplete(@NotNull Player player, @NotNull String[] args) {
        return new ArrayList<>();
    }
}