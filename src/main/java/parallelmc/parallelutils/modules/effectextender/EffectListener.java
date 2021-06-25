package parallelmc.parallelutils.modules.effectextender;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;
import org.bukkit.potion.PotionEffectType;
import parallelmc.parallelutils.modules.effectextender.handlers.ActionAddedHandler;
import parallelmc.parallelutils.modules.effectextender.handlers.ActionChangedHandler;
import parallelmc.parallelutils.modules.effectextender.handlers.ActionRemovedHandler;
import parallelmc.parallelutils.Parallelutils;

import java.util.HashMap;
import java.util.logging.Level;

public class EffectListener implements Listener {

    // TODO: Make this save across logging off or on and reboots
    public static HashMap<LivingEntity, HashMap<PotionEffectType, Integer>> playerEffects = new HashMap<>();

    // could put these in a hashmap but eh
    private static ActionAddedHandler actionAdded = new ActionAddedHandler();
    private static ActionRemovedHandler actionRemoved = new ActionRemovedHandler();
    private static ActionChangedHandler actionChanged = new ActionChangedHandler();

    @EventHandler
    public void onEntityEffect(EntityPotionEffectEvent event) {
        // only want to worry about player effects
        // also ignore effects given by plugins, this improves efficiency but
        // comes at the cost of this plugin not recognizing effects given by other plugins
        // possibly remove if it becomes an issue
        if (event.getEntityType() != EntityType.PLAYER || event.getCause() == Cause.PLUGIN)
            return;

        LivingEntity player = (LivingEntity)event.getEntity();

        // handle each action
        // removed and cleared are pretty much the same thing
        switch (event.getAction()) {
            case ADDED:
                actionAdded.execute(event, player);
                break;
            case CLEARED:
            case REMOVED:
                actionRemoved.execute(event, player);
                break;
            case CHANGED:
                actionChanged.execute(event, player);
                break;
            default:
                Parallelutils.log(Level.SEVERE, "Unhandled action. (How did you get here?) Tried handling action type " + event.getAction());
                break;
        }
    }
}
