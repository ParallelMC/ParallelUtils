package parallelmc.parallelutils.modules.paralleltowns.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.paralleltowns.ParallelTowns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TownCommands implements CommandExecutor, TabCompleter {

    private final HashMap<String, TownCommand> commandMap = new HashMap<>();

    /**
     * Adds a new command to the commandmap
     *
     * @param name    The name of the command
     * @param command The command to be run when the name is called
     * @return Returns true when the command was added successfully, false if the command already exists.
     */
    public boolean addCommand(String name, TownCommand command) {
        if (commandMap.containsKey(name.toLowerCase().strip())) {
            return false;
        }

        commandMap.put(name.toLowerCase().strip(), command);

        return true;
    }

    public HashMap<String, TownCommand> getTownCommands() {
        return commandMap;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // only players can run town commands
        if (sender instanceof Player player) {
            if (command.getName().equalsIgnoreCase("town")) {
                if (args.length == 0) {
                    // If no command was specified, treat it like the gui command
                    if (!ParallelTowns.get().isPlayerInTown(player)) {
                        ParallelChat.sendParallelMessageTo(player, "You are not in a town! Use /town create to create one!");
                    }
                    else {
                        ParallelTowns.get().guiManager.openMainMenuForPlayer(player);
                    }
                }
                else {
                    TownCommand executingCommand = commandMap.get(args[0]);

                    if (executingCommand != null) {
                        executingCommand.execute(player, command, args);
                    } else {
                        ParallelChat.sendParallelMessageTo(player, "Unknown town subcommand.");
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
            // Show Town commands

            String lowerName = command.getName().toLowerCase().strip();

            if (lowerName.equals("town") && args.length == 1) {
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

}
