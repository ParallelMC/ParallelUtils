package parallelmc.parallelutils.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.UUID;


public class GUIManager {
    private static GUIManager Instance;

    public static GUIManager get() {
        if (Instance == null)
            Instance = new GUIManager();
        return Instance;
    }

    // a list of all players with a GUI open
    private final HashMap<UUID, GUIInventory> openGUIs = new HashMap<>();

    public void openInventoryForPlayer(Player player, GUIInventory type) {
        type.onOpen(player);
        player.openInventory(type.inventory);
        openGUIs.put(player.getUniqueId(), type);
    }

    public boolean openInventoryExists(Inventory input) {
        return openGUIs.values().stream().anyMatch(x -> x.inventory.equals(input));
    }

    public GUIInventory getOpenInventory(Player player) {
        return openGUIs.get(player.getUniqueId());
    }

    public void closeMenu(Player player) {
        openGUIs.remove(player.getUniqueId());
    }
}
