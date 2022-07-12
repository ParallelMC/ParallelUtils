package parallelmc.parallelutils.modules.chestshops;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.chestshops.events.*;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;

public class ChestShops implements ParallelModule {

    private final HashMap<UUID, HashSet<Shop>> chestShops = new HashMap<>();
    private final HashMap<UUID, Inventory> shopPreviews = new HashMap<>();
    private final HashMap<UUID, ShopperData> shoppingPlayers = new HashMap<>();

    private Parallelutils puPlugin;

    private static ChestShops INSTANCE;

    // 26 stacks of 64 diamonds
    private static final int MAX_DIAMONDS = 1664;

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

        if (plugin == null) {
            Parallelutils.log(Level.SEVERE, "Unable to enable ChestShops. Plugin " + Constants.PLUGIN_NAME
                    + " does not exist!");
            return;
        }

        this.puPlugin = (Parallelutils) plugin;

        if (!puPlugin.registerModule("ChestShops", this)) {
            Parallelutils.log(Level.SEVERE, "Unable to register module ChestShops! " +
                    "Module may already be registered. Quitting...");
            return;
        }

        try (Connection conn = puPlugin.getDbConn()) {
            if (conn == null) throw new SQLException("Unable to establish connection!");
            Statement statement = conn.createStatement();
            statement.setQueryTimeout(15);
            statement.execute("""
                    create table if not exists ChestShops
                    (
                        UUID        varchar(36) not null,
                        World       varchar(32) not null,
                        ChestX      int         not null,
                        ChestY      int         not null,
                        ChestZ      int         not null,
                        SignX       int         not null,
                        SignY       int         not null,
                        SignZ       int         not null,
                        Item        varchar(50) not null,
                        SellAmt     int         not null,
                        BuyAmt      int         not null
                    );""");
            conn.commit();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // load existing player config
        try (Connection conn = puPlugin.getDbConn()) {
            if (conn == null) throw new SQLException("Unable to establish connection!");
            Statement statement = conn.createStatement();
            statement.setQueryTimeout(60);
            ResultSet results = statement.executeQuery("select * from ChestShops");
            while (results.next()) {
                UUID uuid = UUID.fromString(results.getString("UUID"));
                World world = puPlugin.getServer().getWorld(results.getString("World"));
                Location chestLoc = new Location(world, results.getInt("ChestX"), results.getInt("ChestY"), results.getInt("ChestZ"));
                Location signLoc = new Location(world, results.getInt("SignX"), results.getInt("SignY"), results.getInt("SignZ"));
                Material item = Material.getMaterial(results.getString("Item"));
                int sellAmt = results.getInt("SellAmt");
                int buyAmt = results.getInt("BuyAmt");
                addShop(uuid, chestLoc, signLoc, item, sellAmt, buyAmt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        manager.registerEvents(new OnSignText(), puPlugin);
        manager.registerEvents(new OnClickBlock(), puPlugin);
        manager.registerEvents(new OnBreakShop(), puPlugin);
        manager.registerEvents(new OnPreviewInteract(), puPlugin);
        manager.registerEvents(new OnShopInteract(), puPlugin);

        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        try (Connection conn = puPlugin.getDbConn()) {
            if (conn == null) throw new SQLException("Unable to establish connection!");
            // allow duplicate uuids since players can have multiple shops
            PreparedStatement statement = conn.prepareStatement("INSERT INTO ChestShops (UUID, World, ChestX, ChestY, ChestZ, SignX, SignY, SignZ, Item, SellAmt, BuyAmt) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setQueryTimeout(30);
            this.chestShops.forEach((u, o) -> {
                o.forEach((s) -> {
                    try {
                        statement.setString(1, u.toString());
                        statement.setString(2, s.chestPos().getWorld().getName());
                        statement.setInt(3, s.chestPos().getBlockX());
                        statement.setInt(4, s.chestPos().getBlockY());
                        statement.setInt(5, s.chestPos().getBlockZ());
                        statement.setInt(6, s.signPos().getBlockX());
                        statement.setInt(7, s.signPos().getBlockY());
                        statement.setInt(8, s.signPos().getBlockZ());
                        statement.setString(9, s.item().toString());
                        statement.setInt(10, s.sellAmt());
                        statement.setInt(11, s.buyAmt());
                        statement.addBatch();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            });
            statement.executeBatch();
            conn.commit();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addShop(UUID owner, Location chestPos, Location signPos, Material item, int sellAmt, int buyAmt) {
        Shop shop = new Shop(owner, chestPos, signPos, item, sellAmt, buyAmt);
        if (chestShops.containsKey(owner)) {
            HashSet<Shop> shops = chestShops.get(owner);
            shops.add(shop);
        }
        else {
            HashSet<Shop> shops = new HashSet<>();
            shops.add(shop);
            chestShops.put(owner, shops);
        }
        Parallelutils.log(Level.WARNING, "Added chest shop: \nChestPos: " + chestPos + "\nSignPos: " + signPos + "\nMaterial: " + item);
    }

    public void removeShop(UUID owner, Location chestPos) {
        HashSet<Shop> shops = chestShops.get(owner);
        shops.removeIf(x -> x.chestPos().equals(chestPos));
        if (shops.size() == 0)
            chestShops.remove(owner);
    }

    // ugly but no real better way to do it without storing multiple instances of shops which is a recipe for disaster

    public Shop getShopFromSignPos(Location signPos) {
        Shop out = null;
        for (HashSet<Shop> s : chestShops.values()) {
            Optional<Shop> r = s.stream().filter(x -> x.signPos().equals(signPos)).findFirst();
            if (r.isPresent()) {
                out = r.get();
                break;
            }
        }
        return out;
    }

    public Shop getShopFromChestPos(Location chestPos) {
        Shop out = null;
        for (HashSet<Shop> s : chestShops.values()) {
            Optional<Shop> r = s.stream().filter(x -> x.chestPos().equals(chestPos)).findFirst();
            if (r.isPresent()) {
                out = r.get();
                break;
            }
        }
        return out;
    }


    public ShopResult attemptPurchase(Player player, Shop shop, Chest chest, ItemStack diamonds) {
        Inventory inv = chest.getBlockInventory();
        if (player.getInventory().firstEmpty() == -1) {
            return ShopResult.INVENTORY_FULL;
        }
        if (diamonds == null || diamonds.getType() != Material.DIAMOND) {
            return ShopResult.NO_DIAMONDS;
        }
        if (diamonds.getAmount() < shop.buyAmt()) {
            return ShopResult.INSUFFICIENT_FUNDS;
        }
        if (inv.containsAtLeast(new ItemStack(Material.DIAMOND), MAX_DIAMONDS - shop.buyAmt())) {
            return ShopResult.SHOP_FULL;
        }

        HashMap<Integer, ? extends ItemStack> items = inv.all(shop.item());
        Inventory shopping = Bukkit.createInventory(null, InventoryType.CHEST, Component.text("ChestShop (Click to Buy)"));
        int itemAmt = 0;
        // would rather use two for loops than AtomicInteger tee hee
        for (ItemStack i : items.values()) {
            itemAmt += i.getAmount();
        }
        items.forEach(shopping::setItem);
        if (inv.getContents().length == 0 || items.size() == 0 || itemAmt < shop.sellAmt()) {
            return ShopResult.SHOP_EMPTY;
        }
        player.openInventory(shopping);
        shoppingPlayers.put(player.getUniqueId(), new ShopperData(shopping, inv, shop, diamonds));
        return ShopResult.SUCCESS;
    }

    public void openShopPreview(Player player, Shop shop, Inventory chest) {
        Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST, Component.text("ChestShop Preview"));
        HashMap<Integer, ? extends ItemStack> items = chest.all(shop.item());
        items.forEach(inv::setItem);
        player.openInventory(inv);
        shopPreviews.put(player.getUniqueId(), inv);
    }

    public void closeShopPreview(Player player) {
        shopPreviews.remove(player.getUniqueId());
    }

    public void stopShopping(Player player) { shoppingPlayers.remove(player.getUniqueId()); }

    public Inventory getPreviewInventory(Player player) {
        return shopPreviews.get(player.getUniqueId());
    }

    public ShopperData getShoppingData(Player player) { return shoppingPlayers.get(player.getUniqueId()); }

    public boolean previewInventoryExists(Inventory inv) {
        return shopPreviews.containsValue(inv);
    }

    public boolean shopInventoryExists(Inventory inv) {
        return shoppingPlayers.values().stream().anyMatch(x -> x.fakeInv() == inv);
    }


    public static ChestShops get() {
        return INSTANCE;
    }


}
