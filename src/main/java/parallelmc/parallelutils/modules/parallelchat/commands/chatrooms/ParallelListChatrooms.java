package parallelmc.parallelutils.modules.parallelchat.commands.chatrooms;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.parallelchat.chatrooms.ChatRoom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParallelListChatrooms extends ChatroomCommand {

    private final String USAGE = "/cr list";

    public ParallelListChatrooms() {
        super("list", "Lists all public chatrooms");
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull Command command, String[] args) {
        if (args.length != 1) {
            player.sendMessage(USAGE);
            return false;
        }
        Component text = Component.text("Public Chatrooms: ", NamedTextColor.GOLD);
        for (Map.Entry<String, ChatRoom> e : ParallelChat.get().chatRoomManager.getChatRooms().entrySet()) {
            if (e.getValue().isPrivate()) continue;
            text = text.append(Component.text(e.getKey() + " ", NamedTextColor.NAMES.value(e.getValue().getColor())));
        }
        if (ParallelChat.get().chatRoomManager.isPlayerInChatroom(player)) {
            ChatRoom c = ParallelChat.get().chatRoomManager.getPlayerChatRoom(player);
            text = text.append(Component.newline()).append(Component.text("Your Chatroom: ", NamedTextColor.GOLD)).append(Component.text(c.getName(), NamedTextColor.NAMES.value(c.getColor())));
        }
        player.sendMessage(text);
        return true;
    }

    @Override
    public List<String> getTabComplete(@NotNull Player player, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
