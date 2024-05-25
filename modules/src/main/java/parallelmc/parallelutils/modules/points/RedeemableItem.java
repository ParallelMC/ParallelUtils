package parallelmc.parallelutils.modules.points;

import org.bukkit.Material;

import java.util.List;

// an item that can be redeemed for advancement points
public class RedeemableItem {
    private final Material material;

    private final int modelData;

    private final int requiredPoints;

    private final String permission;

    private final List<String> commands;

    public RedeemableItem(Material material, int requiredPoints, string permission, List<String> commands) {
        this.material = material;
        this.modelData = -1;
        this.requiredPoints = requiredPoints;
        this.permission = permission;
        this.commands = commands;
    }

    public RedeemableItem(Material material, int requiredPoints, string permission, int modelData, List<String> commands) {
        this.material = material;
        this.modelData = modelData;
        this.requiredPoints = requiredPoints;
        this.permission = permission;
        this.commands = commands;
    }

    public Material getMaterial() {
        return material;
    }

    public int getModelData() {
        return modelData;
    }

    public int getRequiredPoints() { return requiredPoints; }

    public string getPermission() { return permission; }

    public List<String> getCommands() { return commands; }
}
