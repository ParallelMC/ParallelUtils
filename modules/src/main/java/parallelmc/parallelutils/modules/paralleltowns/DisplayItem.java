package parallelmc.parallelutils.modules.paralleltowns;

import org.bukkit.Material;

// an item that is displayed in the town list
// simplifies having to store an ItemStack for this feature
public class DisplayItem {
    // the item's material
    private Material material;

    // if the item has any model data
    private int modelData;

    public DisplayItem(Material material) {
        this.material = material;
        this.modelData = -1;
    }

    public DisplayItem(Material material, int modelData) {
        this.material = material;
        this.modelData = modelData;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getModelData() {
        return modelData;
    }

    public void setModelData(int modelData) {
        this.modelData = modelData;
    }
}
