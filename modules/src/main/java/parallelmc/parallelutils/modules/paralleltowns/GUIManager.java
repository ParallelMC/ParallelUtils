package parallelmc.parallelutils.modules.paralleltowns;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import parallelmc.parallelutils.modules.paralleltowns.gui.GUIInventory;
import parallelmc.parallelutils.modules.paralleltowns.gui.MainMenuInventory;
import parallelmc.parallelutils.modules.paralleltowns.gui.OptionsInventory;

import java.util.HashMap;
import java.util.UUID;


// helper class to manage all possible menus and submenus of the town gui
public class GUIManager {
    // a list of all players with the towns UI open
    private final HashMap<UUID, GUIInventory> openGUIs = new HashMap<>();

    public void openMainMenuForPlayer(Player player) {
        openInventoryForPlayer(player, new MainMenuInventory());
    }

    public void openOptionsMenuForPlayer(Player player) {
        openInventoryForPlayer(player, new OptionsInventory());
    }

    private void openInventoryForPlayer(Player player, GUIInventory type) {
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
