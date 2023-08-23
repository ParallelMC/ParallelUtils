package parallelmc.parallelutils.modules.npcshops.maggieshop;

import org.bukkit.entity.Player;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.util.GUIManager;

import java.util.ArrayList;
import java.util.List;

// TODO: abstract this to allow other npc shops
public class MaggieShop {
    private final List<ShopCharm> openCharms = new ArrayList<>();
    private final List<ShopCharm> rankedCharms = new ArrayList<>();
    private final ParallelUtils puPlugin;

    public MaggieShop(ParallelUtils puPlugin) {
        this.puPlugin = puPlugin;
        loadShopCharms();
    }

    private void loadShopCharms() {

    }

    public ShopCharm getOpenCharm(int index) {
        return openCharms.get(index);
    }

    public ShopCharm getRankedCharm(int index) {
        return rankedCharms.get(index);
    }

    public List<ShopCharm> getAllOpenCharms() { return openCharms; }

    public List<ShopCharm> getAllRankedCharms() { return rankedCharms; }


    public void openShopFor(Player player) {
        GUIManager.get().openInventoryForPlayer(player, new MaggieHomeInventory(puPlugin));
    }

    public void openOpenShopFor(Player player) {
        GUIManager.get().openInventoryForPlayer(player, new MaggieOpenInventory());
    }

    public void openRankedShopFor(Player player) {
        GUIManager.get().openInventoryForPlayer(player, new MaggieRankedInventory());
    }
}
