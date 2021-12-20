package parallelmc.parallelutils.modules.parallelchat.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ParallelColors implements CommandExecutor {

    private static final Component text = MiniMessage.get().parse("""
                <red>=========================
                  <white>&0 = <black>Black
                  <white>&1 = <dark_blue>Dark Blue
                  <white>&2 = <dark_green>Dark Green
                  <white>&3 = <dark_aqua>Dark Aqua
                  <white>&4 = <dark_red>Dark Red
                  <white>&5 = <dark_purple>Dark Purple
                  <white>&6 = <gold>Gold
                  <white>&7 = <gray>Gray
                  <white>&8 = <dark_grey>Dark Gray
                  <white>&9 = <blue>Blue
                  <white>&a = <green>Green
                  <white>&b = <aqua>Aqua
                  <white>&c = <red>Red
                  <white>&d = <light_purple>Light Purple
                  <white>&e = <yellow>Yellow
                  <white>&f = White
                  
                <gray>Type /formats to view the Chat Formats.
                <red>=========================""");

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        commandSender.sendMessage(text);
        return true;
    }
}
