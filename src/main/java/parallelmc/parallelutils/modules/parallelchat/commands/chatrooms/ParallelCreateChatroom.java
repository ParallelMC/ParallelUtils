package parallelmc.parallelutils.modules.parallelchat.commands.chatrooms;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import java.util.ArrayList;
import java.util.List;

public class ParallelCreateChatroom extends ChatroomCommand {

    private final String USAGE = "/cr create <name> <color> <isPrivate (true/false)>";

    public ParallelCreateChatroom() {
        super("create", "Creates a new chatroom.");
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull Command command, String[] args) {
        if (args.length != 4) {
            player.sendMessage(USAGE);
            return false;
        }
        if (args[1].length() > 16) {
            ParallelChat.sendParallelMessageTo(player, "Chat room name must be 16 characters or less!");
            return true;
        }
        if (ParallelChat.get().bannedWords.contains(args[1])) {
            ParallelChat.sendParallelMessageTo(player, "Invalid chatroom name.");
            return true;
        }
        if (ParallelChat.get().chatRoomManager.getChatRoom(args[1]) != null) {
            ParallelChat.sendParallelMessageTo(player, "Chat room with name " + args[1] + " already exists!");
            return true;
        }
        if (ParallelChat.get().chatRoomManager.getPlayerChatRoom(player) != null) {
            ParallelChat.sendParallelMessageTo(player, "You are already in a chatroom!");
            return true;
        }
        String color = args[2].toLowerCase();
        if (NamedTextColor.NAMES.value(color) == null) {
            ParallelChat.sendParallelMessageTo(player, "Unknown color " + color + "! Colors are Minecraft namespaced! (red, dark_green, aqua, etc.)");
            return true;
        }
        boolean isPrivate = Boolean.parseBoolean(args[3]);
        ParallelChat.get().chatRoomManager.addChatRoom(player, args[1], color, isPrivate);
        ParallelChat.sendParallelMessageTo(player, "Created new " + (isPrivate ? "private " : "") + "chatroom " + args[1] + "!");
        return true;
    }

    @Override
    public List<String> getTabComplete(@NotNull Player player, @NotNull String[] args) {
        if (args.length == 3)
            return NamedTextColor.NAMES.keys().stream().toList();
        if (args.length == 4)
            return List.of("true", "false");
        else return new ArrayList<>();
    }
}
