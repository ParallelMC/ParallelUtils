package parallelmc.parallelworlds.events;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData;

import java.util.logging.Level;
import java.util.logging.Logger;


public class BlockPacketListener implements PacketListener {

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {}

    @Override
    public void onPacketSend(PacketSendEvent event) {
//        if (event.getPacketType() == PacketType.Play.Server.CHUNK_DATA) {
//            //Health of an entity was updated!
//            WrapperPlayServerUpdateHealth packet = new WrapperPlayServerUpdateHealth(event);
//            float health = packet.getHealth();
//        }
        if (event.getPacketType() == PacketType.Play.Server.CHUNK_DATA) {
            WrapperPlayServerChunkData packet = new WrapperPlayServerChunkData(event);

            for (BaseChunk c : packet.getColumn().getChunks()) {
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {
                        for (int z = 0; z < 16; z++) {
                            if (c.getBlockId(x, y, z) == -1) {
                                WrappedBlockState state = c.get(x, y, z);
                                Logger.getGlobal().log(Level.WARNING, state.toString());
                            }
                        }
                    }
                }
            }
        } else if (event.getPacketType() == PacketType.Play.Server.BLOCK_CHANGE) {
            WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(event);

            Logger.getGlobal().log(Level.WARNING, String.valueOf(packet.getBlockId()));
        }
        //Logger.getGlobal().log(Level.WARNING, event.getPacketType().getName());
    }


}
