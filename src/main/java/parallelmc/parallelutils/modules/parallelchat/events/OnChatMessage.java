package parallelmc.parallelutils.modules.parallelchat.events;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OnChatMessage implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChatMessage(AsyncChatEvent event) {
        Player player = event.getPlayer();
        Server server = player.getServer();

        // convert msg to string for use in multiple modules
        // minimessage html counts towards the string length which messes up some calculations
        // so we're forced to use legacy
        String msgStr = LegacyComponentSerializer.legacyAmpersand().serialize(event.message());

        // for some reason the component .replace wasn't working with this regex
        // so I guess I have to use strings
        // check colors permission
        if (!player.hasPermission("parallelutils.chat.colors")) {
            Parallelutils.log(Level.WARNING, "Cancelling colors");
            event.message(Component.text(msgStr.replaceAll("&[[0-9][a-f]]", "")));
        }

        // check formats permission
        if (!player.hasPermission("parallelutils.chat.formats")) {
            Parallelutils.log(Level.WARNING, "Cancelling formats");
            event.message(Component.text(msgStr.replaceAll("&[[k-o]r]", "")));
        }

        // Chat Logger
        // Log chat no matter what happens below
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        try {
            ParallelChat.get().chatLogWriter.write("[" + timeFormatter.format(now) + "]: " + player.getName() + "> " + msgStr + "\n");
        }
        catch (IOException e) {
            Parallelutils.log(Level.SEVERE, "Failed to log chat message!");
        }

        /* TODO: json magic stuff with components to create colored mentions
        // @ Mention check
        Pattern mention = Pattern.compile("@(\\S+)", Pattern.MULTILINE);
        Matcher mentionMatcher = mention.matcher(msgStr);
        while (mentionMatcher.find()) {
            Player match = server.getPlayer(mentionMatcher.group(1));
            if (match != null) {
                // if the mention is valid then notify the player they have been mentioned
                match.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            }
        } */

        // StaffChat + TeamChat
        if (ParallelChat.get().getStaffChat().contains(player.getUniqueId())) {
            event.setCancelled(true);
            ParallelChat.sendMessageToStaffChat(player, event.message());
            return;
        } else if (ParallelChat.get().getTeamChat().contains(player.getUniqueId())) {
            event.setCancelled(true);
            ParallelChat.sendMessageToTeamChat(player, event.message());
            return;
        }

        // Mute Chat
        if (ParallelChat.get().isChatDisabled) {
            if (!player.hasPermission("parallelutils.bypass.mutechat")) {
                ParallelChat.sendParallelMessageTo(player, "Nobody hears you! The chat is currently muted.");
                event.setCancelled(true);
                return;
            }
        }

        // Anti-Slur
        if (!player.hasPermission("parallelutils.bypass.antislur")) {
            String checkSlurs = msgStr.toLowerCase().replace(" ", "");
            if (ParallelChat.get().bannedWords.stream().anyMatch(checkSlurs::contains)) {
                event.setCancelled(true);
                ParallelChat.sendParallelMessageTo(player, "Please do not say that in chat.");
                Component slurMsg = MiniMessage.get().parse("<gray>[Anti-Swear]: ").append(event.message());
                for (Player p : server.getOnlinePlayers()) {
                    if (p.hasPermission("parallelutils.notify.antislur")) {
                        p.sendMessage(slurMsg);
                    }
                }
                return;
            }
        }

        // Anti-Caps
        if (ParallelChat.get().capsEnabled && !player.hasPermission("parallelutils.bypass.anticaps")) {
            if (msgStr.length() >= ParallelChat.get().capsMinMsgLength) {
                Pattern caps = Pattern.compile("[A-Z]", Pattern.MULTILINE);
                Matcher capsMatcher = caps.matcher(msgStr);
                double matches = 0D;
                while (capsMatcher.find()) {
                    matches++;
                }
                if ((matches / (double) msgStr.length()) * 100D >= (double) ParallelChat.get().capsPercentage) {
                    event.message(LegacyComponentSerializer.legacyAmpersand().deserialize(msgStr.toLowerCase()));
                }
            }
        }

        // re-render the formatted message and send it
        event.renderer(ChatRenderer.viewerUnaware((source, sourceDisplayName, message)
                -> ParallelChat.get().formatForGroup(source, sourceDisplayName, message)));
        // not sure if this is necessary but I don't want to touch it
        event.message(event.message());
    }
}
