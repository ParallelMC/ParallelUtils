package parallelmc.parallelworlds.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import parallelmc.parallelworlds.registry.ParallelBlockRegistry;

import java.util.List;

public class BreakBlockListener implements Listener {

    private final ParallelBlockRegistry blockRegistry;

    public BreakBlockListener() {
        blockRegistry = ParallelBlockRegistry.getInstance();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleBlockBreak(BlockBreakEvent event) {
        CraftBlock cb = (CraftBlock)event.getBlock();

        BlockState blockState = cb.getNMS();

        int id = Block.getId(blockState);

        List<ItemStack> drops = blockRegistry.getDrops(id);

        if (drops != null) {
            // This is a registered Parallel Block
            event.setDropItems(false); // Stop normal drops

            ServerLevel level = cb.getCraftWorld().getHandle();
            BlockPos pos = cb.getPosition();

            for (ItemStack item : drops) {
                Block.popResource(level, pos, item);
            }
        }
    }
}
