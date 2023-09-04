package parallelmc.parallelutils.modules.npcshops.maggieshop;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.charms.ParallelCharms;
import parallelmc.parallelutils.modules.charms.data.CharmOptions;
import parallelmc.parallelutils.modules.charms.data.IEffectSettings;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.helper.EncapsulatedType;
import parallelmc.parallelutils.modules.charms.helper.Types;
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
        ParallelCharms charms = (ParallelCharms)puPlugin.getModule("Charms");
        if (charms == null) {
            ParallelUtils.log(Level.SEVERE, "Failed to get Charms!");
            return;
        }

        File open = new File(puPlugin.getDataFolder(), "maggie_shop.yml");
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(open);
        } catch (Exception e) {
            ParallelUtils.log(Level.SEVERE, "Failed to load maggie_open.yml!");
            return;
        }
        for (String key : config.getKeys(false)) {
            CharmOptions charm = charms.getCharmById(key);
            if (charm == null) {
                ParallelUtils.log(Level.WARNING, "Unknown charm " + key + ", skipping!");
                continue;
            }
            String name = charm.getName();
            IEffectSettings loreSettings = charm.getEffects().get(HandlerType.APP_LORE);
            List<Component> lore = new ArrayList<>();
            if (loreSettings != null) {
                EncapsulatedType loreSetting = loreSettings.getSettings().get("lore");
                if (loreSetting.getType() == Types.STRING) {
                    String loreTotal = (String) loreSetting.getVal();

                    String[] parts = loreTotal.split("\n");

                    for (String s : parts) {
                        String part = PlaceholderAPI.setPlaceholders(null, s);
                        lore.add(MiniMessage.miniMessage().deserialize(part));
                    }
                }
            }
            if (lore.isEmpty()) {
                ParallelUtils.log(Level.WARNING, "Lore is empty for " + key + ", skipping!");
                continue;
            }
            int price = config.getInt(key + ".price");
            boolean ranked = config.getBoolean(key + ".ranked");
            if (ranked) {
                String[] permissions = charm.getAllowedPermissions();
                if (permissions.length > 0) {
                    rankedCharms.add(new ShopCharm(name, lore, price, permissions[0]));
                }
            }
            else {
                openCharms.add(new ShopCharm(name, lore, price, null));
            }
        }
        ParallelUtils.log(Level.WARNING, "Loaded " + openCharms.size() + " open charms and " + rankedCharms.size() + " ranked charms");
    }

    @Nullable
    public ShopCharm getOpenCharm(int index) {
        return openCharms.get(index);
    }

    @Nullable
    public ShopCharm getRankedCharm(int index) {
        return rankedCharms.get(index);
    }

    @NotNull
    public List<ShopCharm> getAllOpenCharms() { return openCharms; }

    @NotNull
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
