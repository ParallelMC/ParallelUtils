package parallelmc.parallelutils.modules.effectextender.handlers;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import parallelmc.parallelutils.modules.effectextender.EffectListener;

import java.util.HashMap;

public class ActionAddedHandler implements ActionHandler {
    @Override
    public void execute(EntityPotionEffectEvent event, LivingEntity player) {
        if (!EffectListener.playerEffects.containsKey(player)) {
            EffectListener.playerEffects.put(player, new HashMap<>());
        }

        HashMap<PotionEffectType, Integer> maxes = EffectListener.playerEffects.get(player);
        PotionEffect newEffect = event.getNewEffect();

        if (newEffect == null) return;

        // if effect is new, use it as the max
        if (!maxes.containsKey(newEffect.getType())) {
            maxes.put(newEffect.getType(), newEffect.getDuration() * 2);
        }
    }
}
