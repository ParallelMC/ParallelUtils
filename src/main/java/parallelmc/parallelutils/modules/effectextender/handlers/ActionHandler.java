package parallelmc.parallelutils.modules.effectextender.handlers;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityPotionEffectEvent;

public interface ActionHandler {
    void execute(EntityPotionEffectEvent event, LivingEntity player);
}
