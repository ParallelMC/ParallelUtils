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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ParallelChat.get().removeFromTeamChat(player);
        ParallelChat.get().removeFromStaffChat(player);
        ParallelChat.get().removeFromLoreChat(player);
        ParallelChat.get().chatRoomManager.removeActiveChatroom(player);

        boolean canSend = true;

        event.quitMessage(null);

        String customMsg = ParallelChat.get().customMessageManager.getLeaveMessageForPlayer(player);

        Component leave = MiniMessage.miniMessage().deserialize("<dark_gray>[<red>-<dark_gray>] ");
        if (customMsg == null)
            leave = leave.append(MiniMessage.miniMessage().deserialize("<yellow><player> left the game", TagResolver.resolver(Placeholder.parsed("player", player.getName()))));
        else
            leave = leave.append(Component.text(customMsg, NamedTextColor.YELLOW));

        if (puPlugin.getModule("DiscordIntegration") != null) {
            synchronized (JoinQuitSuppressorListener.hiddenUsersLock) { // NOTE: This MIGHT cause lag problems. It shouldn't, but beware
                if (JoinQuitSuppressorListener.hiddenUsers.contains(player.getName().strip())) {
                    event.quitMessage(null); // This might need to change, but it needs to be tested
                    canSend = false;
                }
            }
        } else {
            // if you find a better way of doing this feel free to replace
            for (Player p : player.getServer().getOnlinePlayers()) {
                if (!p.canSee(player)) {
                    // if ANYONE can't see the player, don't show the message
                    return;
                }
            }
        }

        if (canSend) {
            for (Player p : player.getServer().getOnlinePlayers()) {
                p.sendMessage(leave);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        // TODO: Refactor this function please. It's a mess
        Player player = event.getPlayer();
        Server server = player.getServer();
        event.joinMessage(null);

        String customMsg = ParallelChat.get().customMessageManager.getJoinMessageForPlayer(player);

        boolean canSend = true;

        Component join = MiniMessage.miniMessage().deserialize("<dark_gray>[<green>+<dark_gray>] ");
        if (customMsg == null)
            join = join.append(MiniMessage.miniMessage().deserialize("<yellow><player> joined the game", TagResolver.resolver(Placeholder.parsed("player", player.getName()))));
        else
            join = join.append(Component.text(customMsg, NamedTextColor.YELLOW));
        
        if (!player.hasPlayedBefore()) {
            Component welcome = MiniMessage.miniMessage().deserialize("\n<dark_aqua><strikethrough>⎯⎯⎯⎯</strikethrough> Welcome to <white><bold>Parallel</bold><dark_aqua>, <player>! <strikethrough>⎯⎯⎯⎯", TagResolver.resolver(Placeholder.parsed("player", player.getName())));
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

            if (puPlugin.getModule("DiscordIntegration") != null) {
                synchronized (JoinQuitSuppressorListener.hiddenUsersLock) { // NOTE: This MIGHT cause lag problems. It shouldn't, but beware
                    if (JoinQuitSuppressorListener.hiddenUsers.contains(player.getName().strip())) {
                        event.joinMessage(null); // This might need to change, but it needs to be tested
                        canSend = false;


                        if (Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
                            return;
                        }
                    }
                }
            } else {
                for (Player p : player.getServer().getOnlinePlayers()) {
                    if (!p.canSee(player)) {
                        // if ANYONE can't see the player, don't show the message
                        return;
                    }
                }
            }

            if (canSend) {
                for (Player p : player.getServer().getOnlinePlayers()) {
                    p.sendMessage(join);
                }
            }

            // private welcome info
            Component info = MiniMessage.miniMessage().deserialize("""
                <dark_aqua><strikethrough>⎯⎯⎯⎯</strikethrough> Welcome back to <white><bold>Parallel</bold><dark_aqua>, <player>! <strikethrough>⎯⎯⎯⎯</strikethrough>
                <blue>{Discord} <gray>https://discord.parallelmc.org
                <blue>{Voting} <gray>Vote for the server each day using /vote!""", TagResolver.resolver(Placeholder.parsed("player", player.getName())));

            player.sendMessage(info);
        }


    }
}
