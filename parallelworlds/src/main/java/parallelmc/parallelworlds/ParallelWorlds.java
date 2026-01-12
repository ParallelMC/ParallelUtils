package parallelmc.parallelworlds;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import parallelmc.parallelworlds.blocks.TestBlock;
import parallelmc.parallelworlds.events.BlockPacketListener;

import java.lang.reflect.Field;
import java.util.Map;

public final class ParallelWorlds extends JavaPlugin {

    @Override
    public void onLoad() {

        PacketEvents.getAPI().getEventManager().registerListener(new BlockPacketListener(ParallelWorldsBootstrapper.getFirstCustomId()), PacketListenerPriority.HIGHEST);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }


}
