package parallelmc.parallelworlds.registry;

import net.minecraft.core.*;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.functions.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static parallelmc.parallelworlds.ReflectionHelper.getPrivateField;

public class ParallelBlockRegistry {
    
    private static ParallelBlockRegistry instance = null;

    private static int firstCustomId = 0;
    private final WritableRegistry<Block> blockRegistry;
    private final HashMap<Integer, Integer> stateMap;

    // NOTE: If the resource pack can be automatically generated and served
    // this can be converted into map of BlockType -> Queue<Integer> since we can choose values entirely server side
    private final HashMap<BlockState, Integer> availableStates;

    private final HashMap<Integer, List<ItemStack>> dropMap;
    private final HashMap<Integer, BlockState> placeMap;

    private boolean frozen = false;
    
    private ParallelBlockRegistry() throws RuntimeException, NoSuchMethodException {
        // This is a ridiculous hack to force internal blocks to register first
        Block dummy = Blocks.DIRT;
        Logger.getGlobal().log(Level.INFO, dummy.toString());

        firstCustomId = Block.BLOCK_STATE_REGISTRY.size();

        blockRegistry = getWritableRegistry(Registries.BLOCK);

        if (blockRegistry == null) {
            throw new RuntimeException("Cannot find block registry");
        }

        stateMap = new HashMap<>();
        availableStates = new HashMap<>();
        dropMap = new HashMap<>();
        placeMap = new HashMap<>();

        // TODO: Fill available states
        addFullBlock(Blocks.NOTE_BLOCK);
        addFullBlock(Blocks.SCULK_SENSOR);
        addFullBlock(Blocks.CALIBRATED_SCULK_SENSOR);

    }

    private void addFullBlock(Block block) {

        for (BlockState state : block.getStateDefinition().getPossibleStates()) {
            if (state.equals(block.defaultBlockState())) continue; // This is the one actually used for rendering

            if (availableStates.containsKey(state)) throw new IllegalStateException("Cannot add state that already exists");

            availableStates.put(state, Block.BLOCK_STATE_REGISTRY.getId(state));
        }

    }

    public static ParallelBlockRegistry getInstance() {
        if (instance == null) {
            try {
                instance = new ParallelBlockRegistry();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                return null;
            }
        }

        return instance;
    }

    // Call this at end of bootstrap to prevent adding new blocks to the registry
    public void freeze() {
        frozen = true;
    }


    // This is private since it can ONLY be run during bootstrap or things break real bad
    @Nullable
    private static <T> WritableRegistry<@NotNull T> getWritableRegistry(ResourceKey<@NotNull Registry<@NotNull T>> registryKey) {

        // Get the global WRITABLE_REGISTRY registry
        MappedRegistry<@NotNull WritableRegistry<?>> writable_registry = (MappedRegistry<WritableRegistry<?>>) getPrivateField("WRITABLE_REGISTRY", BuiltInRegistries.class, null, WritableRegistry.class);

        // Search registry byValue (since byKey doesn't work for whatever reason...)
        Map<WritableRegistry<?>, Holder.Reference<@NotNull WritableRegistry<?>>> byValue = getPrivateField("byValue", MappedRegistry.class, writable_registry, Map.class);

        for (WritableRegistry i : byValue.keySet()) {
            if (String.valueOf(i.key().identifier()).equals(String.valueOf(registryKey.identifier()))) {
                return i;
            }
        }

        return null;
    }

    public boolean registerBlock(ResourceKey<@NotNull Block> key, Block block, BlockState targetBlockstate, Component name) {
        ItemStack stack = Items.BARRIER.getDefaultInstance();

        stack.applyComponentsAndValidate(
                DataComponentPatch.builder()
                        .set(TypedDataComponent.createUnchecked(DataComponents.ITEM_MODEL, key.identifier()))
                        .set(TypedDataComponent.createUnchecked(DataComponents.ITEM_NAME, name)).build());

        return registerBlock(key, block, targetBlockstate, List.of(stack), stack);
    }

    public boolean registerBlock(ResourceKey<@NotNull Block> key, Block block, BlockState targetBlockstate, List<ItemStack> item, ItemStack placeBlock) {
        if (frozen) return false;

        if (!availableStates.containsKey(targetBlockstate)) throw new IllegalStateException("Block state is already used or does not exist");

        Holder.Reference<Block> registeredBlock = blockRegistry.register(key, block, RegistrationInfo.BUILT_IN);

        // TODO: This technically doesn't work since several blockstates are mapped to a single one, but it's easier for now
        for (BlockState blockState : registeredBlock.value().getStateDefinition().getPossibleStates()) {

            Integer stateId = availableStates.remove(targetBlockstate);

            stateMap.put(Block.BLOCK_STATE_REGISTRY.size(), stateId); // Map the new block state to an unused state
            dropMap.put(Block.BLOCK_STATE_REGISTRY.size(), item);
            placeMap.put(ItemStack.hashItemAndComponents(placeBlock), blockState);

            Block.BLOCK_STATE_REGISTRY.add(blockState);

            blockState.initCache();
        }

        return true;
    }

    public static int getFirstCustomId() {
        return firstCustomId;
    }

    @Nullable
    public Integer getMappedState(int state) {
        return stateMap.get(state);
    }

    @Nullable
    public List<ItemStack> getDrops(int state) {
        return dropMap.get(state);
    }

    @Nullable
    public BlockState getBlockState(@NotNull ItemStack item) {
        return placeMap.get(ItemStack.hashItemAndComponents(item));
    }
}
