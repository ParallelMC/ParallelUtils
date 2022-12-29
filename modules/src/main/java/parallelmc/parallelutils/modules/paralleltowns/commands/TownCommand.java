package parallelmc.parallelutils.modules.paralleltowns.commands;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class TownCommand {
    public String name;
    public String helpText;

    /**
     * Creates a new TownCommand with the specified name and help text
     *
     * @param name       The name of the command
     * @param helpText   The helpText of the command
     */
    public TownCommand(String name, String helpText) {
        this.name = name;
        this.helpText = helpText;
    }

    /**
     * Execute the command given the params from the Bukkit {@code onCommand} method
     *
     * @param player  The Player that is executing this Command
     * @param command The Bukkit {@code Command} object
     * @param args    The arguments for this command
     * @return Returns true if the command executed successfully
     */
    public abstract boolean execute(@NotNull Player player, @NotNull Command command, @NotNull String[] args);

    /**
     * Retrieve the tab complete array associated with the given command and arguments
     *
     * @param player The sender of this command
     * @param args   The arguments associated with the command
     * @return The List associated with the given command, sender, and arguments
     */
    public abstract List<String> getTabComplete(@NotNull Player player, @NotNull String[] args);
}
