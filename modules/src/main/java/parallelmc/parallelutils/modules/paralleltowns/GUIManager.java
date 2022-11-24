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
        MainMenuInventory inv = new MainMenuInventory();
        inv.onOpen(player);
        player.openInventory(inv.inventory);
        openGUIs.put(player.getUniqueId(), inv);
    }

    public void openOptionsMenuForPlayer(Player player) {
        OptionsInventory inv = new OptionsInventory();
        player.openInventory(inv.inventory);
        openGUIs.put(player.getUniqueId(), inv);
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
