package parallelmc.parallelutils.modules.paralleltowns;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import parallelmc.parallelutils.modules.paralleltowns.gui.*;

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

    public void openMembersMenuForPlayer(Player player) {
        openInventoryForPlayer(player, new MembersInventory());
    }

    public void openMemberOptionsMenuForPlayer(Player player, OfflinePlayer edit) {
        openInventoryForPlayer(player, new MemberOptionsInventory(edit));
    }

    public void openTownConfirmationForPlayer(Player player, Town town, ConfirmationAction action) {
        openInventoryForPlayer(player, new ConfirmationInventory(town, action));
    }

    public void openTownMemberConfirmationForPlayer(Player player, Town town, OfflinePlayer townMember, ConfirmationAction action) {
        openInventoryForPlayer(player, new ConfirmationInventory(town, townMember, action));
    }

    public void openTownListMenuForPlayer(Player player) {
        openInventoryForPlayer(player, new TownListInventory());
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
