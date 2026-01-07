package parallelmc.parallelutils;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Map;

public class ParallelUtilsBootstrapper implements PluginBootstrap {
    @Override
    public void bootstrap(BootstrapContext context) {
        MappedRegistry<WritableRegistry<?>> writable_registry = (MappedRegistry<WritableRegistry<?>>) getPrivateField("WRITABLE_REGISTRY", BuiltInRegistries.class, null, WritableRegistry.class);

        Map<WritableRegistry<?>, Holder.Reference<WritableRegistry<?>>> byValue = getPrivateField("byValue", MappedRegistry.class, writable_registry, Map.class);

        WritableRegistry<Block> blockRegistry = null;

        for (WritableRegistry i : byValue.keySet()) {
            if (String.valueOf(i.key().location()).equals(String.valueOf(Registries.BLOCK.location()))) {
                blockRegistry = i;
                break;
            }
        }

        if (blockRegistry == null) {
            return;
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
}
