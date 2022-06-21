package parallelmc.parallelutils.modules.parallelchat.commands.chatrooms;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.parallelchat.chatrooms.ChatRoom;

import java.util.ArrayList;
import java.util.List;

public class ParallelDisbandChatroom extends ChatroomCommand {

    // require name to prevent accidentally running the command
    private final String USAGE = "/chatroom disband <name>";

    public ParallelDisbandChatroom() {
        super("disband", "Disbands a chatroom.");
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
            ParallelChat.sendParallelMessageTo(player, "Only the owner can disband the chatroom!");
            return true;
        }
        ParallelChat.get().chatRoomManager.disbandChatroom(c);
        return true;
    }

    @Override
    public List<String> getTabComplete(@NotNull Player player, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
