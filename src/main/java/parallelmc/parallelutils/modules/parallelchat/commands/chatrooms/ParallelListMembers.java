package parallelmc.parallelutils.modules.parallelchat.commands.chatrooms;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.parallelchat.chatrooms.ChatRoom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ParallelListMembers extends ChatroomCommand {

    private final String USAGE = "/chatroom members";

    public ParallelListMembers() {
        super("members", "Lists all chatroom members.");
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
        OfflinePlayer owner = player.getServer().getOfflinePlayer(c.getOwner());
        Component moderators = Component.text("Moderators: ", NamedTextColor.GOLD);
        Component members = Component.text("Members: ", NamedTextColor.GOLD);
        for (Map.Entry<UUID, Boolean> e : c.getMembers().entrySet()) {
            OfflinePlayer p = player.getServer().getOfflinePlayer(e.getKey());
            if (p == owner) continue;
            if (e.getValue()) moderators = moderators.append(Component.text(p.getName() + " ", p.isOnline() ? NamedTextColor.GREEN : NamedTextColor.RED));
            else members = members.append(Component.text(p.getName() + " ", p.isOnline() ? NamedTextColor.GREEN : NamedTextColor.RED));
        }
        Component text = MiniMessage.miniMessage().deserialize("<gold>" + c.getName() + " Members:<newline>Owner: " + (owner.isOnline() ? "<green>" : "<red>") + owner.getName())
                        .append(moderators.append(Component.newline())).append(Component.newline()).append(members.append(Component.newline()));
        player.sendMessage(text);
        return true;
    }


    @Override
    public List<String> getTabComplete(@NotNull Player player, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
