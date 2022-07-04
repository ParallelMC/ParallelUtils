package parallelmc.parallelutils.modules.effectextender;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.effectextender.commands.ParallelEffectsCommand;
import parallelmc.parallelutils.modules.effectextender.listeners.EffectListener;
import parallelmc.parallelutils.modules.effectextender.listeners.JoinLeaveListener;

import java.net.URLClassLoader;
import java.sql.*;
import java.util.logging.Level;

/**
 * A module to allow stacking of potions
 */
public class EffectExtender extends ParallelModule {

    private ParallelUtils puPlugin;

    public EffectExtender(URLClassLoader classLoader) {
        super(classLoader);
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

        if (plugin == null) {
            ParallelUtils.log(Level.SEVERE, "Unable to enable EffectExtender. Plugin " + Constants.PLUGIN_NAME + " does not exist!");
            return;
        }

        puPlugin = (ParallelUtils) plugin;

        if (!puPlugin.registerModule(this)) {
            ParallelUtils.log(Level.SEVERE, "Unable to register module EffectExtender! Module may already be registered. Quitting...");
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

        ParallelUtils.log(ParallelUtils.LOG_LEVEL, "EntityPotionEffectEvent registered successfully.");
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
    public void onUnload() {

    }

    @Override
    public @NotNull String getName() {
        return "EffectsExtender";
    }

}
