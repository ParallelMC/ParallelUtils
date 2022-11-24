package parallelmc.parallelutils.modules.paralleltowns;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import parallelmc.parallelutils.modules.paralleltowns.gui.GUIInventory;
import parallelmc.parallelutils.modules.paralleltowns.gui.MainMenuInventory;
import parallelmc.parallelutils.modules.paralleltowns.gui.OptionsInventory;

import java.util.HashMap;
import java.util.UUID;


// helper class to manage all possible menus and submenus of the town gui
public class GUIManager {
    // the town options menu
    private final Inventory townOptions = Bukkit.createInventory(null, 9, Component.text("Town Options", NamedTextColor.DARK_AQUA).decoration(TextDecoration.BOLD, true));

    // the town members menu
    private final Inventory townMembers = Bukkit.createInventory(null, 54, Component.text("Town Members", NamedTextColor.DARK_AQUA).decoration(TextDecoration.BOLD, true));

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
