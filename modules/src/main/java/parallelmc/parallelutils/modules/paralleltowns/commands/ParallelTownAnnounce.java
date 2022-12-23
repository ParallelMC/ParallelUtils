package parallelmc.parallelutils.modules.paralleltowns.commands;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.paralleltowns.ParallelTowns;
import parallelmc.parallelutils.modules.paralleltowns.Town;
import parallelmc.parallelutils.modules.paralleltowns.TownMember;
import parallelmc.parallelutils.modules.paralleltowns.TownRank;

import java.util.ArrayList;
import java.util.List;

public class ParallelTownAnnounce extends TownCommand {
    private final String USAGE = "/town announce <message>";

    public ParallelTownAnnounce() {
        super("announce", "Announces a message to all town members");
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull Command command, String[] args) {
        if (args.length < 2) {
            player.sendMessage(USAGE);
            return false;
        }
        Town town = ParallelTowns.get().getPlayerTown(player);
        if (town == null) {
            ParallelChat.sendParallelMessageTo(player, "You are not in a town!");
            return true;
        }
        TownMember member = town.getMember(player);
        // tee hee v2
        args[0] = "";
        String msg = ParallelChat.getStringArg(args);
        if (member.getTownRank() == TownRank.MEMBER) {
            ParallelChat.sendParallelMessageTo(player, "You must be an Official or higher to send town announcements!");
            return true;
        }
        else {
            // TODO: possibly allow selecting the color of the announcement
            town.sendMessage("Announcement from " + player.getName() + ":" + msg, NamedTextColor.AQUA);
        }
        return true;
    }

    @Override
    public List<String> getTabComplete(@NotNull Player player, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
