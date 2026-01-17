package parallelmc.parallelworlds.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class QuicksandBlock extends PowderSnowBlock {

    public static final MapCodec<PowderSnowBlock> CODEC = simpleCodec(QuicksandBlock::new);

    @Override
    public MapCodec<PowderSnowBlock> codec() {
        return CODEC;
    }

    public QuicksandBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ItemStack pickupBlock(@Nullable LivingEntity owner, LevelAccessor level, BlockPos pos, BlockState state) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);

        return new ItemStack(Items.BUCKET); // TODO: Update this
    }

}
