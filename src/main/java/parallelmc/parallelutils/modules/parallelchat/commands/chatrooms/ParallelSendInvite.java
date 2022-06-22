package parallelmc.parallelutils.modules.parallelchat.commands.chatrooms;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.parallelchat.chatrooms.ChatRoom;

import java.util.ArrayList;
import java.util.List;

public class ParallelSendInvite extends ChatroomCommand {

    private final String USAGE = "/cr invite <player>";

    public ParallelSendInvite() {
        super("invite", "Invite a player to a private chatroom.");
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
        ChatRoom c = ParallelChat.get().chatRoomManager.getPlayerChatRoom(player);
        if (!c.isPlayerModerator(player)) {
            ParallelChat.sendParallelMessageTo(player, "Only moderators may invite players.");
            return true;
        }
        if (args[1].equals(player.getName())) {
            ParallelChat.sendParallelMessageTo(player, "You cannot invite yourself!");
            return true;
        }
        if (!c.isPrivate()) {
            ParallelChat.sendParallelMessageTo(player, "This chatroom is public, anyone can join without an invite.");
            return true;
        }
        Player invite = player.getServer().getPlayer(args[1]);
        if (invite == null) {
            ParallelChat.sendParallelMessageTo(player, "Could not find player " + args[1]);
            return true;
        }
        if (ParallelChat.get().chatRoomManager.isPlayerInChatroom(invite)) {
            ParallelChat.sendParallelMessageTo(player, args[1] + " is already in a chatroom!");
            return true;
        }
        ParallelChat.sendParallelMessageTo(player, "Sent an invite to " + args[1] + ". They have 30 seconds to accept.");
        ParallelChat.get().chatRoomManager.invitePlayerToChatroom(invite, player);
        return true;
    }

    @Override
    public List<String> getTabComplete(@NotNull Player player, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
