package parallelmc.parallelutils.modules.bitsandbobs.minimodules.togglepvp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import java.util.UUID;

public class TogglePvpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player player) {
            UUID uuid = player.getUniqueId();
            if (!TogglePvpManager.pvpToggles.containsKey(uuid)) {
                TogglePvpManager.pvpToggles.put(uuid, true);
                ParallelChat.sendParallelMessageTo(player, "Toggled PVP on!");
            }
            else {
                boolean pvp = TogglePvpManager.pvpToggles.get(uuid);
                TogglePvpManager.pvpToggles.put(uuid, !pvp);
                // use the opposite in the lambda since it's flipped
                ParallelChat.sendParallelMessageTo(player, "Toggled PVP " + (pvp ? "off!" : "on!"));
            }
        }
        return true;
    }
}
