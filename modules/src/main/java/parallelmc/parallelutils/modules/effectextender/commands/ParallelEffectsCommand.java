package parallelmc.parallelutils.modules.effectextender.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.commands.ParallelCommand;
import parallelmc.parallelutils.commands.permissions.ParallelPermission;
import parallelmc.parallelutils.modules.effectextender.listeners.EffectListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A command to display the current max effect durations for the player
 * Usage: /pu effects
 */
public class ParallelEffectsCommand extends ParallelCommand {

    public ParallelEffectsCommand() {
        super("effects", "A debug command to list the current effect maxes the player has",
                new ParallelPermission("parallelutils.effects"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {
        if (!hasPermissions(sender)) {
            return false;
        }

        if (sender instanceof Player player) {
            HashMap<PotionEffectType, Integer> maxes = EffectListener.playerEffects.get(player);
            if (maxes == null || maxes.values().size() <= 0) {
                player.sendMessage("You do not have any active effects!");
                return true;
            }
            player.sendMessage("Your current active effect max durations:");
            maxes.forEach((k, v) -> player.sendMessage(String.format("%s | %d secs", k, v)));
        }

        return true;
    }

    @Override
    public List<String> getTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        return new ArrayList<>();
    }


}
