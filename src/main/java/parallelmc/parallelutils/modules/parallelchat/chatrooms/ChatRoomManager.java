package parallelmc.parallelutils.modules.parallelchat.chatrooms;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class ChatRoomManager {
    private HashMap<String, ChatRoom> chatRooms = new HashMap<>();
    // list of players who are in a chatroom
    private final HashMap<UUID, String> playersInChatrooms = new HashMap<>();
    // list of players who have a chatroom passively active (i.e. by running /cr with no arguments)
    private final HashMap<UUID, String> hasChatroomActive = new HashMap<>();

    // list of players waiting to accept an invite
    private final HashMap<UUID, String> pendingInvites = new HashMap<>();

    private final Path jsonPath = Path.of(ParallelChat.get().getPlugin().getDataFolder().getAbsolutePath() + "/chatrooms.json");

    public ChatRoomManager() {
        if (!jsonPath.toFile().exists()) {
            Parallelutils.log(Level.WARNING, "ChatRooms JSON file does not exist, skipping loading.");
            return;
        }
        try {
            Type type = new TypeToken<HashMap<String, ChatRoom>>(){}.getType();
            this.chatRooms = new Gson().fromJson(Files.readString(jsonPath), type);
            Parallelutils.log(Level.INFO, "Loaded " + chatRooms.size() + " chat rooms.");
        } catch (IOException e) {
            Parallelutils.log(Level.SEVERE, "Failed to load chat rooms!\n" + e.getMessage());
        }
    }

    public void saveChatroomsToFile() {
        String json = new Gson().toJson(chatRooms);
        try {
            Files.writeString(jsonPath, json);
            Parallelutils.log(Level.INFO, "Saved " + chatRooms.size() + " chat rooms.");
        } catch (IOException e) {
            Parallelutils.log(Level.SEVERE, "Failed to save chat rooms!\n" + e.getMessage());
        }
    }


    public void addChatRoom(Player owner, String name, String color, boolean isPrivate) {
        this.chatRooms.put(name, new ChatRoom(owner.getUniqueId(), name, color, isPrivate));
        this.playersInChatrooms.put(owner.getUniqueId(), name);
    }

    public void removeChatRoom(String name) {
        this.chatRooms.remove(name);
    }

    public ChatRoom getChatRoom(String name) {
        return chatRooms.get(name);
    }

    public ChatRoom getPlayerChatRoom(Player player) {
        return chatRooms.get(playersInChatrooms.get(player.getUniqueId()));
    }

    public boolean isPlayerInChatroom(Player player) {
        return getPlayerChatRoom(player) != null;
    }

    public void addPlayerToChatroom(Player player, String name) {
        this.playersInChatrooms.put(player.getUniqueId(), name);
        chatRooms.get(name).addMember(player);
    }

    public void removePlayerFromChatroom(Player player) {
        ChatRoom c = getPlayerChatRoom(player);
        c.removeMember(player);
        this.playersInChatrooms.remove(player.getUniqueId());
        this.hasChatroomActive.remove(player.getUniqueId());
        player.hideBossBar(c.getBossBar());
    }

    public void kickPlayerFromChatroom(Player player, Player moderator) {
        ChatRoom c = getPlayerChatRoom(player);
        c.kickMember(player, moderator);
        this.playersInChatrooms.remove(player.getUniqueId());
        this.hasChatroomActive.remove(player.getUniqueId());
        player.hideBossBar(c.getBossBar());
    }

    public void invitePlayerToChatroom(Player player, Player moderator) {
        ChatRoom c = getPlayerChatRoom(player);
        this.pendingInvites.put(player.getUniqueId(), c.getName());
        ParallelChat.sendParallelMessageTo(player, "You have been invited to the chatroom " + c.getName() + " by " + moderator.getName() + ". Type /chatroom accept to join!");
        player.getServer().getScheduler().runTaskLater(ParallelChat.get().getPlugin(), () -> {
            if (hasPendingInvite(player)) {
                this.pendingInvites.remove(player.getUniqueId());
                ParallelChat.sendParallelMessageTo(player, "Chatroom invite expired.");
                ParallelChat.sendParallelMessageTo(moderator, "Chatroom invite expired.");
            }
        }, 600L);
    }

    public void acceptChatroomInvite(Player player) {
        addPlayerToChatroom(player, this.pendingInvites.get(player.getUniqueId()));
        this.pendingInvites.remove(player.getUniqueId());
    }

    public void disbandChatroom(ChatRoom c) {
        c.announceMessage("The chatroom has been disbanded.", NamedTextColor.RED);
        c.getMembers().forEach((u, b) -> {
            this.playersInChatrooms.remove(u);
            this.hasChatroomActive.remove(u);
            // sucks but we have to do it
            Player p = ParallelChat.get().getPlugin().getServer().getPlayer(u);
            if (p != null)
                p.hideBossBar(c.getBossBar());
        });
        removeChatRoom(c.getName());
    }

    public boolean hasPendingInvite(Player player) {
        return this.pendingInvites.containsKey(player.getUniqueId());
    }

    public void toggleActiveChatroom(Player player) {
        ChatRoom c = getPlayerChatRoom(player);
        if (hasChatroomActive.get(player.getUniqueId()) != null) {
            player.hideBossBar(c.getBossBar());
            hasChatroomActive.remove(player.getUniqueId());
        }
        else {
            player.showBossBar(c.getBossBar());
            hasChatroomActive.put(player.getUniqueId(), c.getName());
        }
    }

    public void removeActiveChatroom(Player player) {
        ChatRoom c = getPlayerChatRoom(player);
        if (c == null) return;
        player.hideBossBar(c.getBossBar());
        hasChatroomActive.remove(player.getUniqueId());
    }

    public boolean hasChatroomActive(Player player) {
        return hasChatroomActive.containsKey(player.getUniqueId());
    }

    public HashMap<String, ChatRoom> getChatRooms() { return chatRooms; }
}
