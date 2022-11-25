package parallelmc.parallelutils.modules.paralleltowns.commands;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.paralleltowns.ParallelTowns;

import java.util.List;

public class ParallelTownAcceptInvite extends TownCommand {

    private final String USAGE = "/town accept";

    public ParallelTownAcceptInvite() {
        super("accept", "Accept a town invite");
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull Command command, @NotNull String[] args) {
        if (args.length != 1) {
            player.sendMessage(USAGE);
            return false;
        }
        if (ParallelTowns.get().isPlayerInTown(player)) {
            ParallelChat.sendParallelMessageTo(player, "You are already in a town!");
        } else {
            if (ParallelTowns.get().hasPendingInvite(player)) {
                ParallelTowns.get().acceptTownInvite(player);
            }
            else {
                ParallelChat.sendParallelMessageTo(player, "You do not have any pending town invites.");
            }
        }
        return true;
    }

    @Override
    public List<String> getTabComplete(@NotNull Player player, @NotNull String[] args) { return null; }
}
