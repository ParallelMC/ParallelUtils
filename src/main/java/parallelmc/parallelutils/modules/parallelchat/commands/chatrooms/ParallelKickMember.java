package parallelmc.parallelutils.modules.parallelchat.commands.chatrooms;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.parallelchat.chatrooms.ChatRoom;

import java.util.ArrayList;
import java.util.List;

public class ParallelKickMember extends ChatroomCommand {

    private final String USAGE = "/chatroom kick <player>";

    public ParallelKickMember() {
        super("kick", "Kicks a player from the chatroom.");
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull Command command, String[] args) {
        if (args.length != 2) {
            player.sendMessage(USAGE);
            return false;
        }
        if (!ParallelChat.get().chatRoomManager.isPlayerInChatroom(player)) {
            ParallelChat.sendParallelMessageTo(player, "You are not in a chatroom!");
            return true;
        }
        if (args[1].equals(player.getName())) {
            ParallelChat.sendParallelMessageTo(player, "You cannot kick yourself!");
            return true;
        }
        Player kick = player.getServer().getPlayer(args[1]);
        if (kick == null) {
            ParallelChat.sendParallelMessageTo(player, "Could not find player " + args[1]);
            return true;
        }
        ChatRoom c = ParallelChat.get().chatRoomManager.getPlayerChatRoom(player);
        if (!c.isPlayerModerator(player)) {
            ParallelChat.sendParallelMessageTo(player, "Only moderators can kick players!");
            return true;
        }
        if (!c.hasMember(kick)) {
            ParallelChat.sendParallelMessageTo(player, args[1] + " is not in this chatroom!");
            return true;
        }
        if (!c.isPlayerOwner(player) && (c.isPlayerModerator(player) && c.isPlayerModerator(kick))) {
            ParallelChat.sendParallelMessageTo(player, "You cannot kick another moderator!");
            return true;
        }
        ParallelChat.get().chatRoomManager.kickPlayerFromChatroom(kick, player);
        return true;
    }

    @Override
    public List<String> getTabComplete(@NotNull Player player, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
