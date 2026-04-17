package parallelmc.parallelworlds;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import org.bukkit.plugin.java.JavaPlugin;
import parallelmc.parallelworlds.events.BlockPacketListener;
import parallelmc.parallelworlds.events.BlockBreakPlaceListener;
import parallelmc.parallelworlds.registry.ParallelBlockRegistry;

public final class ParallelWorlds extends JavaPlugin {

    @Override
    public void onLoad() {
        PacketEvents.getAPI().getEventManager().registerListener(new BlockPacketListener(ParallelBlockRegistry.getFirstCustomId()), PacketListenerPriority.HIGHEST);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        getServer().getPluginManager().registerEvents(new BlockBreakPlaceListener(), this);

    }


}
