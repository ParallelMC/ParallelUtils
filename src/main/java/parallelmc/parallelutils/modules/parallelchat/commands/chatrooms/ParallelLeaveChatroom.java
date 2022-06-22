package parallelmc.parallelutils.modules.parallelchat.commands.chatrooms;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.parallelchat.chatrooms.ChatRoom;

import java.util.ArrayList;
import java.util.List;

public class ParallelLeaveChatroom extends ChatroomCommand {

    private final String USAGE = "/cr leave";

    public ParallelLeaveChatroom() {
        super("leave", "Leave the chatroom you are in.");
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull Command command, String[] args) {
        if (args.length != 1) {
            player.sendMessage(USAGE);
            return false;
        }
        if (!ParallelChat.get().chatRoomManager.isPlayerInChatroom(player)) {
            ParallelChat.sendParallelMessageTo(player, "You are not in a chatroom!");
            return true;
        }
        ChatRoom c = ParallelChat.get().chatRoomManager.getPlayerChatRoom(player);
        if (c.isPlayerOwner(player)) {
            ParallelChat.sendParallelMessageTo(player, "You cannot leave the chatroom! You can use /cr disband if you want to remove the chatroom entirely.");
            return true;
        }
        ParallelChat.get().chatRoomManager.removePlayerFromChatroom(player);
        return true;
    }

    @Override
    public List<String> getTabComplete(@NotNull Player player, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
