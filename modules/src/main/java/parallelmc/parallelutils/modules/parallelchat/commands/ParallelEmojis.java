package parallelmc.parallelutils.modules.parallelchat.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.parallelchat.emojis.Emoji;

public class ParallelEmojis implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player player) {
            StringBuilder sb = new StringBuilder("<red>=========================<white>\n");
            // TODO: allow clicking on the emoji to insert it into a message
            for (Emoji e : ParallelChat.get().emojiManager.getEmojis().values()) {
                sb.append(e.id());
                sb.append('\n');
            }
            sb.append("\n<gray>Type an emoji ID in a message to turn it into an emoji!\n");
            sb.append("<red>=========================");
            player.sendMessage(MiniMessage.miniMessage().deserialize(sb.toString()));
        }
        return true;
    }
}
