package parallelmc.parallelworlds.events;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.protocol.world.chunk.impl.v_1_18.Chunk_v1_18;
import com.github.retrooper.packetevents.protocol.world.chunk.palette.DataPalette;
import com.github.retrooper.packetevents.protocol.world.chunk.palette.ListPalette;
import com.github.retrooper.packetevents.protocol.world.chunk.palette.MapPalette;
import com.github.retrooper.packetevents.protocol.world.chunk.palette.SingletonPalette;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Server;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import parallelmc.parallelworlds.ParallelWorldsBootstrapper;
import parallelmc.parallelworlds.registry.ParallelBlockRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static parallelmc.parallelworlds.ReflectionHelper.getPrivateField;


public class BlockPacketListener implements PacketListener {

    private final int firstCustomId;

    private final int noteblockStart;
    private final int noteblockEnd; // Last valid noteblock state
    private final int defaultReplaceState;

    private final ParallelBlockRegistry registry;

    public BlockPacketListener(int firstCustomId) {
        this.firstCustomId = firstCustomId;
        this.noteblockStart = Block.BLOCK_STATE_REGISTRY.getId(Blocks.NOTE_BLOCK.defaultBlockState());
        this.noteblockEnd = this.noteblockStart + Blocks.NOTE_BLOCK.getStateDefinition().getPossibleStates().size() -1;
        this.defaultReplaceState = noteblockStart;
        this.registry = ParallelBlockRegistry.getInstance();
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {

        PacketTypeCommon type = event.getPacketType();

        
        if (type == PacketType.Play.Server.CHUNK_DATA || type == PacketType.Play.Server.MAP_CHUNK_BULK) {

            BaseChunk[][] chunks;
            if (type == PacketType.Play.Server.CHUNK_DATA) {
                WrapperPlayServerChunkData packet = new WrapperPlayServerChunkData(event);

                chunks = new BaseChunk[][] {packet.getColumn().getChunks()};
            } else {
                WrapperPlayServerChunkDataBulk packet = new WrapperPlayServerChunkDataBulk(event);
                chunks = packet.getChunks();
            }

            for (BaseChunk[] arr : chunks) {
                for (BaseChunk c : arr) {
                    for (int x = 0; x < 16; x++) {
                        for (int y = 0; y < 16; y++) {
                            for (int z = 0; z < 16; z++) {
                                int id = c.getBlockId(x, y, z);
                                Integer replace_state;
                                if (id >= firstCustomId) {
                                    //BlockState state = Block.BLOCK_STATE_REGISTRY.byId(id);
                                    //Logger.getGlobal().log(Level.WARNING, state.toString());
                                    replace_state = registry.getMappedState(id);
                                    if (replace_state == null) {
                                        replace_state = defaultReplaceState;
                                        Logger.getGlobal().log(Level.WARNING, "Could not find mapping for id" + id);
                                    }
                                } else if (id >= noteblockStart && id <= noteblockEnd) {
                                    replace_state = noteblockStart;
                                } else {
                                    continue;
                                }
                                c.set(x, y, z, replace_state);

                                event.markForReEncode(true);
                            }
                        }
                    }

                    Chunk_v1_18 chunkV118 = (Chunk_v1_18) c;

                    DataPalette palette = getPrivateField("chunkData", Chunk_v1_18.class, chunkV118, DataPalette.class);
                    if (palette.palette instanceof ListPalette lp) {
                        int[] palatteData = getPrivateField("data", ListPalette.class, lp, int[].class);

                        for (int i = 0; i < palatteData.length; i++) {
                            int id = palatteData[i];
                            if (id >= firstCustomId) {
                                Integer replace_state = registry.getMappedState(id);
                                if (replace_state == null) {
                                    replace_state = defaultReplaceState;
                                    Logger.getGlobal().log(Level.WARNING, "Could not find mapping for id" + id);
                                }
                                palatteData[i] = replace_state;
                                event.markForReEncode(true);
                            }
                        }
                    } else if (palette.palette instanceof SingletonPalette sp) {
                        int id = sp.idToState(0);

                        if (id >= firstCustomId) {
                            Integer replace_state = registry.getMappedState(id);
                            if (replace_state == null) {
                                replace_state = defaultReplaceState;
                                Logger.getGlobal().log(Level.WARNING, "Could not find mapping for id" + id);
                            }
                            palette.palette = new SingletonPalette(replace_state);
                            event.markForReEncode(true);
                        }
                    } else if (palette.palette instanceof MapPalette mp) {
                        int[] idToState = getPrivateField("idToState", MapPalette.class, mp, int[].class);
                        HashMap<Object, Integer> stateToId = getPrivateField("stateToId", MapPalette.class, mp, HashMap.class);

                        // NOTE: This is not the most efficient, but it was an easy solution
                        for (Object state : Map.copyOf(stateToId).keySet()) {
                            Integer st = (Integer) state;
                            int id = stateToId.get(state);

                            if (st >= firstCustomId) {
                                Integer replace_state = registry.getMappedState(st);
                                if (replace_state == null) {
                                    replace_state = defaultReplaceState;
                                    Logger.getGlobal().log(Level.WARNING, "Could not find mapping for id" + st);
                                }

                                stateToId.remove(state);
                                stateToId.put(replace_state, id);
                                idToState[id] = replace_state;

                                event.markForReEncode(true);
                            }
                        }


                    }
                }
            }
        } else if (type == PacketType.Play.Server.BLOCK_CHANGE) {
            WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(event);

            int id = packet.getBlockId();
            Integer replace_state;
            if (id >= firstCustomId) {
                replace_state = registry.getMappedState(id);
                if (replace_state == null) {
                    replace_state = defaultReplaceState;
                    Logger.getGlobal().log(Level.WARNING, "Could not find mapping for id" + id);
                }
            } else if (id >= noteblockStart && id <= noteblockEnd) {
                replace_state = noteblockStart;
            } else {
                return;
            }

            packet.setBlockID(replace_state);
            event.markForReEncode(true);

            //Logger.getGlobal().log(Level.WARNING, String.valueOf(packet.getBlockId()));
        } else if (type == PacketType.Play.Server.MULTI_BLOCK_CHANGE) {
            WrapperPlayServerMultiBlockChange packet = new WrapperPlayServerMultiBlockChange(event);

            for (WrapperPlayServerMultiBlockChange.EncodedBlock block : packet.getBlocks()) {
                int id = block.getBlockId();

                Integer replace_state;
                if (id >= firstCustomId) {
                    replace_state = registry.getMappedState(id);
                    if (replace_state == null) {
                        replace_state = defaultReplaceState;
                        Logger.getGlobal().log(Level.WARNING, "Could not find mapping for id" + id);
                    }
                } else if (id >= noteblockStart && id <= noteblockEnd) {
                    replace_state = noteblockStart;
                } else {
                    continue;
                }

                block.setBlockId(replace_state);
                event.markForReEncode(true);
            }

        } else if (type == PacketType.Play.Server.BLOCK_ACTION) {
            WrapperPlayServerBlockAction packet = new WrapperPlayServerBlockAction(event);

            int id = packet.getBlockTypeId();
            Integer replace_state;
            if (id >= firstCustomId) {
                replace_state = registry.getMappedState(id);
            }  else if (id >= noteblockStart && id <= noteblockEnd) {
                replace_state = noteblockStart;
            } else {
                return;
            }


            if (replace_state == null) {
                replace_state = defaultReplaceState;
                Logger.getGlobal().log(Level.WARNING, "Could not find mapping for id" + id);
            }

            packet.setBlockTypeId(replace_state);
            event.markForReEncode(true);
        } else if (type == PacketType.Play.Server.DECLARE_COMMANDS) {
            WrapperPlayServerDeclareCommands packet = new WrapperPlayServerDeclareCommands(event);

            packet.getNodes();

        } else if (type != PacketType.Play.Server.ENTITY_HEAD_LOOK &&
                type != PacketType.Play.Server.ENTITY_RELATIVE_MOVE &&
                type != PacketType.Play.Server.ENTITY_RELATIVE_MOVE_AND_ROTATION &&
        type != PacketType.Play.Server.ENTITY_VELOCITY &&
        type != PacketType.Play.Server.ENTITY_POSITION_SYNC){
            //Logger.getGlobal().log(Level.WARNING, type.getName());
        }
    }


}
