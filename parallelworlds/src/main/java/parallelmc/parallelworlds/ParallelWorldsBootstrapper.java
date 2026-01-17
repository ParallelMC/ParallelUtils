package parallelmc.parallelworlds;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import parallelmc.parallelworlds.blocks.QuicksandBlock;
import parallelmc.parallelworlds.blocks.TestBlock;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static parallelmc.parallelworlds.ReflectionHelper.getPrivateField;

public class ParallelWorldsBootstrapper implements PluginBootstrap {

    private static int firstCustomId = 0;

    @Override
    public void bootstrap(BootstrapContext context) {

        // This is a ridiculous hack to force internal blocks to register first
        Block dummy = Blocks.DIRT;
        Logger.getGlobal().log(Level.WARNING, dummy.toString());

        firstCustomId = Block.BLOCK_STATE_REGISTRY.size();


        WritableRegistry<Block> blockRegistry = getWritableRegistry(Registries.BLOCK);

        if (blockRegistry == null) {
            return;
        }


        ResourceKey<Block> testBlockKey = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("parallelutils", "testblock"));
        Block testBlock = new TestBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.5f).sound(SoundType.AMETHYST).setId(testBlockKey));
        registerBlock(blockRegistry, testBlockKey, testBlock);

        ResourceKey<Block> quicksandKey = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("parallelutils", "quicksand"));
        Block quicksandBlock = new QuicksandBlock(BlockBehaviour.Properties.of().mapColor(MapColor.SAND).strength(0.25F).sound(SoundType.SAND)
                .dynamicShape().noOcclusion().isRedstoneConductor((blockState, blockGetter, blockPos) -> false).setId(quicksandKey));
        registerBlock(blockRegistry, quicksandKey, quicksandBlock);
    }

    @Override
    public JavaPlugin createPlugin(PluginProviderContext context) {
        return PluginBootstrap.super.createPlugin(context);
    }



    // This is private since it can ONLY be run during bootstrap or things break real bad
    @Nullable
    private static <T> WritableRegistry<T> getWritableRegistry(ResourceKey<Registry<T>> registryKey) {

        // Get the global WRITABLE_REGISTRY registry
        MappedRegistry<WritableRegistry<?>> writable_registry = (MappedRegistry<WritableRegistry<?>>) getPrivateField("WRITABLE_REGISTRY", BuiltInRegistries.class, null, WritableRegistry.class);

        // Search registry byValue (since byKey doesn't work for whatever reason...)
        Map<WritableRegistry<?>, Holder.Reference<WritableRegistry<?>>> byValue = getPrivateField("byValue", MappedRegistry.class, writable_registry, Map.class);

        for (WritableRegistry i : byValue.keySet()) {
            if (String.valueOf(i.key().identifier()).equals(String.valueOf(registryKey.identifier()))) {
                return i;
            }
        }

        return null;
    }

    private static void registerBlock(WritableRegistry<Block> blockRegistry, ResourceKey<@NotNull Block> key, Block block) {
        Holder.Reference<Block> registeredBlock = blockRegistry.register(key, block, RegistrationInfo.BUILT_IN);

        for (BlockState blockState : registeredBlock.value().getStateDefinition().getPossibleStates()) {
            Block.BLOCK_STATE_REGISTRY.add(blockState);
            blockState.initCache();
        }
    }

    public static int getFirstCustomId() {
        return firstCustomId;
    }
}
