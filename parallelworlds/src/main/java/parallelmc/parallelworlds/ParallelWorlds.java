package parallelmc.parallelworlds;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import org.bukkit.plugin.java.JavaPlugin;
import parallelmc.parallelworlds.events.BlockPacketListener;

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
