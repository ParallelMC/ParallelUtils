package parallelmc.parallelutils.modules.paralleltowns.commands;

import org.bukkit.command.Command;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.paralleltowns.ParallelTowns;
import parallelmc.parallelutils.modules.paralleltowns.TownMember;
import parallelmc.parallelutils.modules.paralleltowns.TownRank;

import java.util.ArrayList;
import java.util.List;

public class ParallelTownInvite extends TownCommand {

    private final String USAGE = "/town invite <player>";

    public ParallelTownInvite() {
        super("invite", "Invite a player to your town");
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull Command command, @NotNull String[] args) {
        if (args.length != 2) {
            player.sendMessage(USAGE);
            return false;
        }
        if (!ParallelTowns.get().isPlayerInTown(player)) {
            ParallelChat.sendParallelMessageTo(player, "You are not in a town!");
        }
        else {
            TownMember member = ParallelTowns.get().getPlayerTownStatus(player);
            if (member.getTownRank() == TownRank.MEMBER) {
                ParallelChat.sendParallelMessageTo(player, "Only Town Officials and above can invite players.");
            }
            else {
                if (args[1].equals(player.getName())) {
                    ParallelChat.sendParallelMessageTo(player, "You cannot invite yourself!");
                    return true;
                }
                Player invite = player.getServer().getPlayer(args[1]);
                if (invite == null) {
                    ParallelChat.sendParallelMessageTo(player, "Could not find player " + args[1]);
                    return true;
                }
                if (ParallelTowns.get().isPlayerInTown(invite)) {
                    ParallelChat.sendParallelMessageTo(player, args[1] + " is already in a town!");
                    return true;
                }
                ParallelChat.sendParallelMessageTo(player, "Sent a town invite to " + args[1] + ". They have 30 seconds to accept.");
                ParallelTowns.get().invitePlayerToTown(player, invite);
            }
        }
        return true;
    }

    @Override
    public List<String> getTabComplete(@NotNull Player player, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if(args.length == 2){
            list.addAll(player.getServer().getOnlinePlayers().stream().map(HumanEntity::getName).toList());
        }
        return list;
    }
}
