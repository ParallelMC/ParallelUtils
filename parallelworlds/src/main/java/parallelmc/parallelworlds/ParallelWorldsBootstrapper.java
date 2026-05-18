package parallelmc.parallelworlds;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import org.bukkit.plugin.java.JavaPlugin;
import parallelmc.parallelworlds.blocks.QuicksandBlock;
import parallelmc.parallelworlds.registry.ParallelBlockRegistry;

import java.util.function.Function;


public class ParallelWorldsBootstrapper implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {

        ParallelBlockRegistry registry = ParallelBlockRegistry.getInstance();

        if (registry == null) throw new RuntimeException("ParallelBlockRegistry is null!");

        register(registry, "polished_sandstone", Block::new,
                BlockBehaviour.Properties.of().mapColor(MapColor.SAND).requiresCorrectToolForDrops().strength(0.8F)
                        .sound(SoundType.STONE),
                ParallelBlockRegistry.BlockType.FULL_BLOCK,
                Blocks.SANDSTONE.defaultBlockState(),
                Component.literal("Polished Sandstone").setStyle(Style.EMPTY));

        register(registry, "quicksand", QuicksandBlock::new,
                BlockBehaviour.Properties.of().mapColor(MapColor.SAND).strength(0.25F)
                        .sound(SoundType.SAND).dynamicShape().noOcclusion()
                        .isRedstoneConductor((blockState, blockGetter, blockPos) -> false),
                ParallelBlockRegistry.BlockType.FULL_BLOCK,
                Blocks.SAND.defaultBlockState(),
                Component.literal("Quicksand").setStyle(Style.EMPTY));

        registry.freeze();
    }

    @Override
    public JavaPlugin createPlugin(PluginProviderContext context) {
        return PluginBootstrap.super.createPlugin(context);
    }

    private static void register(ParallelBlockRegistry registry, String name,
                                 Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties,
                                 ParallelBlockRegistry.BlockType targetType, BlockState particleState, Component itemName) {
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("parallelutils", name));
        Block block = factory.apply(properties.setId(blockKey));
        registry.registerBlock(blockKey, block, targetType, particleState, itemName);
    }


}
