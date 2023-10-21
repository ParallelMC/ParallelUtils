package parallelmc.parallelutils.modules.bitsandbobs.minimodules.togglepvp;

import parallelmc.parallelutils.ParallelUtils;

import java.sql.*;
import java.util.HashMap;
import java.util.UUID;

public class TogglePvpManager {
    private final ParallelUtils puPlugin;
    public static HashMap<UUID, Boolean> pvpToggles = new HashMap<>();

    public TogglePvpManager(ParallelUtils puPlugin) {
        this.puPlugin = puPlugin;
    }

    public void init() {
        // init database
        try (Connection conn = puPlugin.getDbConn()) {
            if (conn == null) throw new SQLException("Unable to establish connection!");
            Statement statement = conn.createStatement();
            statement.setQueryTimeout(15);
            statement.execute("""
                    create table if not exists TogglePvp
                    (
                        UUID        varchar(36) not null,
                        Pvp         tinyint     not null,
                        constraint TogglePvp_UUID_uindex
                            unique (UUID),
                        PRIMARY KEY (UUID)
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
            statement.setQueryTimeout(10);
            ResultSet results = statement.executeQuery("select * from TogglePvp");
            while (results.next()) {
                UUID uuid = UUID.fromString(results.getString("UUID"));
                boolean pvp = results.getBoolean("Pvp");
                pvpToggles.put(uuid, pvp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unload() {
        // unload hashmap back into database
        try (Connection conn = puPlugin.getDbConn()) {
            if (conn == null) throw new SQLException("Unable to establish connection!");
            PreparedStatement statement = conn.prepareStatement("INSERT INTO TogglePvp (UUID, Pvp) VALUES (?, ?) ON DUPLICATE KEY UPDATE Pvp = ?");
            statement.setQueryTimeout(30);
            this.pvpToggles.forEach((u, p) -> {
                try {
                    statement.setString(1, u.toString());
                    statement.setBoolean(2, p);
                    statement.setBoolean(3, p);
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
}
