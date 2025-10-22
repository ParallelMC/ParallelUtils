package parallelmc.parallelutils.modules.datafixerupper;

import org.bukkit.inventory.ItemStack;
import parallelmc.parallelutils.modules.parallelitems.ParallelItems;

public class UpdateItem {

    /**
     * NOTE: ALL ITEMS BEING CHECKED FOR UPDATES HERE ARE BEING UPDATED TO THEIR LATEST VERSIONS AS OF:
     * OCTOBER 7, 2025. THIS MEANS THAT CURRENTLY, ALL OLD HATS ARE BEING UPGRADED ASSUMING:
     * - NO ONE BOUGHT THEM FROM THE ONLINE STORE
     * - NONE OF THEM HAVE HELMET STATS BOUND TO THEM
     * IF EITHER OF THE ABOVE ARE TRUE, THEY ARE CONSIDERED UP TO DATE ALREADY AND WILL NOT BE CHECKED FOR AN UPDATE BELOW
     */

    private ParallelItems parallelItems;

    public UpdateItem() {
        // parallelItems
    }

    public static ItemStack updateItem() {
        // TODO: CHECK FOR OLD ITEMREGISTRYID NUMBER ITEMS, NON-PU HATS & ITEMS (NMS), OLD FISH,
        //  AND ITEMS THAT HAVE NEW FEATURES IN GENERAL
        return null;
    }
}
