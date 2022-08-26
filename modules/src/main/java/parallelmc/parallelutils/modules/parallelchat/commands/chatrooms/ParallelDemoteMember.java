package parallelmc.parallelutils.modules.parallelchat.commands.chatrooms;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.parallelchat.chatrooms.ChatRoom;

import java.util.ArrayList;
import java.util.List;

public class ParallelDemoteMember  extends ChatroomCommand {

    private final String USAGE = "/cr demote <player>";

    public ParallelDemoteMember() {
        super("demote", "Demotes a player to member status in a chatroom.");
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
            ParallelChat.sendParallelMessageTo(player, "Only the owner can demote players!");
            return true;
        }
        if (args[0].equals(player.getName())) {
            ParallelChat.sendParallelMessageTo(player, "You cannot demote yourself!");
            return true;
        }
        Player demote = player.getServer().getPlayer(args[1]);
        if (demote == null) {
            ParallelChat.sendParallelMessageTo(player, "Could not find player " + args[1]);
            return true;
        }
        if (!c.hasMember(demote)) {
            ParallelChat.sendParallelMessageTo(player, args[1] + " is not in this chatroom!");
            return true;
        }
        if (!c.isPlayerModerator(demote)) {
            ParallelChat.sendParallelMessageTo(player, args[1] + " is already a member!");
            return true;
        }
        c.demoteMember(demote);
        return true;
    }

    @Override
    public List<String> getTabComplete(@NotNull Player player, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if(args.length == 2){
            ChatRoom c = ParallelChat.get().chatRoomManager.getPlayerChatRoom(player);
            c.getMembers().forEach((u, b) -> {
                OfflinePlayer p = player.getServer().getOfflinePlayer(u);
                if (p.isOnline()) {
                    list.add(p.getName());
                }
            });
        }
        return list;
    }
}
