package parallelmc.parallelutils.modules.parallelchat.chatrooms;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import java.util.HashMap;
import java.util.UUID;

public class ChatRoom {
    private final UUID owner;
    private final String name;
    private final NamedTextColor chatColor;
    private final HashMap<UUID, Boolean> members;
    private final boolean isPrivate;
    private final BossBar activeBossbar;

    public ChatRoom(UUID owner, String name, String color, boolean isPrivate) {
        this.owner = owner;
        this.name = name;
        this.chatColor = NamedTextColor.NAMES.value(color);
        this.members = new HashMap<>();
        this.members.put(owner, true);
        this.isPrivate = isPrivate;
        this.activeBossbar = BossBar.bossBar(Component.text("ChatRoom: " + name, chatColor), 1, BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS);
    }

    public void addMember(Player player) {
        this.members.put(player.getUniqueId(), false);
        announceMessage(player.getName() + " joined the chatroom.", NamedTextColor.GREEN);
    }

    public void removeMember(Player player) {
        announceMessage(player.getName() + " left the chatroom.", NamedTextColor.RED);
        this.members.remove(player.getUniqueId());
    }

    public void kickMember(Player player, Player moderator) {
        announceMessage(player.getName() + " was kicked by " + moderator.getName(), NamedTextColor.RED);
        this.members.remove(player.getUniqueId());
    }

    public void promoteMember(Player player) {
        this.members.put(player.getUniqueId(), true);
        announceMessage(player.getName() + " has been promoted to moderator.", NamedTextColor.GREEN);
    }

    public void demoteMember(Player player) {
        this.members.put(player.getUniqueId(), false);
        announceMessage(player.getName() + " has been demoted to member.", NamedTextColor.RED);
    }

    public boolean isPlayerModerator(Player player) {
        if (members.get(player.getUniqueId()) == null) return false;
        return members.get(player.getUniqueId());
    }

    public boolean isPlayerOwner(Player player) {
        return player.getUniqueId() == owner;
    }

    public boolean hasMember(Player player) { return members.containsKey(player.getUniqueId()); }

    public void sendMessage(Player sender, Component message) {
        Component text = MiniMessage.miniMessage().deserialize("<gold>[<" + chatColor + ">" + name + "<gold>] <" + chatColor + ">" + getPrefix(sender) + " <gray>> ").append(message.color(chatColor));
        members.forEach((u, b) -> {
           Player p = ParallelChat.get().getPlugin().getServer().getPlayer(u);
           if (p != null) {
               p.sendMessage(text);
           }
        });
        if (!sender.hasPermission("parallelutils.bypass.chatroomspy")) {
            Component chatroomSpy = MiniMessage.miniMessage().deserialize("<yellow>[<aqua>ChatRoom-Spy<yellow>] ").append(text);
            // ChatRoom Spy
            ParallelChat.get().socialSpyUsers.forEach((u, o) -> {
                if (u.equals(sender.getUniqueId())) return;
                if (o.isSocialSpy()) {
                    // this kinda sucks but not much can be done
                    Player spyUser = sender.getServer().getPlayer(u);
                    if (spyUser != null) {
                        spyUser.sendMessage(chatroomSpy);
                    }
                }
            });
        }
    }

    public void announceMessage(String message, NamedTextColor color) {
        Component text = MiniMessage.miniMessage().deserialize("<gold>[<" + chatColor + ">" + name + "<gold>] <gray>> ").append(Component.text(message, color));
        members.forEach((u, b) -> {
            Player p = ParallelChat.get().getPlugin().getServer().getPlayer(u);
            if (p != null) {
                p.sendMessage(text);
            }
        });
        Component chatroomSpy = MiniMessage.miniMessage().deserialize("<yellow>[<aqua>ChatRoom-Spy<yellow>] ").append(text);
        // ChatRoom Spy
        ParallelChat.get().socialSpyUsers.forEach((u, o) -> {
            if (o.isSocialSpy()) {
                // this kinda sucks but not much can be done
                Player spyUser = ParallelChat.get().getPlugin().getServer().getPlayer(u);
                if (spyUser != null) {
                    spyUser.sendMessage(chatroomSpy);
                }
            }
        });
    }

    private String getPrefix(Player player) {
        if (isPlayerOwner(player))
            return "<bold>O</bold> " + player.getName();
        if (isPlayerModerator(player))
            return "<bold>M</bold> " + player.getName();
        return player.getName();
    }

    public UUID getOwner() { return this.owner; }

    public String getName() { return this.name; }

    public boolean isPrivate() { return this.isPrivate; }

    public BossBar getBossBar() { return this.activeBossbar; }

    public HashMap<UUID, Boolean> getMembers() { return this.members; }


}
