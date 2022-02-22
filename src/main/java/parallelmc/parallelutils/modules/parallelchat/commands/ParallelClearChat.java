package parallelmc.parallelutils.modules.parallelchat.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ParallelClearChat implements CommandExecutor {

    // minecraft chat stores 100 messages
    // do a few more just to be safe
    private final String clearChatMsg = "\n".repeat(124)
            + "<white>*<red><strikethrough>--------------------------------------------<reset><white>*\n"
            + "<yellow>The chat has been cleared by <player>\n"
            + "<white>*<red><strikethrough>--------------------------------------------<reset><white>*";

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (commandSender instanceof Player sender) {
            if (!sender.hasPermission("parallelutils.clearchat")) {
                return true;
            }
        }

        Component message = MiniMessage.miniMessage().deserialize(clearChatMsg, TagResolver.resolver(Placeholder.parsed("player", commandSender.getName())));
        String clearChatBypassMsg = "<yellow>The chat has been cleared by <player>, but it wasn't for you. :)";
        Component bypassMessage = MiniMessage.miniMessage().deserialize(clearChatBypassMsg, TagResolver.resolver(Placeholder.parsed("player", commandSender.getName())));

        for (Player p : commandSender.getServer().getOnlinePlayers()) {
            if (p.hasPermission("parallelutils.bypass.clearchat"))
                p.sendMessage(bypassMessage);
            else
                p.sendMessage(message);
        }
        return true;
    }
}
