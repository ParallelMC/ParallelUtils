package parallelmc.parallelutils.modules.effectextender.handlers;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffectType;
import parallelmc.parallelutils.modules.effectextender.EffectListener;

import java.util.HashMap;

public class ActionRemovedHandler implements ActionHandler {
    @Override
    public void execute(EntityPotionEffectEvent event, LivingEntity player) {
        if (EffectListener.playerEffects.containsKey(player)) {
            HashMap<PotionEffectType, Integer> maxes = EffectListener.playerEffects.get(player);
            PotionEffectType oldEffectType = event.getOldEffect().getType();

            // clean each player's hashmap as effects get removed
            if (maxes.containsKey(oldEffectType)) {
                maxes.remove(oldEffectType);
            }
        }
    }
}
