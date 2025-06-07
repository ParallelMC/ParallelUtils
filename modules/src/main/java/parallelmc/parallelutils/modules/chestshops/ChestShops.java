package parallelmc.parallelutils.modules.chestshops;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelClassLoader;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.chestshops.commands.ChestShopCommands;
import parallelmc.parallelutils.modules.chestshops.commands.ChestShopDebug;
import parallelmc.parallelutils.modules.chestshops.events.*;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;

public class ChestShops extends ParallelModule {

    private final HashMap<UUID, HashSet<Shop>> chestShops = new HashMap<>();
    private final HashMap<UUID, Inventory> shopPreviews = new HashMap<>();
    private final HashMap<UUID, ShopperData> shoppingPlayers = new HashMap<>();

    private ParallelUtils puPlugin;

    private ChestShopCommands chestShopCommands;

    private static ChestShops INSTANCE;

    // 26 stacks of 64 diamonds
    private static final int MAX_DIAMONDS = 1664;

    public ChestShops(ParallelClassLoader classLoader, List<String> dependents) {
        super(classLoader, dependents);
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

        if (plugin == null) {
            ParallelUtils.log(Level.SEVERE, "Unable to enable ChestShops. Plugin " + Constants.PLUGIN_NAME
                    + " does not exist!");
            return;
        }

        this.puPlugin = (ParallelUtils) plugin;

        if (!puPlugin.registerModule(this)) {
            ParallelUtils.log(Level.SEVERE, "Unable to register module ChestShops! " +
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
                        shopID      varchar(36) not null,
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
                        BuyAmt      int         not null,
                        Timestamp   timestamp   not null default current_timestamp
                            on update current_timestamp,
                        constraint ChestShops_UUID_uindex
					        unique (shopID),
					    PRIMARY KEY (shopID)
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
                UUID id = UUID.fromString(results.getString("shopID"));
                World world = puPlugin.getServer().getWorld(results.getString("World"));
                Location chestLoc = new Location(world, results.getInt("ChestX"), results.getInt("ChestY"), results.getInt("ChestZ"));
                Location signLoc = new Location(world, results.getInt("SignX"), results.getInt("SignY"), results.getInt("SignZ"));
                Material item = Material.getMaterial(results.getString("Item"));
                int sellAmt = results.getInt("SellAmt");
                int buyAmt = results.getInt("BuyAmt");
                addShop(uuid, id, chestLoc, signLoc, item, sellAmt, buyAmt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.chestShopCommands = new ChestShopCommands();
        puPlugin.getCommand("chestshop").setExecutor(chestShopCommands);
        chestShopCommands.addCommand("debug", new ChestShopDebug());

        manager.registerEvents(new OnSignText(), puPlugin);
        manager.registerEvents(new OnClickBlock(), puPlugin);
        manager.registerEvents(new OnBreakShop(), puPlugin);
        manager.registerEvents(new OnPreviewInteract(), puPlugin);
        manager.registerEvents(new OnShopInteract(), puPlugin);
        manager.registerEvents(new OnSignEdit(), puPlugin);

        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        try (Connection conn = puPlugin.getDbConn()) {
            if (conn == null) throw new SQLException("Unable to establish connection!");
            // allow duplicate player uuids since players can have multiple shops
            // however, shop ids should never be the same nor update their data
            // so do nothing in that case
            PreparedStatement statement = conn.prepareStatement("INSERT INTO ChestShops (shopID, UUID, World, ChestX, ChestY, ChestZ, SignX, SignY, SignZ, Item, SellAmt, BuyAmt) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE shopID = shopID");
            statement.setQueryTimeout(30);
            this.chestShops.forEach((u, o) -> {
                o.forEach((s) -> {
                    try {
                        statement.setString(1, s.id().toString());
                        statement.setString(2, u.toString());
                        statement.setString(3, s.chestPos().getWorld().getName());
                        statement.setInt(4, s.chestPos().getBlockX());
                        statement.setInt(5, s.chestPos().getBlockY());
                        statement.setInt(6, s.chestPos().getBlockZ());
                        statement.setInt(7, s.signPos().getBlockX());
                        statement.setInt(8, s.signPos().getBlockY());
                        statement.setInt(9, s.signPos().getBlockZ());
                        statement.setString(10, s.item().toString());
                        statement.setInt(11, s.sellAmt());
                        statement.setInt(12, s.buyAmt());
                        statement.addBatch();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            });
            statement.executeBatch();
            conn.commit();
            statement.close();

	    // this logic below needs to be redone at some point
	    // ideally shifting to a full database driven setup
		
            // remove old chestshop entries that no longer exist on the server
            // this helps fix conflicts where someone creates a shop where one recently existed (small edge case but good to patch)
            // since existing chestshops have their timestamp updated automatically above, they shouldn't be touched by this
            /* PreparedStatement cleanup = conn.prepareStatement("DELETE FROM ChestShops WHERE Timestamp < DATE_SUB(NOW(), INTERVAL 15 MINUTE)");
            cleanup.setQueryTimeout(30);
            cleanup.execute();
            conn.commit();
            cleanup.close();
	     */

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUnload() {

    }

    @Override
    public @NotNull String getName() {
        return "ChestShops";
    }

    public void addShop(UUID owner, UUID id, Location chestPos, Location signPos, Material item, int sellAmt, int buyAmt) {
        Shop shop = new Shop(owner, id, chestPos, signPos, item, sellAmt, buyAmt);
        if (chestShops.containsKey(owner)) {
            HashSet<Shop> shops = chestShops.get(owner);
            shops.add(shop);
        }
        else {
            HashSet<Shop> shops = new HashSet<>();
            shops.add(shop);
            chestShops.put(owner, shops);
        }
    }

    public void removeShop(UUID owner, Location chestPos) {
        HashSet<Shop> shops = chestShops.get(owner);
        shops.removeIf(x -> x.chestPos().equals(chestPos));
        if (shops.size() == 0)
            chestShops.remove(owner);
    }

    public List<Shop> getAllShopsFromSignPos(Location signPos) {
        List<Shop> out = new ArrayList<>();
        for (HashSet<Shop> s : chestShops.values()) {
            List<Shop> r = s.stream().filter(x -> x.signPos().equals(signPos)).toList();
            if (!r.isEmpty()) {
                out.addAll(r);
            }
        }
        return out;
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


    public ShopResult attemptPurchase(Player player, Shop shop, Container chest) {
        int empty = 0;
        for (ItemStack i : player.getInventory().getStorageContents()) {
            if (i == null || i.getType() == Material.AIR)
                empty++;
        }
        if (empty < Math.ceil((double)shop.sellAmt() / shop.item().getMaxStackSize())) {
            return ShopResult.INVENTORY_FULL;
        }
        if (shop.buyAmt() > 0) {
            int slot = player.getInventory().first(Material.DIAMOND);
            if (slot == -1) {
                return ShopResult.NO_DIAMONDS;
            }
            ItemStack diamonds = player.getInventory().getItem(slot);
            if (diamonds.getAmount() < shop.buyAmt()) {
                return ShopResult.INSUFFICIENT_FUNDS;
            }
        }

        Inventory inv = chest.getInventory();
        InventoryHolder holder = inv.getHolder();
        HashMap<Integer, ? extends ItemStack> items;
        if (holder instanceof DoubleChest dc) {
            Chest left = (Chest)dc.getLeftSide();
            if (left == null) {
                ParallelUtils.log(Level.WARNING, "attemptPurchase: getLeftSide() returned null");
                return ShopResult.ERROR;
            }
            Chest right = (Chest)dc.getRightSide();
            if (right == null) {
                ParallelUtils.log(Level.WARNING, "attemptPurchase: getRightSide() returned null");
                return ShopResult.ERROR;
            }
            if (left.getInventory().containsAtLeast(new ItemStack(Material.DIAMOND), MAX_DIAMONDS - shop.buyAmt())) {
                return ShopResult.SHOP_FULL;
            }
            items = left.getInventory().all(shop.item());
            Inventory shopping = Bukkit.createInventory(null, 54, Component.text("ChestShop (Click to Buy)"));
            items.forEach(shopping::setItem);
            int itemAmt = 0;
            for (ItemStack i : items.values()) {
                itemAmt += i.getAmount();
            }
            if (inv.getContents().length == 0 || items.size() == 0 || itemAmt < shop.sellAmt()) {
                return ShopResult.SHOP_EMPTY;
            }
            player.openInventory(shopping);
            shoppingPlayers.put(player.getUniqueId(), new ShopperData(shopping, inv, shop));
        }
        else {
            if (inv.containsAtLeast(new ItemStack(Material.DIAMOND), MAX_DIAMONDS - shop.buyAmt())) {
                return ShopResult.SHOP_FULL;
            }
            items = inv.all(shop.item());
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
            shoppingPlayers.put(player.getUniqueId(), new ShopperData(shopping, inv, shop));
        }
        return ShopResult.SUCCESS;
    }

    public void openShopPreview(Player player, Shop shop, Inventory chest) {
        if (chest.getHolder() instanceof DoubleChest dc) {
            Chest left = (Chest)dc.getLeftSide();
            if (left == null) {
                ParallelUtils.log(Level.WARNING, "attemptPurchase: getLeftSide() returned null");
                return;
            }
            Chest right = (Chest)dc.getRightSide();
            if (right == null) {
                ParallelUtils.log(Level.WARNING, "attemptPurchase: getRightSide() returned null");
                return;
            }
            Inventory inv = Bukkit.createInventory(null, 54, Component.text("ChestShop Preview"));
            HashMap<Integer, ? extends ItemStack> items = left.getInventory().all(shop.item());
            // unfortunately have to do this in two iterations to avoid '? extends/captures' conflicts
            items.forEach(inv::setItem);
            items = right.getInventory().all(shop.item());
            items.forEach(inv::setItem);
            player.openInventory(inv);
            shopPreviews.put(player.getUniqueId(), inv);
        }
        else {
            Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST, Component.text("ChestShop Preview"));
            HashMap<Integer, ? extends ItemStack> items = chest.all(shop.item());
            items.forEach(inv::setItem);
            player.openInventory(inv);
            shopPreviews.put(player.getUniqueId(), inv);
        }
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
        return shoppingPlayers.values().stream().anyMatch(x -> x.fakeInv().equals(inv));
    }

    public boolean isPlayerUsingShop(Shop shop) {
        return shoppingPlayers.values().stream().anyMatch(x -> x.shop().equals(shop));
    }


    public static ChestShops get() {
        return INSTANCE;
    }


}
