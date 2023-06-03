package parallelmc.parallelutils.modules.parallelparkour;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

public class ParkourPlayer {
    private final Player player;
    private final BossBar bossBar;
    private final long startTime;
    private long endTime;
    // TODO: implement best time
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
        // TODO: these database calls are really slow. Probably want to cache times on startup
        ParallelChat.sendParallelMessageTo(player, "Starting parkour course: " + layout.name());
        var topTime = ParallelParkour.get().getTopTimesFor(layout.name(), 1);
        if (topTime.size() == 0) {
            ParallelChat.sendParallelMessageTo(player, "Top Time: None");
        }
        else {
            ParkourTime time = topTime.get(0);
            OfflinePlayer p = Bukkit.getOfflinePlayer(time.player());
            ParallelChat.sendParallelMessageTo(player, String.format("Top Time: %s by %s",
                    ParallelParkour.get().getTimeString(time.time()), p.getName()));
        }
        this.bestTime = ParallelParkour.get().getBestTimeFor(player, layout);
        if (bestTime == 0)
            ParallelChat.sendParallelMessageTo(player, "Your Best Time: None");
        else
            ParallelChat.sendParallelMessageTo(player, String.format("Your Best Time: %s",
                    ParallelParkour.get().getTimeString(bestTime)));
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
        ParallelChat.sendParallelMessageTo(player, String.format("You finished the course in: %s",
                ParallelParkour.get().getTimeString(getFinishTime())));
        if (this.bestTime == 0 || getFinishTime() < this.bestTime) {
            ParallelChat.sendParallelMessageTo(player, "New Personal Record!");
            ParallelParkour.get().saveBestTimeFor(player, this);
        }
        runnable.cancel();
    }

    public void cancel() {
        if (runnable != null) {
            runnable.cancel();
            hideBossbar();
            ParallelChat.sendParallelMessageTo(player, "You ended the parkour early. Your time will not be saved!");
        }
    }

    private BossBar createBossbar() {
        return BossBar.bossBar(
                Component.text(String.format("Checkpoint %d/%d | Time: 00:00:00", 1, lastCheckpoint)),
                1f / lastCheckpoint,
                BossBar.Color.RED,
                BossBar.Overlay.PROGRESS
        );
    }

    public void updateBossbarText() {
        this.bossBar.name(
                Component.text(String.format("Checkpoint %d/%d | Time: %s",
                        this.currentCheckpoint,
                        this.lastCheckpoint,
                        ParallelParkour.get().getTimeString(System.currentTimeMillis() - startTime)))
        );
    }

    public void updateCheckpoint() {
        ParallelChat.sendParallelMessageTo(player, String.format("Reached Checkpoint %d in %s!", this.currentCheckpoint,
                ParallelParkour.get().getTimeString(System.currentTimeMillis() - startTime)));
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
