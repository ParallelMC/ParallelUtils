package parallelmc.parallelutils.modules.bitsandbobs.minimodules;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class EntityTweaks implements Listener {

    @EventHandler
    public void onShibaInuSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Wolf wolf) {
            // Check if the wolf is a shiba inu variant
            if (wolf.getVariant().getKey().equals(NamespacedKey.fromString("parallel:shiba_inu"))) {
                // Set scale to 0.8
                AttributeInstance scale = wolf.getAttribute(Attribute.GENERIC_SCALE);
                if (scale != null) {
                    scale.setBaseValue(0.8D);
                }
            }
        }
    }
}
