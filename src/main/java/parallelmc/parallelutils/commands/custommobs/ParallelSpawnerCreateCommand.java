package parallelmc.parallelutils.commands.custommobs;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.commands.ParallelCommand;
import parallelmc.parallelutils.commands.permissions.ParallelPermission;

public class ParallelSpawnerCreateCommand extends ParallelCommand {
    public ParallelSpawnerCreateCommand(String name, ParallelPermission permission) {
        super(name, permission);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {
        return false;
    }
}
