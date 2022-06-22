package parallelmc.parallelutils.modules.parallelchat.commands.chatrooms;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.parallelchat.chatrooms.ChatRoom;

import java.util.ArrayList;
import java.util.List;

public class ParallelPromoteMember extends ChatroomCommand {

    private final String USAGE = "/cr promote <player>";

    public ParallelPromoteMember() {
        super("promote", "Promotes a player to moderator status in a chatroom.");
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull Command command, String[] args) {
        if (args.length != 2) {
            player.sendMessage(USAGE);
            return false;
        }
        ChatRoom c = ParallelChat.get().chatRoomManager.getPlayerChatRoom(player);
        if (c == null) {
            ParallelChat.sendParallelMessageTo(player, "You are not in a chatroom!");
            return true;
        }
        if (!c.isPlayerOwner(player)) {
            ParallelChat.sendParallelMessageTo(player, "Only the owner can promote players!");
            return true;
        }
        if (args[1].equals(player.getName())) {
            ParallelChat.sendParallelMessageTo(player, "You cannot promote yourself!");
            return true;
        }
        Player promote = player.getServer().getPlayer(args[1]);
        if (promote == null) {
            ParallelChat.sendParallelMessageTo(player, "Could not find player " + args[1]);
            return true;
        }
        if (!c.hasMember(promote)) {
            ParallelChat.sendParallelMessageTo(player, args[1] + " is not in this chatroom!");
            return true;
        }
        if (c.isPlayerModerator(promote)) {
            ParallelChat.sendParallelMessageTo(player, args[1] + " is already a moderator!");
            return true;
        }
        c.promoteMember(promote);
        return true;
    }

    @Override
    public List<String> getTabComplete(@NotNull Player player, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
