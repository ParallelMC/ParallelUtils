package parallelmc.parallelutils.modules.parallelchat.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.logging.Level;

public class OnCommand implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        // if the command sender bypasses then do nothing
        if (player.hasPermission("parallelutils.bypass.commandspy")) {
            return;
        }

        // Command Spy
        Component cmdSpy = MiniMessage.miniMessage().deserialize("<yellow>[<aqua>Command-Spy:<yellow>] <dark_gray>" + player.getName() + ": <aqua>" + event.getMessage());
        UUID senderUUID = player.getUniqueId();
        ParallelChat.get().socialSpyUsers.forEach((u, o) -> {
            if (u.equals(senderUUID)) return;
            if (o.isCmdSpy()) {
                // this kinda sucks but not much can be done
                Player spyUser = player.getServer().getPlayer(u);
                if (spyUser != null) {
                    spyUser.sendMessage(cmdSpy);
                }
            }
        });

        // Command Logger
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        try {
            ParallelChat.get().cmdLogWriter.write("[" + timeFormatter.format(now) + "]: " + player.getName() + "> " + event.getMessage());
        }
        catch (IOException e) {
            Parallelutils.log(Level.SEVERE, "Failed to log command!");
        }
    }
}
