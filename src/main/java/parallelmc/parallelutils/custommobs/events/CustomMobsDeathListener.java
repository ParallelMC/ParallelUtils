package parallelmc.parallelutils.custommobs.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import parallelmc.parallelutils.custommobs.bukkitmobs.CraftWisp;
import parallelmc.parallelutils.custommobs.nmsmobs.EntityData;
import parallelmc.parallelutils.custommobs.registry.EntityRegistry;

/**
 * For listening for deaths of players and entities
 **/
public class CustomMobsDeathListener implements Listener {
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		EntityDamageEvent lastDamage = player.getLastDamageCause();
		if (lastDamage instanceof EntityDamageByEntityEvent) {
			org.bukkit.entity.Entity killer = ((EntityDamageByEntityEvent) lastDamage).getDamager();
			if (EntityRegistry.getInstance().containsEntity(killer.getUniqueId().toString())) {
				EntityData pair = EntityRegistry.getInstance().getEntity(killer.getUniqueId().toString());
				switch (pair.type) {
					case "wisp":
						event.setDeathMessage(player.getDisplayName() + " was slain by Wisp");
						break;
				}
			}
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		EntityData pair = EntityRegistry.getInstance().getEntity(event.getEntity().getUniqueId().toString());
		if (pair != null) {
			switch (pair.type) {
				case "wisp":
					CraftWisp.deathLoot(event);
					break;
			}
		}
	}
}
