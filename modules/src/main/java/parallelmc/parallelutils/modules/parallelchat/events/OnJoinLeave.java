package parallelmc.parallelutils.modules.parallelchat.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.discordintegration.JoinQuitSuppressorListener;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.parallelchat.messages.CustomMessageSelection;

public class OnJoinLeave implements Listener {

    private final ParallelUtils puPlugin;

    public OnJoinLeave(ParallelUtils puPlugin) {
        this.puPlugin = puPlugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ParallelChat.get().removeFromTeamChat(player);
        ParallelChat.get().removeFromStaffChat(player);
        ParallelChat.get().removeFromLoreChat(player);
        ParallelChat.get().chatRoomManager.removeActiveChatroom(player);

        // Setup custom leave messages
        String customMsg = ParallelChat.get().customMessageManager.getLeaveMessageForPlayer(player);

        Component leave = MiniMessage.miniMessage().deserialize("<dark_gray>[<red>-<dark_gray>] ");
        if (customMsg == null) {
            leave = leave.append(MiniMessage.miniMessage().deserialize("<yellow><player> left the game", TagResolver.resolver(Placeholder.parsed("player", player.getName()))));
        } else {
            leave = leave.append(Component.text(customMsg, NamedTextColor.YELLOW));
        }

        event.quitMessage(leave);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Server server = player.getServer();

        // Get custom join messages
        String customMsg = ParallelChat.get().customMessageManager.getJoinMessageForPlayer(player);

        Component join = MiniMessage.miniMessage().deserialize("<dark_gray>[<green>+<dark_gray>] ");
        if (customMsg == null) {
            join = join.append(MiniMessage.miniMessage().deserialize("<yellow><player> joined the game", TagResolver.resolver(Placeholder.parsed("player", player.getName()))));
        } else {
            join = join.append(Component.text(customMsg, NamedTextColor.YELLOW));
        }

        // If player is new, we want more stuff
        if (!player.hasPlayedBefore()) {
            Component welcome = MiniMessage.miniMessage().deserialize("\n<dark_aqua><strikethrough>⎯⎯⎯⎯</strikethrough> Welcome to <white><bold>Parallel</bold><dark_aqua>, <player>! <strikethrough>⎯⎯⎯⎯", TagResolver.resolver(Placeholder.parsed("player", player.getName())));
            join = join.append(welcome);

            // private welcome info
            Component info = MiniMessage.miniMessage().deserialize("""
                <blue>{Discord} <gray>https://discord.parallelmc.org
                <blue>{Voting} <gray>Vote for the server each day using /vote!
                <blue>{Resource Pack} <gray>Use our resource pack to see our custom items!""");
            player.sendMessage(info);
            player.getServer().dispatchCommand(server.getConsoleSender(), "ibooks give rules " + player.getName());
        } else {
            // private welcome info
            Component info = MiniMessage.miniMessage().deserialize("""
                <dark_aqua><strikethrough>⎯⎯⎯⎯</strikethrough> Welcome back to <white><bold>Parallel</bold><dark_aqua>, <player>! <strikethrough>⎯⎯⎯⎯</strikethrough>
                <blue>{Discord} <gray>https://discord.parallelmc.org
                <blue>{Voting} <gray>Vote for the server each day using /vote!""", TagResolver.resolver(Placeholder.parsed("player", player.getName())));

            player.sendMessage(info);
        }

        event.joinMessage(join);
    }
}
