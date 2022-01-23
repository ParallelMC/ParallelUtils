package parallelmc.parallelutils.modules.parallelchat.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ParallelFormats implements CommandExecutor {

    private static final Component text = MiniMessage.miniMessage().deserialize("""
            <red>=========================
              <white>&k = <obfuscated>Obfuscated</obfuscated>
              <white>&l = <bold>Bold</bold>
              <white>&m = <strikethrough>Strikethrough</strikethrough>
              <white>&n = <underline>underline</underline>
              <white>&o = <italic>Italic</italic>
              <white>&r = Reset
              
            <gray>Type /colors to view the Chat Colors.
            <red>=========================""");

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        commandSender.sendMessage(text);
        return true;
    }
}
