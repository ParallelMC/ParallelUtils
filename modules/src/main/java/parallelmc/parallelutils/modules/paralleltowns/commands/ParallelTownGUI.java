package parallelmc.parallelutils.modules.paralleltowns.commands;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.paralleltowns.ParallelTowns;

import java.util.ArrayList;
import java.util.List;

public class ParallelTownGUI extends TownCommand {
    private final String USAGE = "/town gui";

    public ParallelTownGUI() {
        super("gui", "Opens the town GUI");
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull Command command, String[] args) {
        if (args.length != 1) {
            player.sendMessage(USAGE);
            return false;
        }
        if (!ParallelTowns.get().isPlayerInTown(player)) {
            ParallelChat.sendParallelMessageTo(player, "You are not in a town! Use /town create to create one!");
        }
        else {
            ParallelTowns.get().openMainMenuForPlayer(player);
        }
        return true;
    }

    @Override
    public List<String> getTabComplete(@NotNull Player player, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
