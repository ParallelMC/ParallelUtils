package parallelmc.parallelutils.modules.parallelchat.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ParallelChatCommands implements CommandExecutor {
    private static final Component text = MiniMessage.miniMessage().deserialize("""
                <red>=========================
                  <white><bold>Chat Commands:</bold>
                  /uwu
                  /dab
                  /shrug
                  /wave
                  /wut
                  /hotsoup
                  /vibecheck
                  /gamermoment
                <red>=========================""");

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        commandSender.sendMessage(text);
        return true;
    }
}
