package parallelmc.parallelworlds.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.GameMode;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import parallelmc.parallelworlds.registry.ParallelBlockRegistry;

import java.util.List;

public class BlockBreakPlaceListener implements Listener {

    private final ParallelBlockRegistry blockRegistry;

    public BlockBreakPlaceListener() {
        blockRegistry = ParallelBlockRegistry.getInstance();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleBlockPlace(BlockPlaceEvent event) {

        BlockState state = blockRegistry.getBlockState(ItemStack.fromBukkitCopy(event.getItemInHand()));

        if (state != null) {
            event.getBlock().setBlockData(state.createCraftBlockData());
        }
    }
}
