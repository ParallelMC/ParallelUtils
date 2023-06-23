package parallelmc.parallelutils.modules.parallelparkour;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import javax.annotation.Nullable;

public class ParkourPlayer {
    private final Player player;
    private final BossBar bossBar;
    private final long startTime;
    private long endTime;
    private long bestTime;
    private int currentCheckpoint;
    private final int lastCheckpoint;
    private final ParkourLayout layout;
    private BukkitTask runnable;

    public ParkourPlayer(Player player, ParkourLayout layout) {
        this.player = player;
        this.layout = layout;
        this.currentCheckpoint = 1;
        this.lastCheckpoint = layout.positions().size();
        this.bossBar = createBossbar();
        this.startTime = System.currentTimeMillis();
        this.endTime = -1;
        start();
    }

    private void start() {
        ParallelChat.sendParallelMessageTo(player, MiniMessage.miniMessage().deserialize("<gold>Starting Course: <yellow>" + layout.name()));
        var topTime = ParallelParkour.get().getTopTimesFor(layout.name(), 1);
        if (topTime.size() == 0) {
            ParallelChat.sendParallelMessageTo(player, MiniMessage.miniMessage().deserialize("<gold>Top Time: <yellow>None"));
        }
        else {
            ParkourTime time = topTime.get(0);
            OfflinePlayer p = Bukkit.getOfflinePlayer(time.player());
            Component msg = MiniMessage.miniMessage().deserialize(String.format("<gold>Top Time: <green>%s <yellow>by %s",
                    ParallelParkour.get().getTimeString(time.time()), p.getName()));
            ParallelChat.sendParallelMessageTo(player, msg);
        }
        this.bestTime = ParallelParkour.get().getBestTimeFor(player, layout);
        if (bestTime == 0)
            ParallelChat.sendParallelMessageTo(player, MiniMessage.miniMessage().deserialize("<gold>Your Best Time: <yellow>None"));
        else
            ParallelChat.sendParallelMessageTo(player, MiniMessage.miniMessage().deserialize(String.format("<gold>Your Best Time: <green>%s",
                    ParallelParkour.get().getTimeString(bestTime))));
        ParallelChat.sendParallelMessageTo(player, Component.text("Note: You can use /endrun to end your run early.", NamedTextColor.YELLOW));
        showBossbar();
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                updateBossbarText();
            }
        }.runTaskTimer(ParallelParkour.get().getPlugin(), 0, 1);
    }

    public void end() {
        hideBossbar();
        this.endTime = System.currentTimeMillis();
        ParallelChat.sendParallelMessageTo(player, MiniMessage.miniMessage().deserialize(String.format("<gold>You finished the course in: <green>%s",
                ParallelParkour.get().getTimeString(getFinishTime()))));
        if (this.bestTime == 0 || getFinishTime() < this.bestTime) {
            ParallelChat.sendParallelMessageTo(player, Component.text("New Personal Record!", NamedTextColor.YELLOW));
            ParallelParkour.get().saveBestTimeFor(player, this);
        }
        runnable.cancel();
    }

    public void cancel(@Nullable String reason) {
        if (runnable != null) {
            runnable.cancel();
            hideBossbar();
            ParallelChat.sendParallelMessageTo(player, Component.text("You ended the parkour early. Your time will not be saved!", NamedTextColor.RED));
            if (reason != null) {
                ParallelChat.sendParallelMessageTo(player, Component.text("Reason: " + reason, NamedTextColor.RED));
            }
        }
    }

    private BossBar createBossbar() {
        return BossBar.bossBar(
                Component.text(String.format("Checkpoint %d/%d | Time: 00:00:00", 1, lastCheckpoint), NamedTextColor.GREEN),
                1f / lastCheckpoint,
                BossBar.Color.YELLOW,
                BossBar.Overlay.PROGRESS
        );
    }

    public void updateBossbarText() {
        this.bossBar.name(
                Component.text(String.format("Checkpoint %d/%d | Time: %s",
                        this.currentCheckpoint,
                        this.lastCheckpoint,
                        ParallelParkour.get().getTimeString(System.currentTimeMillis() - startTime)),
                        NamedTextColor.GREEN)
        );
    }

    public void updateCheckpoint() {
        Component msg = MiniMessage.miniMessage().deserialize(String.format("<gold>Reached checkpoint <yellow>%d <gold>in <green>%s!", this.currentCheckpoint,
                ParallelParkour.get().getTimeString(System.currentTimeMillis() - startTime)));
        ParallelChat.sendParallelMessageTo(player, msg);
        this.currentCheckpoint++;
        this.bossBar.progress((float)this.currentCheckpoint / this.lastCheckpoint);
    }

    public void showBossbar() {
        player.showBossBar(this.bossBar);
    }

    public void hideBossbar() {
        player.hideBossBar(this.bossBar);
    }

    public ParkourLayout getLayout() { return layout; }

    public int getCurrentCheckpoint() { return currentCheckpoint; }

    public int getLastCheckpoint() { return lastCheckpoint; }

    public long getFinishTime() {
        return this.endTime - this.startTime;
    }
}
