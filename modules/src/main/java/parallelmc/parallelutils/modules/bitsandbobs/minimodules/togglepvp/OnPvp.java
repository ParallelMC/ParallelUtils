package parallelmc.parallelutils.modules.bitsandbobs.minimodules.togglepvp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.UUID;

public class OnPvp implements Listener {

    @EventHandler
    public void onPVP(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player attacker && event.getEntity() instanceof Player victim) {
            if (attacker == victim) {
                return;
            }
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
        if (event.getDamager() instanceof Firework firework && event.getEntity() instanceof Player victim) {
            if (firework.getShooter() instanceof Player shooter) {
                UUID sid = shooter.getUniqueId();
                UUID vid = victim.getUniqueId();
                Component cantAttack = MiniMessage.miniMessage().deserialize("<red>You cannot attack players with PVP disabled!");
                Component hasDisabled = MiniMessage.miniMessage().deserialize("<red>" + victim.getName() + " has PVP disabled!");
                if (TogglePvpManager.pvpToggles.containsKey(sid)) {
                    if (!TogglePvpManager.pvpToggles.containsKey(sid)) {
                        shooter.sendMessage(cantAttack);
                        event.setCancelled(true);
                    }
                }
                else {
                    shooter.sendMessage(cantAttack);
                    event.setCancelled(true);
                }
                if (TogglePvpManager.pvpToggles.containsKey(vid)) {
                    if (!TogglePvpManager.pvpToggles.get(vid)) {
                        shooter.sendMessage(hasDisabled);
                        event.setCancelled(true);
                    }
                }
                // PVP is off by default
                else {
                    shooter.sendMessage(hasDisabled);
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onHitByProjectile(ProjectileHitEvent event) {
        if (event.getEntity().getShooter() instanceof Player attacker && event.getHitEntity() instanceof Player victim) {
            if (attacker == victim)
                 return;
            UUID aid = attacker.getUniqueId();
            UUID vid = victim.getUniqueId();
            Component cantAttack = MiniMessage.miniMessage().deserialize("<red>You cannot attack players with PVP disabled!");
            Component hasDisabled = MiniMessage.miniMessage().deserialize("<red>" + victim.getName() + " has PVP disabled!");
            if (TogglePvpManager.pvpToggles.containsKey(aid)) {
                if (!TogglePvpManager.pvpToggles.get(aid)) {
                    attacker.sendMessage(cantAttack);
                    if (event.getEntity() instanceof Arrow) {
                        event.getEntity().remove();
                    }
                    event.setCancelled(true);
                }
            }
            // PVP is off by default
            else {
                attacker.sendMessage(cantAttack);
                if (event.getEntity() instanceof Arrow) {
                    event.getEntity().remove();
                }
                event.setCancelled(true);
            }
            if (TogglePvpManager.pvpToggles.containsKey(vid)) {
                if (!TogglePvpManager.pvpToggles.get(vid)) {
                    attacker.sendMessage(hasDisabled);
                    if (event.getEntity() instanceof Arrow) {
                        event.getEntity().remove();
                    }
                    event.setCancelled(true);
                }
            }
            // PVP is off by default
            else {
                attacker.sendMessage(hasDisabled);
                if (event.getEntity() instanceof Arrow) {
                    event.getEntity().remove();
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPotionThrown(PotionSplashEvent event) {
        Component hasDisabled = MiniMessage.miniMessage().deserialize("<red>The potion did not effect you since you have PVP disabled!");
        if (event.getEntity().getShooter() instanceof Player thrower) {
            for (LivingEntity e : event.getAffectedEntities()) {
                if (e instanceof Player victim) {
                    if (victim == thrower)
                        continue;
                    UUID uuid = victim.getUniqueId();
                    Component attackerDisabled = MiniMessage.miniMessage().deserialize("<red>The potion did not effect " + victim.getName() + ", they have PVP disabled!");
                    if (TogglePvpManager.pvpToggles.containsKey(uuid)) {
                        // if the victim has pvp off, then prevent them from being splashed
                        // let them know since this also cancels non-harmful potions
                        if (!TogglePvpManager.pvpToggles.get(uuid)) {
                            victim.sendMessage(hasDisabled);
                            thrower.sendMessage(attackerDisabled);
                            event.setIntensity(e, 0d);
                        }
                    }
                }
            }
        }
    }
}
