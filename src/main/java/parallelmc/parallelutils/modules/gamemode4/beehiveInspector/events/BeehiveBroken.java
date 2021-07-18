package parallelmc.parallelutils.modules.gamemode4.beehiveInspector.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.*;

public class BeehiveBroken implements Listener {
    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        Block block = event.getBlock();

        // only handle if it's a beehive
        if (block.getBlockData().getMaterial().equals(Material.BEEHIVE))
        {
            // if the block doesn't have any drops for some reason, like in creative, then do nothing
            if (block.getDrops().isEmpty())
                return;

            // don't drop the original block
            event.setDropItems(false);

            Location loc = block.getLocation();

            // i know this is extremely ugly but bukkit has two separate classes for beehives
            // ... for whatever reason :thonk:
            int bees = ((org.bukkit.block.Beehive)block).getEntityCount();
            int honey = ((org.bukkit.block.data.type.Beehive)block).getHoneyLevel();

            // more beautiful code
            // since a beehive usually only drops itself or nothing, we can hardcode this index
            ItemStack item = (ItemStack)block.getDrops().toArray()[0];
            List<Component> lore = new ArrayList();
            // add the lore with the hive information
            lore.add(Component.text("Bees: " + bees, NamedTextColor.GRAY));
            lore.add(Component.text("Honey Level: " + honey, NamedTextColor.GRAY));
            item.lore(lore);

            // simulate an item drop
            block.getWorld().dropItemNaturally(loc, item);
        }
    }
}
