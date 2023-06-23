package parallelmc.parallelutils.modules.parallelparkour.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.parallelparkour.ParallelParkour;
import parallelmc.parallelutils.modules.parallelparkour.ParkourLayout;
import parallelmc.parallelutils.modules.parallelparkour.ParkourPlayer;

public class OnPlayerInteract implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            Block block = event.getClickedBlock();
            Player player = event.getPlayer();
            if (block != null) {
                Location pos = block.getLocation();
                ParkourPlayer pp = ParallelParkour.get().getParkourPlayer(player);
                if (block.getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE ||
                        block.getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
                    if (pp != null) {
                        int current = pp.getCurrentCheckpoint();
                        ParkourLayout layout = pp.getLayout();
                        if (!layout.allowEffects() && player.getActivePotionEffects().size() > 0) {
                            pp.cancel("Potion effects are not allowed in this course.");
                            ParallelParkour.get().endParkourFor(player);
                            return;
                        }
                        Location next = pp.getLayout().positions().get(current);
                        if (pos.equals(next)) {
                            pp.updateCheckpoint();
                            if (pp.getCurrentCheckpoint() == pp.getLastCheckpoint()) {
                                pp.end();
                                ParallelParkour.get().endParkourFor(player);
                            }
                        }
                    } else {
                        ParkourLayout layout = ParallelParkour.get().getParkourStartingAt(pos);
                        if (layout == null)
                            return;
                        if (!layout.allowEffects() && player.getActivePotionEffects().size() > 0) {
                            ParallelChat.sendParallelMessageTo(player, Component.text("Potion effects are not allowed in this course.", NamedTextColor.RED));
                            return;
                        }
                        ParallelParkour.get().startParkourFor(player, layout);
                    }
                }
            }
        }
    }
}
