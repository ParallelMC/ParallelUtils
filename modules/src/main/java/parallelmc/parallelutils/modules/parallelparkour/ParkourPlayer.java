package parallelmc.parallelutils.modules.parallelparkour;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ParkourPlayer {
    private final Player player;
    private final BossBar bossBar;
    private final long startTime;
    // TODO: implement best time
    private long bestTime;
    private int currentCheckpoint;
    private final int lastCheckpoint;
    private final ParkourLayout layout;

    private BukkitTask runnable;

    private static final SimpleDateFormat timerFormat = new SimpleDateFormat("mm:ss:SSS");

    public ParkourPlayer(Player player, ParkourLayout layout) {
        this.player = player;
        this.layout = layout;
        this.bossBar = createBossbar();
        this.currentCheckpoint = 1;
        this.lastCheckpoint = layout.positions().size();
        this.startTime = System.currentTimeMillis();
        start();
    }

    private void start() {
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
        ParallelChat.sendParallelMessageTo(player, String.format("You finished the course in: %s", getTimeString()));
        runnable.cancel();
        // TODO: handle post-finish stuff
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
                0,
                BossBar.Color.RED,
                BossBar.Overlay.PROGRESS
        );
    }

    public void updateBossbarText() {
        this.bossBar.name(
                Component.text(String.format("Checkpoint %d/%d | Time: %s",
                        this.currentCheckpoint,
                        this.lastCheckpoint,
                        getTimeString()))
        );
    }

    private String getTimeString() {
        long now = System.currentTimeMillis();
        return timerFormat.format(new Date(now - startTime));
    }

    public void updateCheckpoint() {
        ParallelChat.sendParallelMessageTo(player, String.format("Reached Checkpoint %d in %s!", this.currentCheckpoint, getTimeString()));
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
}
