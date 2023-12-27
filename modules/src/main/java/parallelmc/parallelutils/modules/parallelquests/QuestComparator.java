package parallelmc.parallelutils.modules.parallelquests;

import org.bukkit.inventory.ItemStack;

import java.util.Comparator;

/** Helper class for sorting ItemStacks in the Quest GUI **/

public class QuestComparator implements Comparator<ItemStack> {
    @Override
    public int compare(ItemStack item1, ItemStack item2) {
        return Integer.compare(value(item1), value(item2));
    }

    private int value(ItemStack item) {
        return switch (item.getType()) {
            case YELLOW_CONCRETE -> 1;
            case GREEN_CONCRETE -> 2;
            case RED_CONCRETE -> 3;
            case BLACK_CONCRETE -> 4;
            default -> Integer.MAX_VALUE;
        };
    }
}
