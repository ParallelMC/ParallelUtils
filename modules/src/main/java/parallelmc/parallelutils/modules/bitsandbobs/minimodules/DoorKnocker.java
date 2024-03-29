package parallelmc.parallelutils.modules.bitsandbobs.minimodules;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class DoorKnocker implements Listener {

    @EventHandler
    public void onDoorKnock(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                    Block block = event.getClickedBlock();

                    if (block == null) return;

                    // case for iron door
                    if (block.getType().name().contains("IRON_DOOR")) {
                        Location location = block.getLocation();
                        location.getWorld().playSound(location, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1, 1);
                        // case for all other door types (as of right now, wooden doors)
                    } else if (block.getType().name().contains("_DOOR")) {
                        Location location = block.getLocation();
                        location.getWorld().playSound(location, Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 1);
                    }
                }

            }
        }
    }

}
