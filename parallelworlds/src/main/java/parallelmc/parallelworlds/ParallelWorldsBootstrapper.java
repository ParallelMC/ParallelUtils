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
import org.jetbrains.annotations.Nullable;
import parallelmc.parallelworlds.blocks.TestBlock;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ParallelWorldsBootstrapper implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {

        // This is a ridiculous hack to force internal blocks to register first
        Block dummy = Blocks.DIRT;
        Logger.getGlobal().log(Level.WARNING, dummy.toString());

        WritableRegistry<Block> blockRegistry = getWritableRegistry(Registries.BLOCK);

        if (blockRegistry == null) {
            return;
        }



        ResourceKey<Block> testBlockKey = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("parallelutils", "testblock"));
        Block block = new TestBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.5f).sound(SoundType.AMETHYST).setId(testBlockKey));

        Holder.Reference<Block> registeredBlock = blockRegistry.register(testBlockKey, block, RegistrationInfo.BUILT_IN);

        for (BlockState blockState : registeredBlock.value().getStateDefinition().getPossibleStates()) {
            Block.BLOCK_STATE_REGISTRY.add(blockState);
            blockState.initCache();
        }
    }

    @Override
    public JavaPlugin createPlugin(PluginProviderContext context) {
        return PluginBootstrap.super.createPlugin(context);
    }

    @Nullable
    public static <T, U> U getPrivateField(String fieldName, Class<T> clazz, T object, Class<U> retClazz) throws ClassCastException{
        Field field;

        try {
            field = clazz.getDeclaredField(fieldName);

            field.setAccessible(true);

            Object o = field.get(object);

            return (U) o;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
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
}
