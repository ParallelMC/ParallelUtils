package parallelmc.parallelutils.modules.bitsandbobs.minimodules.togglepvp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.UUID;

public class OnPvp implements Listener {

    @EventHandler
    public void onPVP(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player attacker && event.getEntity() instanceof Player victim) {
            UUID aid = attacker.getUniqueId();
            UUID vid = victim.getUniqueId();
            Component cantAttack = MiniMessage.miniMessage().deserialize("<red>You cannot attack players with PVP disabled!");
            Component hasDisabled = MiniMessage.miniMessage().deserialize("<red>" + victim.getName() + " has PVP disabled!");
            if (TogglePvpManager.pvpToggles.containsKey(aid)) {
                if (!TogglePvpManager.pvpToggles.get(aid)) {
                    attacker.sendMessage(cantAttack);
                    event.setCancelled(true);
                }
            }
            // PVP is off by default
            else {
                attacker.sendMessage(cantAttack);
                event.setCancelled(true);
            }
            if (TogglePvpManager.pvpToggles.containsKey(vid)) {
                if (!TogglePvpManager.pvpToggles.get(vid)) {
                    attacker.sendMessage(hasDisabled);
                    event.setCancelled(true);
                }
            }
            // PVP is off by default
            else {
                attacker.sendMessage(hasDisabled);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHitByProjectile(ProjectileHitEvent event) {
        if (event.getEntity().getShooter() instanceof Player attacker && event.getHitEntity() instanceof Player victim) {
            UUID aid = attacker.getUniqueId();
            UUID vid = victim.getUniqueId();
            Component cantAttack = MiniMessage.miniMessage().deserialize("<red>You cannot attack players with PVP disabled!");
            Component hasDisabled = MiniMessage.miniMessage().deserialize("<red>" + victim.getName() + " has PVP disabled!");
            if (TogglePvpManager.pvpToggles.containsKey(aid)) {
                if (!TogglePvpManager.pvpToggles.get(aid)) {
                    attacker.sendMessage(cantAttack);
                    event.setCancelled(true);
                }
            }
            // PVP is off by default
            else {
                attacker.sendMessage(cantAttack);
                event.setCancelled(true);
            }
            if (TogglePvpManager.pvpToggles.containsKey(vid)) {
                if (!TogglePvpManager.pvpToggles.get(vid)) {
                    attacker.sendMessage(hasDisabled);
                    event.setCancelled(true);
                }
            }
            // PVP is off by default
            else {
                attacker.sendMessage(hasDisabled);
                event.setCancelled(true);
            }
        }
    }
}
