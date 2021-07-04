package parallelmc.parallelutils.modules.effectextender;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
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

    // dont worry about it
    private Connection dbConn;

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

        if (plugin == null) {
            Parallelutils.log(Level.SEVERE, "Unable to enable EffectExtender. Plugin " + Constants.PLUGIN_NAME + " does not exist!");
            return;
        }

        Parallelutils puPlugin = (Parallelutils) plugin;

        dbConn = puPlugin.getDbConn();

        // create effects table if it doesn't exist
        try {
            Statement statement = dbConn.createStatement();
            statement.setQueryTimeout(15);
            statement.execute("""
                            create table if not exists PlayerEffects
                            (
                                UUID            varchar(36) not null,
                                EffectType      varchar(20) not null,
                                MaxDuration     int         not null
                            );""");
            dbConn.commit();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        manager.registerEvents(new EffectListener(), plugin);
        manager.registerEvents(new JoinLeaveListener(dbConn), plugin);

        puPlugin.addCommand("effects", new ParallelEffectsCommand());

        Parallelutils.log(Parallelutils.LOG_LEVEL, "EntityPotionEffectEvent registered successfully.");
    }

    @Override
    public void onDisable() {
        // move each player's effects from the hashmap into the db
        try {
            // prepare a statement to send in bulk
            PreparedStatement statement = dbConn.prepareStatement("insert into PlayerEffects values (?, ?, ?)");
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

            dbConn.commit();

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
