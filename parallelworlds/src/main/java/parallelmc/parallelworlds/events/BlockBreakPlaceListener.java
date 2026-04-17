package parallelmc.parallelworlds.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
import parallelmc.parallelworlds.ParallelBlockData;
import parallelmc.parallelworlds.registry.ParallelBlockRegistry;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BlockBreakPlaceListener implements Listener {

    private final ParallelBlockRegistry blockRegistry;

    public BlockBreakPlaceListener() {
        blockRegistry = ParallelBlockRegistry.getInstance();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleBlockBreak(BlockBreakEvent event) {
        CraftBlock cb = (CraftBlock)event.getBlock();

        BlockState blockState = cb.getNMS();

        int id = Block.getId(blockState);

        ParallelBlockData blockData = blockRegistry.getBlockData(id);

        if (blockData == null) return;

        event.setDropItems(false);

        ServerLevel level = cb.getCraftWorld().getHandle();
        BlockPos pos = cb.getPosition();


        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        // TODO: Need special cases for silk touch, etc.

        List<ItemStack> drops = blockData.drops();

        for (ItemStack item : drops) {
            Block.popResource(level, pos, item);
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
