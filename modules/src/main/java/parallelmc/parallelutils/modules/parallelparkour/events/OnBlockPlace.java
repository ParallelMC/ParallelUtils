package parallelmc.parallelutils.modules.parallelparkour.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.parallelparkour.ParallelParkour;
import parallelmc.parallelutils.modules.parallelparkour.ParkourLayout;

public class OnBlockPlace implements Listener {
    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        ParkourLayout layout = ParallelParkour.get().getParkourCreation(player);
        if (layout != null) {
            int positions = layout.positions().size();
            Location loc = block.getLocation();
            if (block.getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
                layout.addLocation(loc);
                if (positions == 0) {
                    ParallelChat.sendParallelMessageTo(player, "Start point created! Place down Iron Pressure Plates for checkpoints, and another Golden Pressure Plate for the end!");
                }
                else {
                    ParallelChat.sendParallelMessageTo(player, "End point created! " + layout.name() + " is now complete!");
                    ParallelParkour.get().saveParkourCreation(player);
                }
            }
            else if (block.getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
                layout.addLocation(loc);
                ParallelChat.sendParallelMessageTo(player, "Checkpoint " + positions + " created!");
            }
        }
    }
}
