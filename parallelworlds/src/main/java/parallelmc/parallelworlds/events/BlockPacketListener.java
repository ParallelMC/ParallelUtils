package parallelmc.parallelworlds.events;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.protocol.world.chunk.impl.v_1_18.Chunk_v1_18;
import com.github.retrooper.packetevents.protocol.world.chunk.palette.DataPalette;
import com.github.retrooper.packetevents.protocol.world.chunk.palette.ListPalette;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockAction;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDeclareCommands;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import parallelmc.parallelworlds.ParallelWorldsBootstrapper;
import parallelmc.parallelworlds.registry.ParallelBlockRegistry;

import java.util.logging.Level;
import java.util.logging.Logger;

import static parallelmc.parallelworlds.ReflectionHelper.getPrivateField;


public class BlockPacketListener implements PacketListener {

    private final int firstCustomId;

    private final int defaultReplaceState;

    private final ParallelBlockRegistry registry;

    public BlockPacketListener(int firstCustomId) {
        this.firstCustomId = firstCustomId;
        this.defaultReplaceState = Block.BLOCK_STATE_REGISTRY.getId(Blocks.NOTE_BLOCK.defaultBlockState());;
        this.registry = ParallelBlockRegistry.getInstance();
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {}

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.CHUNK_DATA) {
            WrapperPlayServerChunkData packet = new WrapperPlayServerChunkData(event);

            for (BaseChunk c : packet.getColumn().getChunks()) {
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {
                        for (int z = 0; z < 16; z++) {
                            int id = c.getBlockId(x, y, z);
                            if (id >= firstCustomId) {
                                //BlockState state = Block.BLOCK_STATE_REGISTRY.byId(id);
                                //Logger.getGlobal().log(Level.WARNING, state.toString());
                                Integer replace_state = registry.getMappedState(id);
                                if (replace_state == null) {
                                    replace_state = defaultReplaceState;
                                    Logger.getGlobal().log(Level.WARNING, "Could not find mapping for id" + id);
                                }

                                c.set(x, y, z, replace_state);

                                Chunk_v1_18 chunkV118 = (Chunk_v1_18) c;

                                DataPalette palette = getPrivateField("chunkData", Chunk_v1_18.class, chunkV118, DataPalette.class);
                                ListPalette lp = (ListPalette) palette.palette;

                                int[] palatteData = getPrivateField("data", ListPalette.class, lp, int[].class);

                                for (int i = 0; i< palatteData.length; i++) {
                                    if (palatteData[i] >= firstCustomId) {
                                        palatteData[i] = replace_state;
                                    }
                                }


                                event.markForReEncode(true);
                            }
                        }
                    }
                }
            }

            int a = 0;

        } else if (event.getPacketType() == PacketType.Play.Server.BLOCK_CHANGE) {
            WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(event);

            int id = packet.getBlockId();
            if (id >= firstCustomId) {
                Integer replace_state = registry.getMappedState(id);
                if (replace_state == null) {
                    replace_state = defaultReplaceState;
                    Logger.getGlobal().log(Level.WARNING, "Could not find mapping for id" + id);
                }

                packet.setBlockID(replace_state);
                event.markForReEncode(true);
            }

            //Logger.getGlobal().log(Level.WARNING, String.valueOf(packet.getBlockId()));
        } else if (event.getPacketType() == PacketType.Play.Server.BLOCK_ACTION) {
            WrapperPlayServerBlockAction packet = new WrapperPlayServerBlockAction(event);

            int id = packet.getBlockTypeId();
            if (id >= firstCustomId) {
                Integer replace_state = registry.getMappedState(id);
                if (replace_state == null) {
                    replace_state = defaultReplaceState;
                    Logger.getGlobal().log(Level.WARNING, "Could not find mapping for id" + id);
                }

                packet.setBlockTypeId(replace_state);
                event.markForReEncode(true);
            }
        } else if (event.getPacketType() == PacketType.Play.Server.DECLARE_COMMANDS) {
            WrapperPlayServerDeclareCommands packet = new WrapperPlayServerDeclareCommands(event);

            packet.getNodes();
        }
//        } else if (event.getPacketType() != PacketType.Play.Server.ENTITY_HEAD_LOOK &&
//                event.getPacketType() != PacketType.Play.Server.ENTITY_RELATIVE_MOVE &&
//                event.getPacketType() != PacketType.Play.Server.ENTITY_RELATIVE_MOVE_AND_ROTATION &&
//        event.getPacketType() != PacketType.Play.Server.ENTITY_VELOCITY){
//            Logger.getGlobal().log(Level.WARNING, event.getPacketType().getName());
//        }
    }


}
