package parallelmc.parallelutils.modules.gamemode4_beehiveInspector.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.data.type.Beehive;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import parallelmc.parallelutils.ParallelUtils;

import java.util.*;
import java.util.logging.Level;

/**
 * This class listens for the block break event and adds the correct lore to the dropped block
 */
public class BeehiveBroken implements Listener {
    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        Block block = event.getBlock();

        // only handle if it's a beehive
        if (block.getType().equals(Material.BEEHIVE) || block.getType().equals(Material.BEE_NEST))
        {
            // if the block doesn't have any drops for some reason, then do nothing
            if (block.getDrops().isEmpty())
                return;

            ParallelUtils.log(Level.INFO, "Dropping");

            // don't drop the original block
            event.setDropItems(false);

            Location loc = block.getLocation();

            // i know this is extremely ugly but bukkit has two separate classes for beehives
            // ... for whatever reason :thonk:
            int bees = -1;
            int honey = -1;
            if (block.getState() instanceof org.bukkit.block.Beehive beehive) {
                bees = beehive.getEntityCount();
            }

            if (block.getBlockData() instanceof Beehive beeHiveData) {
                honey = beeHiveData.getHoneyLevel();
            }

            ParallelUtils.log(Level.INFO, "" + bees);
            ParallelUtils.log(Level.INFO, "" + honey);

            // more beautiful code
            // since a beehive usually only drops itself or nothing, we can hardcode this index
            ItemStack item = (ItemStack)block.getDrops().toArray()[0];
            List<Component> lore = new ArrayList<>();
            // add the lore with the hive information
            lore.add(Component.text("Bees: " + bees, NamedTextColor.GRAY));
            lore.add(Component.text("Honey Level: " + honey, NamedTextColor.GRAY));
            item.lore(lore);

            // simulate an item drop
            block.getWorld().dropItemNaturally(loc, item);
        }
    }
}
