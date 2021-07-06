package parallelmc.parallelutils.modules.effectextender.handlers;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import parallelmc.parallelutils.modules.effectextender.listeners.EffectListener;

import java.util.HashMap;

public class ActionChangedHandler implements ActionHandler {
    @Override
    public void execute(EntityPotionEffectEvent event, LivingEntity player) {
        if (EffectListener.playerEffects.containsKey(player)) {
            PotionEffect newEffect = event.getNewEffect();
            PotionEffect oldEffect = event.getOldEffect();
            HashMap<PotionEffectType, Integer> maxes = EffectListener.playerEffects.get(player);

            if (newEffect == null || oldEffect == null) return;

            PotionEffectType type = newEffect.getType();
            if (!maxes.containsKey(type)) return;

            int curMax = maxes.get(type);
            int newDuration = newEffect.getDuration();
            int oldDuration = oldEffect.getDuration();

            if (oldEffect.getAmplifier() > newEffect.getAmplifier())
                // disregard lower tier effects
                return;

            Cause cause = event.getCause();

            if (cause == Cause.WITHER_ROSE) {
                // wither rose should purely mimic vanilla to stay balanced
                return;
            }

            if (oldDuration < newDuration || newEffect.getAmplifier() > oldEffect.getAmplifier()) {
                // update max accordingly if current duration is less than new duration
                // or if there is a higher tier of potion effect
                curMax = newDuration * 2;
                maxes.put(newEffect.getType(), curMax);
            }

            if (cause == Cause.BEACON && oldDuration > newDuration) {
                // cancel beacon override only if the player has a longer duration than a beacon's
                event.setCancelled(true);
                return;
            }

            if (cause == Cause.AREA_EFFECT_CLOUD) {
                /*
                    lingering potion applies effect once every second for five seconds
                    this means if they already have the effect they will slowly gain more time
                    if they don't already have the effect then just mimic vanilla behavior
                 */
                if (oldDuration > newDuration)
                    newDuration /= 5;
                else
                    newDuration -= oldDuration;
            }

            int extendedDuration = oldDuration + newDuration;

            // reset timer if new tier of effect
            // yes ik im checking this twice just shh
            if (newEffect.getAmplifier() > oldEffect.getAmplifier()) {
                extendedDuration = newDuration;
            }
            // cap potion effect durations to the current max
            else if (extendedDuration > curMax) {
                extendedDuration = curMax;
            }

            PotionEffect extendedEffect = new PotionEffect(newEffect.getType(), extendedDuration, newEffect.getAmplifier(), newEffect.isAmbient(), newEffect.hasParticles());

            // replace the new effect with ours
            event.setCancelled(true);
            player.removePotionEffect(extendedEffect.getType());
            player.addPotionEffect(extendedEffect);
        }
    }
}
