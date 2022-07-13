package parallelmc.parallelutils.modules.parallelchat.commands.chatrooms;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import java.util.ArrayList;
import java.util.List;

public class ParallelAcceptInvite extends ChatroomCommand {

    private final String USAGE = "/cr accept";

    public ParallelAcceptInvite() {
        super("accept", "Accept a chatroom invite.");
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull Command command, String[] args) {
        if (args.length != 1) {
            player.sendMessage(USAGE);
            return false;
        }
        if (ParallelChat.get().chatRoomManager.isPlayerInChatroom(player)) {
            ParallelChat.sendParallelMessageTo(player, "You are already in a chatroom!");
            return true;
        }
        ParallelChat.get().chatRoomManager.acceptChatroomInvite(player);
        return true;
    }

    @Override
    public List<String> getTabComplete(@NotNull Player player, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
