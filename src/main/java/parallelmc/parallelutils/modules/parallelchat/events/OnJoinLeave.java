package parallelmc.parallelutils.modules.parallelchat.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.placeholder.Placeholder;
import net.kyori.adventure.text.minimessage.placeholder.PlaceholderResolver;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

public class OnJoinLeave implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ParallelChat.get().removeFromTeamChat(player);
        ParallelChat.get().removeFromStaffChat(player);
        event.quitMessage(null);
        Component leave = MiniMessage.miniMessage().deserialize("<yellow><player> left the game", PlaceholderResolver.placeholders(Placeholder.miniMessage("player", player.getName())));
        // if you find a better way of doing this feel free to replace
        for (Player p : player.getServer().getOnlinePlayers()) {
            if (!p.canSee(player)) {
                // if ANYONE can't see the player, don't show the message
                return;
            }
        }

        for (Player p : player.getServer().getOnlinePlayers()) {
            p.sendMessage(leave);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Server server = player.getServer();
        event.joinMessage(null);
        Component join = MiniMessage.miniMessage().deserialize("<yellow><player> joined the game", PlaceholderResolver.placeholders(Placeholder.miniMessage("player", player.getName())));
        if (!player.hasPlayedBefore()) {
            Component welcome = MiniMessage.miniMessage().deserialize("\n<dark_aqua><strikethrough>⎯⎯⎯⎯</strikethrough> Welcome to <white><bold>Parallel</bold><dark_aqua>, <player>! <strikethrough>⎯⎯⎯⎯", PlaceholderResolver.placeholders(Placeholder.miniMessage("player", player.getName())));
            join = join.append(welcome);
            for (Player p : server.getOnlinePlayers()) {
                p.sendMessage(join);
            }
            // private welcome info
            Component info = MiniMessage.miniMessage().deserialize("""
                <blue>{Discord} <gray>https://discord.parallelmc.org
                <blue>{Voting} <gray>Vote for the server each day using /vote!
                <blue>{Resource Pack} <gray>Use our resource pack to see our custom items!""");
            player.sendMessage(info);
            player.getServer().dispatchCommand(server.getConsoleSender(), "ibooks give rules " + player.getName());
        }
        else {
            for (Player p : player.getServer().getOnlinePlayers()) {
                if (!p.canSee(player)) {
                    // if ANYONE can't see the player, don't show the message
                    return;
                }
            }

            for (Player p : player.getServer().getOnlinePlayers()) {
                p.sendMessage(join);
            }

            // private welcome info
            Component info = MiniMessage.miniMessage().deserialize("""
                <dark_aqua><strikethrough>⎯⎯⎯⎯</strikethrough> Welcome back to <white><bold>Parallel</bold><dark_aqua>, <player>! <strikethrough>⎯⎯⎯⎯</strikethrough>
                <blue>{Discord} <gray>https://discord.parallelmc.org
                <blue>{Voting} <gray>Vote for the server each day using /vote!""", PlaceholderResolver.placeholders(Placeholder.miniMessage("player", player.getName())));
            player.sendMessage(info);
        }


    }
}
