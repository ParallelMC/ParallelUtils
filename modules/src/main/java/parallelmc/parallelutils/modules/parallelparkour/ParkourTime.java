package parallelmc.parallelutils.modules.parallelparkour;

import java.util.UUID;

/* Helper record to hold times on a parkour course */
public record ParkourTime(UUID player, String course, long time) { }
