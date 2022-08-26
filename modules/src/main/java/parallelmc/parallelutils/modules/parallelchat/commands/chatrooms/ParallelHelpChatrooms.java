package parallelmc.parallelutils.modules.parallelchat.commands.chatrooms;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ParallelHelpChatrooms extends ChatroomCommand {

    private static final int PAGE_SIZE = 8;

    private final ParallelUtils puPlugin;

    public ParallelHelpChatrooms() {
        super("help", "Show a list of Chatroom commands");

        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

        if (plugin == null) {
            ParallelUtils.log(Level.SEVERE, "Unable to initialize ParallelHelpChatrooms. Plugin " + Constants.PLUGIN_NAME
                    + " does not exist!");
            puPlugin = null;
            return;
        }

        puPlugin = (ParallelUtils) plugin;
    }

    @Override
    public boolean execute(@NotNull Player sender, @NotNull Command command, @NotNull String[] args) {

        Map<String, ChatroomCommand> commands = ParallelChat.get().getChatroomCommands();

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
        builder.append(Component.text("/cr ", NamedTextColor.GREEN)).append(Component.text(": Toggles speaking in the chatroom")).append(Component.newline());

        for (int i=start; i<end; i++) {
            String name = sortedNames.get(i);
            String helpText = commands.get(name).helpText;

            builder.append(Component.text("/cr " + name, NamedTextColor.GREEN))
                    .append(Component.text(": "))
                    .append(Component.text(helpText))
                    .append(Component.newline());
        }

        if (page != 1) {
            builder.append(Component.text("[Back] ", NamedTextColor.AQUA)
                    .clickEvent(ClickEvent.runCommand("/cr help " + (page - 1))));
        }
        if (page != numPages) {
            builder.append(Component.text(" [Forward]", NamedTextColor.AQUA)
                    .clickEvent(ClickEvent.runCommand("/cr help " + (page+1))));
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
