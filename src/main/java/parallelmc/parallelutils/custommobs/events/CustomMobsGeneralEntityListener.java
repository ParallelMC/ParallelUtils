package parallelmc.parallelutils.custommobs.events;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.Registry;

import java.util.logging.Level;

/** For non-specific entity listeners **/
public class CustomMobsGeneralEntityListener implements Listener {

	@EventHandler
	public void onEntityDespawn(final EntityRemoveFromWorldEvent event) {
		CraftEntity entity = (CraftEntity)event.getEntity();
		if (Registry.containsEntity(entity.getUniqueId().toString())) {
			Parallelutils.log(Level.ALL, "Removing entity " + entity.getUniqueId().toString() + " from world");
			Registry.removeEntity(entity.getUniqueId().toString());
		}
	}
}
