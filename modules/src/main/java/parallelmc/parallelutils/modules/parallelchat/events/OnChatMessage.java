package parallelmc.parallelutils.modules.parallelchat.events;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.parallelchat.emojis.Emoji;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OnChatMessage implements Listener {

    private static final Pattern mention = Pattern.compile("@(\\S+)", Pattern.MULTILINE);
    private static final Pattern caps = Pattern.compile("[A-Z]", Pattern.MULTILINE);
    private static final Pattern emoji = Pattern.compile(":\\w+:");

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
            msgStr = msgStr.replaceAll("&[[0-9][a-f]]", "");
        }

        // check hex permission
        if (!player.hasPermission("parallelutils.chat.hex")) {
            msgStr = msgStr.replaceAll("&#(.{6})", "");
        }

        // check formats permission
        if (!player.hasPermission("parallelutils.chat.formats")) {
            msgStr = msgStr.replaceAll("&[[l-o]r]", "");
        }

        // check magic permission
        if (!player.hasPermission("parallelutils.chat.magic")) {
            msgStr = msgStr.replaceAll("&k", "");
        }

        event.message(Component.text(msgStr));

        // Chat Logger
        // Log chat no matter what happens below
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        try {
            ParallelChat.get().chatLogWriter.write("[" + timeFormatter.format(now) + "]: " + player.getName() + "> " + msgStr + "\n");
        }
        catch (IOException e) {
            ParallelUtils.log(Level.SEVERE, "Failed to log chat message!");
        }

        // @ Mention check
        // have to use strings to figure out the player names
        Matcher mentionMatcher = mention.matcher(msgStr);
        ArrayList<Player> mentionedPlayers = new ArrayList<>();
        while (mentionMatcher.find()) {
            String match = mentionMatcher.group();
            Player matchPlayer = player.getServer().getPlayer(mentionMatcher.group(1));
            if (matchPlayer != null) {
                event.message(event.message().replaceText(x -> x.matchLiteral(match).replacement(Component.text(mentionMatcher.group(), NamedTextColor.YELLOW))));
                matchPlayer.playSound(matchPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
                mentionedPlayers.add(matchPlayer);
            }
        }

        // Emojis
        // again easier to use the string version of the message to search for emotes
        Matcher emojiMatcher = emoji.matcher(msgStr);
        while (emojiMatcher.find()) {
            String match = emojiMatcher.group();
            Emoji emoji = ParallelChat.get().emojiManager.getEmojis(match);
            if (emoji != null && player.hasPermission("parallelutils.emoji." + emoji.name())) {
                event.message(event.message().replaceText(y -> y.matchLiteral(emoji.id()).replacement(Component.text(emoji.replacement()).hoverEvent(Component.text(emoji.id()).asHoverEvent()))));
            }
        }

        // StaffChat, TeamChat, LoreChat
        if (ParallelChat.get().getStaffChat().contains(player.getUniqueId())) {
            event.setCancelled(true);
            ParallelChat.sendMessageToStaffChat(player, event.message());
            return;
        } else if (ParallelChat.get().getTeamChat().contains(player.getUniqueId())) {
            event.setCancelled(true);
            ParallelChat.sendMessageToTeamChat(player, event.message());
            return;
        } else if (ParallelChat.get().getLoreChat().contains(player.getUniqueId())) {
            event.setCancelled(true);
            ParallelChat.sendMessageToLoreChat(player, event.message());
            return;
        }

        // ChatRooms
        if (ParallelChat.get().chatRoomManager.hasChatroomActive(player)) {
            event.setCancelled(true);
            ParallelChat.get().chatRoomManager.getPlayerChatRoom(player).sendMessage(player, event.message());
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
            String checkSlurs = msgStr.toLowerCase();
            // Regex checking
            // (?s) makes . accept ALL characters
            // Note, this may do funky things with word boundaries.
            // Regex can specify \b to look for a word boundary specifically
            //if (ParallelChat.get().bannedWords.stream().anyMatch(x -> checkSlurs.matches("(?s).*" + x + ".*"))) {
            for (String x : ParallelChat.get().bannedWords) {
                if (checkSlurs.contains(x)) {
                    //if (ParallelChat.get().allowedWords.stream().noneMatch(checkSlurs::contains)) {
                        event.setCancelled(true);
                        ParallelChat.sendParallelMessageTo(player, "Please do not say that in chat.");
                        Component slurMsg = MiniMessage.miniMessage().deserialize("<gray>").append(player.displayName())
                                                                                   .append(Component.text(" [Anti-Swear]: "))
                                                                                    .append(event.message())
                                                                                    .append(Component.text(" | Match: " + x));
                        for (Player p : server.getOnlinePlayers()) {
                            if (p.hasPermission("parallelutils.notify.antislur")) {
                                p.sendMessage(slurMsg);
                            }
                        }
                        return;
                    //}
                }
            }
        }


        // Anti-Caps
        if (ParallelChat.get().capsEnabled && !player.hasPermission("parallelutils.bypass.anticaps")) {
            if (msgStr.length() >= ParallelChat.get().capsMinMsgLength) {
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


        // remove dnd players from the recipient list if they have not been mentioned
        // also show the message to the player if they send it
        event.viewers().removeAll(ParallelChat.dndPlayers.keySet()
             .stream()
             .filter(x -> !mentionedPlayers.contains(x))
             .filter(x -> x != player)
             .collect(Collectors.toSet()));

        // re-render the formatted message and send it
        event.renderer(ChatRenderer.viewerUnaware((source, sourceDisplayName, message) ->
        {
            message =  LegacyComponentSerializer.legacyAmpersand().deserialize(LegacyComponentSerializer.legacyAmpersand().serialize(message));
            // Item chat
            ItemStack item = player.getInventory().getItemInMainHand();

            if (item.getType() != Material.AIR) {
                TextComponent itemComponent = Component.text()
                        .append(item.displayName().hoverEvent(item.asHoverEvent()))
                        .append(Component.text(" x" + item.getAmount(), item.displayName().color()))
                        .build();

                message = message.replaceText(x -> x.once().match("\\[item\\]").replacement(itemComponent));
            }
            return ParallelChat.get().formatForGroup(source, sourceDisplayName, message);
        }));
        // not sure if this is necessary but I don't want to touch it
        event.message(event.message());
    }
}
