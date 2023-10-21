package parallelmc.parallelutils.modules.chestshops.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChestShopCommands implements CommandExecutor, TabCompleter {
    private final HashMap<String, ChestShopCommand> commandMap;

    public ChestShopCommands() {
        commandMap = new HashMap<>();
    }

    /**
     * Adds a new command to the commandmap
     *
     * @param name    The name of the command
     * @param command The command to be run when the name is called
     * @return Returns true when the command was added successfully, false if the command already exists.
     */
    public boolean addCommand(String name, ChestShopCommand command) {
        if (commandMap.containsKey(name.toLowerCase().strip())) {
            return false;
        }

        commandMap.put(name.toLowerCase().strip(), command);

        return true;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // only players can run chestshop commands
        if (sender instanceof Player player) {
            if (!player.hasPermission("parallelutils.chestshop.debug")) {
                return true;
            }
            if (command.getName().equalsIgnoreCase("chestshop")) {
                // If no command was specified, toggle the player's active chatroom
                if (args.length == 0) {
                    ParallelChat.sendParallelMessageTo(player, "Please select a sub-command.");
                } else {
                    ChestShopCommand executingCommand = commandMap.get(args[0]);

                    if (executingCommand != null) {
                        executingCommand.execute(player, command, args);
                    } else {
                        ParallelChat.sendParallelMessageTo(player, "Unknown chestshop subcommand.");
                    }
                }
            }
        }
        return true;
    }

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        ArrayList<String> list = new ArrayList<>();

        if (sender instanceof Player player) {
            // Show ChestShop commands

            String lowerName = command.getName().toLowerCase().strip();

            if ((lowerName.equals("chestshop")) && args.length == 1) {
                // List every sub-command
                list.addAll(commandMap.keySet());
            } else {
                if (commandMap.containsKey(args[0].toLowerCase().strip())) {
                    return commandMap.get(args[0].toLowerCase().strip()).getTabComplete(player, args);
                }
            }
        }
        return list;
    }

    /**
     * Return a deep copy of the command map. Modifying the returned map will not modify the commands
     *
     * @return A deep copy of the command map
     */
    public Map<String, ChestShopCommand> getCommands() {
        return commandMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
