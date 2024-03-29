package parallelmc.parallelutils.modules.parallelparkour.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.parallelparkour.ParallelParkour;
import parallelmc.parallelutils.modules.parallelparkour.ParkourTime;

import java.util.ArrayList;
import java.util.List;

// TODO: add some sort of pagination to this
public class ParallelLeaderboard implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (commandSender instanceof Player player) {
            if (args.length == 0)
                return false;
            if (!ParallelParkour.get().parkourNameExists(args[0])) {
                ParallelChat.sendParallelMessageTo(player, "A course with the name " + args[0] + " does not exist!");
                return true;
            }
            List<ParkourTime> times = ParallelParkour.get().getTopTimesFor(args[0], 10);
            ParallelChat.sendParallelMessageTo(player, MiniMessage.miniMessage().deserialize("<gold>Speedrun Leaderboard for: <yellow>" + args[0]));
            for (int i = 0; i < Math.min(times.size(), 10); i++) {
                ParkourTime time = times.get(i);
                OfflinePlayer p = Bukkit.getOfflinePlayer(time.player());
                Component msg = MiniMessage.miniMessage().deserialize(String.format("<gold>%d. <green>%s <yellow>by %s", i+1, ParallelParkour.get().getTimeString(time.time()), p.getName()));
                ParallelChat.sendParallelMessageTo(player, msg);
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return ParallelParkour.get().getAllLayoutNames();
        }
        return new ArrayList<>();
    }
}
