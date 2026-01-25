package parallelmc.parallelworlds;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import org.bukkit.plugin.java.JavaPlugin;
import parallelmc.parallelworlds.blocks.QuicksandBlock;
import parallelmc.parallelworlds.blocks.TestBlock;
import parallelmc.parallelworlds.registry.ParallelBlockRegistry;


public class ParallelWorldsBootstrapper implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {

        ParallelBlockRegistry registry = ParallelBlockRegistry.getInstance();

        ResourceKey<Block> quicksandKey = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("parallelutils", "quicksand"));
        Block quicksandBlock = new QuicksandBlock(BlockBehaviour.Properties.of().mapColor(MapColor.SAND).strength(0.25F).sound(SoundType.SAND)
                .dynamicShape().noOcclusion().isRedstoneConductor((blockState, blockGetter, blockPos) -> false).setId(quicksandKey));
        registry.registerBlock(quicksandKey, quicksandBlock,
                Blocks.NOTE_BLOCK.getStateDefinition().any().setValue(NoteBlock.INSTRUMENT, NoteBlockInstrument.BANJO).setValue(NoteBlock.NOTE, 1), Component.literal("Quicksand"));

        registry.freeze();
    }

    @Override
    public JavaPlugin createPlugin(PluginProviderContext context) {
        return PluginBootstrap.super.createPlugin(context);
    }



}
