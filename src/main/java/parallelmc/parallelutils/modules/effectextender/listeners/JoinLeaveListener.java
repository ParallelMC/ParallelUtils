package parallelmc.parallelutils.modules.effectextender.listeners;

import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;
import parallelmc.parallelutils.modules.effectextender.EffectExtender;

import java.sql.*;
import java.util.HashMap;

public class JoinLeaveListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        HashMap<PotionEffectType, Integer> effects = new HashMap<>();
        LivingEntity player = event.getPlayer();
        String uuid = player.getUniqueId().toString();

        try {
            Statement statement = EffectExtender.dbConn.createStatement();
            statement.setQueryTimeout(10);
            ResultSet result = statement.executeQuery(
                    "select * from PlayerEffects where UUID = '" + uuid + "'");

            while (result.next()) {
                PotionEffectType type = PotionEffectType.getByName(result.getString("EffectType"));
                int duration = result.getInt("MaxDuration");
                effects.put(type, duration);
            }

            // add player effects to hashmap when they join
            if (effects.size() > 0) {
                EffectListener.playerEffects.put(player, effects);
                // after we put effects into live hashmap we can remove them from the db
                statement.execute("delete from PlayerEffects where UUID = '" + uuid + "'");
            }

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        LivingEntity player = event.getPlayer();

        // don't do anything if they have no effects
        if (!EffectListener.playerEffects.containsKey(player))
            return;


        try {
            HashMap<PotionEffectType, Integer> effects = EffectListener.playerEffects.get(player);
            String uuid = player.getUniqueId().toString();
            // prepare a batch of sql statements for each effect
            PreparedStatement statement = EffectExtender.dbConn.prepareStatement("insert into PlayerEffects values (?, ?, ?)");
            statement.setQueryTimeout(60);
            effects.forEach((effect, maxDuration) -> {
                // why do I HAVE to handle SQLExceptions
                try {
                    statement.setString(1, uuid);
                    statement.setString(2, effect.getName());
                    statement.setInt(3, maxDuration);
                    statement.addBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            statement.executeBatch();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
