package parallelmc.parallelutils.modules.parallelchat.commands.chatrooms;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.parallelchat.chatrooms.ChatRoom;

import java.util.ArrayList;
import java.util.List;

public class ParallelJoinChatroom extends ChatroomCommand {

    private final String USAGE = "/chatroom join <name>";

    public ParallelJoinChatroom() {
        super("join", "Join a public chatroom.");
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull Command command, String[] args) {
        if (args.length != 2) {
            player.sendMessage(USAGE);
            return false;
        }
        if (ParallelChat.get().chatRoomManager.isPlayerInChatroom(player)) {
            ParallelChat.sendParallelMessageTo(player, "You are already in a chatroom!");
            return true;
        }
        ChatRoom c = ParallelChat.get().chatRoomManager.getChatRoom(args[1]);
        if (c == null) {
            ParallelChat.sendParallelMessageTo(player, "Unknown chatroom name " + args[1]);
            return true;
        }
        if (c.isPrivate()) {
            ParallelChat.sendParallelMessageTo(player, args[1] + " is a private chatroom. You must be invited in order to join.");
            return true;
        }
        ParallelChat.get().chatRoomManager.addPlayerToChatroom(player, c.getName());
        return true;
    }

    @Override
    public List<String> getTabComplete(@NotNull Player player, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
