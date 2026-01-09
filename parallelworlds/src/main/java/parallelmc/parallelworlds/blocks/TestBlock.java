package parallelmc.parallelworlds.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;

public class TestBlock extends Block {
    public static final MapCodec<TestBlock> CODEC = simpleCodec(TestBlock::new);

    public MapCodec<TestBlock> codec() {
        return CODEC;
    }

    public TestBlock(Properties properties) {
        super(properties);
    }
}
