package parallelmc.parallelutils.modules.effectextender;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.effectextender.commands.ParallelEffectsCommand;
import parallelmc.parallelutils.modules.effectextender.listeners.EffectListener;
import parallelmc.parallelutils.modules.effectextender.listeners.JoinLeaveListener;

import java.sql.*;
import java.util.logging.Level;

/**
 * A module to allow stacking of potions
 */
public class EffectExtender implements ParallelModule {

    private Parallelutils puPlugin;

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

        if (plugin == null) {
            Parallelutils.log(Level.SEVERE, "Unable to enable EffectExtender. Plugin " + Constants.PLUGIN_NAME + " does not exist!");
            return;
        }

        puPlugin = (Parallelutils) plugin;

        if (!puPlugin.registerModule(this)) {
            Parallelutils.log(Level.SEVERE, "Unable to register module EffectExtender! Module may already be registered. Quitting...");
            return;
        }

        // create effects table if it doesn't exist
        try (Connection conn = puPlugin.getDbConn()) {
            if (conn == null) throw new SQLException("Unable to establish connection!");

            Statement statement = conn.createStatement();
            statement.setQueryTimeout(15);
            statement.execute("""
                            create table if not exists PlayerEffects
                            (
                                UUID            varchar(36) not null,
                                EffectType      varchar(20) not null,
                                MaxDuration     int         not null
                            );""");
            conn.commit();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        manager.registerEvents(new EffectListener(), plugin);
        manager.registerEvents(new JoinLeaveListener(puPlugin), plugin);

        puPlugin.addCommand("effects", new ParallelEffectsCommand());

        Parallelutils.log(Parallelutils.LOG_LEVEL, "EntityPotionEffectEvent registered successfully.");
    }

    @Override
    public void onDisable() {
        // move each player's effects from the hashmap into the db
        try (Connection conn = puPlugin.getDbConn()) {
            if (conn == null) throw new SQLException("Unable to establish connection!");

            // prepare a statement to send in bulk
            PreparedStatement statement = conn.prepareStatement("insert into PlayerEffects values (?, ?, ?)");
            statement.setQueryTimeout(60);

            // double foreach lets goooo
            EffectListener.playerEffects.forEach((player, effects) -> {
                effects.forEach((effect, maxDuration) -> {
                    try {
                        String uuid = player.getUniqueId().toString();
                        statement.setString(1, uuid);
                        statement.setString(2, effect.getName());
                        statement.setInt(3, maxDuration);
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

    @Override
    public @NotNull String getName() {
        return "EffectsExtender";
    }

}
