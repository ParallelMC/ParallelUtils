package parallelmc.parallelutils.modules.npcshops.maggieshop;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.util.GUIManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

// TODO: abstract this to allow other npc shops
public class MaggieShop {
    private final List<ShopCharm> openCharms = new ArrayList<>();
    private final List<ShopCharm> rankedCharms = new ArrayList<>();
    private final ParallelUtils puPlugin;

    public MaggieShop(ParallelUtils puPlugin) {
        this.puPlugin = puPlugin;
        loadShopCharms();
    }

    // TODO: optimize
    private void loadShopCharms() {
        File open = new File(puPlugin.getDataFolder(), "maggie_open.yml");
        File ranked = new File(puPlugin.getDataFolder(), "maggie_ranked.yml");
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(open);
        } catch (Exception e) {
            ParallelUtils.log(Level.SEVERE, "Failed to load maggie_open.yml!");
            return;
        }
        for (String key : config.getKeys(false)) {
            String id = config.getString(key + ".charm_id");
            String name = config.getString(key + ".charm_name");
            List<String> lore = config.getStringList(key + ".lore");
            int price = config.getInt(key + ".price");
            openCharms.add(new ShopCharm(id, name, lore, price, null));
        }

        try {
            config.load(ranked);
        } catch (Exception e) {
            ParallelUtils.log(Level.SEVERE, "Failed to load maggie_ranked.yml!");
            return;
        }
        for (String key : config.getKeys(false)) {
            String id = config.getString(key + ".charm_id");
            String name = config.getString(key + ".charm_name");
            List<String> lore = config.getStringList(key + ".lore");
            int price = config.getInt(key + ".price");
            String rank = config.getString(key + ".required_rank");
            rankedCharms.add(new ShopCharm(id, name, lore, price, rank));
        }

        ParallelUtils.log(Level.WARNING, "Loaded " + openCharms.size() + " open charms and " + rankedCharms.size() + " ranked charms");
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
