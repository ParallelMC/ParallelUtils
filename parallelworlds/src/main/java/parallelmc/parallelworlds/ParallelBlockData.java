package parallelmc.parallelworlds;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ParallelBlockData(@NotNull List<ItemStack> drops, @NotNull ParticleOptions particles) {
    // TODO: Maybe replace this so you getDrops passing in an item, similar to base game
}
