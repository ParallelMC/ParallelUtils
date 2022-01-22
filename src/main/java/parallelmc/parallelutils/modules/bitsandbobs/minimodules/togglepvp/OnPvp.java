package parallelmc.parallelutils.modules.bitsandbobs.minimodules.togglepvp;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;

import java.util.UUID;

public class OnPvp implements Listener {
    /* TODO:
       Possibly convert messages to minimessages
       I didn't have the updated version so I used this for now
       Also I used sendMessageTo to prevent the extra newlines from sendParallelMessageTo
     */
    @EventHandler
    public void onPVP(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player attacker && event.getEntity() instanceof Player victim) {
            UUID aid = attacker.getUniqueId();
            UUID vid = victim.getUniqueId();
            if (TogglePvpManager.pvpToggles.containsKey(aid)) {
                if (TogglePvpManager.pvpToggles.get(aid)) {
                    ParallelChat.sendMessageTo(attacker, "§cYou cannot attack players with PVP disabled!");
                    event.setCancelled(true);
                }
            }
            if (TogglePvpManager.pvpToggles.containsKey(vid)) {
                if (TogglePvpManager.pvpToggles.get(vid)) {
                    ParallelChat.sendMessageTo(attacker, "§c" + victim.getName() + " has PVP disabled!");
                    event.setCancelled(true);
                }
            }
        }
    }
}
