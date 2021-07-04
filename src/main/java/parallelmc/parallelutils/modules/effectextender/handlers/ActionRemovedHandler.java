package parallelmc.parallelutils.modules.effectextender.handlers;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import parallelmc.parallelutils.modules.effectextender.listeners.EffectListener;

import java.util.HashMap;

public class ActionRemovedHandler implements ActionHandler {
    @Override
    public void execute(EntityPotionEffectEvent event, LivingEntity player) {
        if (EffectListener.playerEffects.containsKey(player)) {
            HashMap<PotionEffectType, Integer> maxes = EffectListener.playerEffects.get(player);

            PotionEffect oldEffect = event.getOldEffect();

            if (oldEffect == null) return;

            PotionEffectType oldEffectType = oldEffect.getType();

            // clean each player's hashmap as effects get removed
            maxes.remove(oldEffectType);
        }
    }
}
