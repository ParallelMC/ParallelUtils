package parallelmc.parallelutils.modules.paralleltowns.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.parallelchat.commands.chatrooms.ChatroomCommand;
import parallelmc.parallelutils.modules.paralleltowns.ParallelTowns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ParallelTownHelp extends TownCommand {

    private static final int PAGE_SIZE = 8;

    public ParallelTownHelp() {
        super("help", "Show a list of Town related commands");
    }

    @Override
    public boolean execute(@NotNull Player sender, @NotNull Command command, @NotNull String[] args) {

        Map<String, TownCommand> commands = ParallelTowns.get().getTownCommands();

        List<String> sortedNames = new ArrayList<>(commands.keySet());
        Collections.sort(sortedNames);

        int numPages = (int)Math.ceil((double)commands.size() / (double)PAGE_SIZE);

        int page = 1;

        if (args.length > 1) {
            page = Integer.parseInt(args[1]);
        }

        if (page > numPages || page <= 0) {
            sender.sendMessage("Invalid page number!");
            return true;
        }

        int start = (page-1)*PAGE_SIZE;
        int end = start+PAGE_SIZE;

        if (end > commands.size()) {
            end = commands.size();
        }

        TextComponent.Builder builder = Component.text()
                .append(Component.text("--------- ", NamedTextColor.YELLOW))
                .append(Component.text("Help: Index ("))
                .append(Component.text(page))
                .append(Component.text("/"))
                .append(Component.text(numPages))
                .append(Component.text(")"))
                .append(Component.text(" --------------------\n", NamedTextColor.YELLOW));

        // add this so people are aware of this functionality
        builder.append(Component.text("/town ", NamedTextColor.GREEN)).append(Component.text(": Opens the main town GUI. Use this for most town settings.")).append(Component.newline());

        for (int i=start; i<end; i++) {
            String name = sortedNames.get(i);
            String helpText = commands.get(name).helpText;

            builder.append(Component.text("/town " + name, NamedTextColor.GREEN))
                    .append(Component.text(": "))
                    .append(Component.text(helpText))
                    .append(Component.newline());
        }

        if (page != 1) {
            builder.append(Component.text("[Back] ", NamedTextColor.AQUA)
                    .clickEvent(ClickEvent.runCommand("/town help " + (page - 1))));
        }
        if (page != numPages) {
            builder.append(Component.text(" [Forward]", NamedTextColor.AQUA)
                    .clickEvent(ClickEvent.runCommand("/town help " + (page+1))));
        }

        sender.sendMessage(builder.build());

        return true;
    }

    @Override
    public List<String> getTabComplete(@NotNull Player sender, @NotNull String[] args) {
        ArrayList<String> list = new ArrayList<>();
        if (args.length == 2) {
            list.add("page");
        }

        return list;
    }
}
