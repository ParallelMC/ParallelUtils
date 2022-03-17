package parallelmc.parallelutils.modules.parallelitems.pocketteleporter;


import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerPositionManager {
    private final Vector spawn = new Vector(-255.5, 140.5, -225.5);
    private final HashMap<UUID, PlayerPosition> savedPositions = new HashMap<>();
    private final HashSet<UUID> teleportCooldowns = new HashSet<>();
    private final HashMap<UUID, BukkitTask> attemptedTeleports = new HashMap<>();

    private final Parallelutils puPlugin;

    public PlayerPositionManager(Parallelutils plugin) {
        this.puPlugin = plugin;
    }

    public void init() {
        // init database
        try (Connection conn = puPlugin.getDbConn()) {
            if (conn == null) throw new SQLException("Unable to establish connection!");
            Statement statement = conn.createStatement();
            statement.setQueryTimeout(15);
            statement.execute("""
                    create table if not exists SavedPositions
                    (
                        UUID          varchar(36) not null,
                        World         varchar(32) not null,
                        x             int         not null,
                        y             int         not null,
                        z             int         not null,
                        beenToSpawn   tinyint     not null,
                        constraint SavedPositions_UUID_index
                            unique(UUID),
                        PRIMARY KEY(UUID)
                    );""");
            conn.commit();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // load database into hashmap
        try (Connection conn = puPlugin.getDbConn()) {
            if (conn == null) throw new SQLException("Unable to establish connection!");
            Statement statement = conn.createStatement();
            statement.setQueryTimeout(30);
            ResultSet results = statement.executeQuery("select * from SavedPositions");
            while (results.next()) {
                UUID uuid = UUID.fromString(results.getString("UUID"));
                String world = results.getString("World");
                int x = results.getInt("x");
                int y = results.getInt("y");
                int z = results.getInt("z");
                boolean beenToSpawn = results.getBoolean("beenToSpawn");
                savedPositions.put(uuid, new PlayerPosition(new Location(
                        puPlugin.getServer().getWorld(world),
                        x, y, z), beenToSpawn));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void unload() {
        try (Connection conn = puPlugin.getDbConn()) {
            if (conn == null) throw new SQLException("Unable to establish connection!");
            PreparedStatement statement = conn.prepareStatement("INSERT INTO SavedPositions (UUID, World, x, y, z, beenToSpawn) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE World = ?, x = ?, y = ?, z = ?, beenToSpawn = ?");
            statement.setQueryTimeout(60);
            this.savedPositions.forEach((u, p) -> {
                try {
                    World world = p.location().getWorld();
                    statement.setString(1, u.toString());
                    statement.setString(2, world.getName());
                    statement.setInt(3, p.location().getBlockX());
                    statement.setInt(4, p.location().getBlockY());
                    statement.setInt(5, p.location().getBlockZ());
                    statement.setBoolean(6, p.hasBeenToSpawn());
                    statement.setString(7, world.getName());
                    statement.setInt(8, p.location().getBlockX());
                    statement.setInt(9, p.location().getBlockY());
                    statement.setInt(10, p.location().getBlockZ());
                    statement.setBoolean(11, p.hasBeenToSpawn());
                    statement.addBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            statement.executeBatch();
            conn.commit();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void attemptTeleport(Player player, ItemStack teleporter) {
        UUID uuid = player.getUniqueId();
        if (attemptedTeleports.containsKey(uuid)) {
            return;
        }
        if (teleportCooldowns.contains(uuid)) {
            ParallelChat.sendParallelMessageTo(player, "Your teleporter is on cooldown! Please wait a few seconds.");
            return;
        }
        // get block player is standing on
        Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        if (player.isJumping() ||
                block.getType() == Material.AIR ||
                player.isInsideVehicle() ||
                player.isSleeping() ||
                player.isClimbing()) {
            ParallelChat.sendParallelMessageTo(player, "Cannot teleport while not on the ground!");
            return;
        }
        if (!isPositionSafe(player.getLocation().getBlock())) {
            ParallelChat.sendParallelMessageTo(player, "You must be standing in a clear area to teleport!");
            return;
        }
        if (player.isSneaking()) {
            removeSavedPosition(player, teleporter);
            ParallelChat.sendParallelMessageTo(player, "Your saved position has been cleared.");
            return;
        }
        ParallelChat.sendParallelMessageTo(player, "Teleporting in 5 seconds, please do not move...");
        BukkitTask runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (savedPositions.containsKey(uuid)) {
                    PlayerPosition pos = savedPositions.get(uuid);
                    if (pos.hasBeenToSpawn()) {
                        // loop teleport attempts in case it fails
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (player.getWorld().getName().equals("world2")) {
                                    RegionManager wgWorld = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld()));
                                    if (wgWorld != null) {
                                        Location pPos = player.getLocation();
                                        ProtectedRegion spawn = wgWorld.getRegion("spawn-world2");
                                        ProtectedRegion spawnTown = wgWorld.getRegion("world2-shopping");
                                        // if player isn't in either region
                                        if (spawn != null &&
                                                spawnTown != null &&
                                                !spawn.contains(pPos.getBlockX(), pPos.getBlockY(), pPos.getBlockZ()) &&
                                                !spawnTown.contains(pPos.getBlockX(), pPos.getBlockY(), pPos.getBlockZ())) {
                                            ParallelChat.sendParallelMessageTo(player, "You must be in the main spawn or shopping region to teleport back!");
                                            attemptedTeleports.remove(uuid);
                                            this.cancel();
                                            return;
                                        }
                                    }
                                }
                                // they have to be in world2 in the first place
                                else {
                                    ParallelChat.sendParallelMessageTo(player, "You must be in the main spawn or shopping region to teleport back!");
                                    attemptedTeleports.remove(uuid);
                                    this.cancel();
                                    return;
                                }
                                Location loc = getNextSafeBlock(pos.location());
                                if (loc == null) {
                                    ParallelChat.sendParallelMessageTo(player, "Unable to teleport!");
                                    attemptedTeleports.remove(uuid);
                                    this.cancel();
                                    return;
                                }
                                if (player.teleport(loc)) {
                                    ParallelChat.sendParallelMessageTo(player, "Teleported back to last saved position!");
                                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                                    removeSavedPosition(player, teleporter);
                                    attemptedTeleports.remove(uuid);
                                    this.cancel();
                                }
                            }
                        }.runTaskTimer(puPlugin, 0L, 2L);
                        teleportCooldowns.add(uuid);
                        // 5 second teleport cooldown
                        Bukkit.getScheduler().runTaskLater(puPlugin, () -> teleportCooldowns.remove(uuid), 100L);
                        return;
                    }
                }
                // loop teleport attempts in case it fails
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        setSavedPosition(player, teleporter);
                        if (player.teleport(new Location(puPlugin.getServer().getWorld(Constants.DEFAULT_WORLD), spawn.getX(), spawn.getY(), spawn.getZ()))) {
                            ParallelChat.sendParallelMessageTo(player, "Teleported to spawn! Your previous position has been saved.");
                            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                            attemptedTeleports.remove(uuid);
                            this.cancel();
                        }
                    }
                }.runTaskTimer(puPlugin, 0L, 2L);
                teleportCooldowns.add(uuid);
                // 5 second teleport cooldown
                Bukkit.getScheduler().runTaskLater(puPlugin, () -> teleportCooldowns.remove(uuid), 100L);
            }
        }.runTaskLater(puPlugin, 100L);
        attemptedTeleports.put(uuid, runnable);

        // movement check, will run 5 times internally (5 seconds)
        new MovementCheck(player, player.getLocation(), 5).runTaskTimer(puPlugin, 0L, 20L);
    }

    private void removeSavedPosition(Player player, ItemStack teleporter) {
        savedPositions.remove(player.getUniqueId());
        ItemMeta teleMeta = teleporter.getItemMeta();
        ArrayList<Component> lore = new ArrayList<>();
        lore.add(Component.text("Right-click ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                .append(Component.text("to teleport between spawn", NamedTextColor.GRAY)).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("and your last location.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("Shift + right-click ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                .append(Component.text("to reset your last location.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));
        teleMeta.lore(lore);
        teleporter.setItemMeta(teleMeta);
        player.updateInventory();
    }

    private void setSavedPosition(Player player, ItemStack teleporter) {
        Location loc = player.getLocation();
        boolean beenToSpawn;
        if (savedPositions.containsKey(player.getUniqueId())) {
            beenToSpawn = savedPositions.get(player.getUniqueId()).hasBeenToSpawn();
        }
        else {
            // if they are not in the hashmap then they just teleported to spawn
            beenToSpawn = true;
        }
        savedPositions.put(player.getUniqueId(), new PlayerPosition(loc, beenToSpawn));
        ItemMeta teleMeta = teleporter.getItemMeta();
        ArrayList<Component> lore = new ArrayList<>();
        lore.add(Component.text("Last Position: ", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)
                .append(Component.text(String.format("%d %d %d [%s]", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), getWorldDisplayName(loc.getWorld().getName())), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));
        lore.add(Component.text("Right-click ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                .append(Component.text("to teleport between spawn", NamedTextColor.GRAY)).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("and your last location.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("Shift + right-click ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                .append(Component.text("to reset your last location.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));
        teleMeta.lore(lore);
        teleporter.setItemMeta(teleMeta);
        player.updateInventory();
    }

    // currently runs synchronously, doubt there will be any lag issues unless someone fills like multiple chunks full of blocks
    // most times this will probably only loop once (best case)
    // essentials uses four while loops so i doubt this will be much slower
    public Location getNextSafeBlock(Location start) {
        Location current = start.clone();
        double originalY = start.getY();

        // base case
        if (isPositionSafe(current.getBlock()))
            return current.add(0D, 0.1D, 0D);

        for (double j = originalY; j < 319; j++) {
            for (int count = 1; count <= 5; count++) {
                    if (isPositionSafe(add(current, count, 0, 0).getBlock())) {
                        return current.add(count, 0.1, 0);
                    }
                    if (isPositionSafe(add(current, -count, 0, 0).getBlock())) {
                        return current.add(-count, 0.1, 0);
                    }
                    if (isPositionSafe(add(current, 0, 0, count).getBlock())) {
                        return current.add(0, 0.1, count);
                    }
                    if (isPositionSafe(add(current, 0, 0, -count).getBlock())) {
                        return current.add(0, 0.1, -count);
                    }
            }
            current.add(0, 1, 0);
        }

        return null;
    }

    // spigot add no make copy
    // this function make copy
    // :)
    private Location add(Location old, double x, double y, double z) {
        return old.clone().add(x, y ,z);
    }

    private boolean isPositionSafe(Block check) {
        return !check.isSolid() &&
                !check.getRelative(BlockFace.UP).isSolid() &&
                check.getType() != Material.FIRE &&
                (check.getRelative(BlockFace.DOWN).isSolid() ||
                        check.getRelative(BlockFace.DOWN).getType() == Material.WATER);
    }


    public String getWorldDisplayName(String worldId) {
        return switch(worldId) {
            case "world2" -> "Overworld";
            case "world2_nether" -> "Nether";
            case "world2_the_end", "world_the_end" -> "The End";
            case "world" -> "Overworld (Parallel Prime)";
            case "world_nether" -> "Nether (Parallel Prime)";
            case "world_skyteaser" -> "Sky Dimension";
            case "world_space" -> "Space";
            default -> worldId;
        };
    }

    public void cancelTeleport(Player player, String reason) {
        UUID uuid = player.getUniqueId();
        if (attemptedTeleports.containsKey(uuid)) {
            attemptedTeleports.get(uuid).cancel();
            switch (reason) {
                case "move" -> ParallelChat.sendParallelMessageTo(player, "You moved! Teleportation cancelled.");
                case "damage" -> ParallelChat.sendParallelMessageTo(player, "You took damage! Teleportation cancelled.");
                case "drop" -> ParallelChat.sendParallelMessageTo(player, "You dropped your teleporter! Teleportation cancelled.");
                case "disconnect" -> Parallelutils.log(Level.INFO, player.getName() + " left while teleporting! Cancelled teleportation.");
                default -> ParallelChat.sendParallelMessageTo(player, "Teleportation cancelled for an unknown reason.");
            }
            attemptedTeleports.remove(uuid);
        }
    }
}
