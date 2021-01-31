package parallelmc.parallelutils.custommobs.bukkitmobs;

import org.bukkit.craftbukkit.v1_16_R3.entity.CraftZombie;
import org.bukkit.plugin.Plugin;

import static org.bukkit.attribute.Attribute.*;

public class CraftWisp {

    public static void setupNBT(Plugin plugin, CraftZombie entity){
        //Attributes
        //Health = 30
        entity.getAttribute(GENERIC_MAX_HEALTH).setBaseValue(30.0);
        entity.setHealth(entity.getAttribute(GENERIC_MAX_HEALTH).getBaseValue());

        //Damage = 8
        entity.getAttribute(GENERIC_ATTACK_DAMAGE).setBaseValue(8.0);

        //Follow range = 16
        entity.getAttribute(GENERIC_FOLLOW_RANGE).setBaseValue(16.0);

        //Movement speed = 0.25
        entity.getAttribute(GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);

        //No zombie reinforcements
        entity.getAttribute(ZOMBIE_SPAWN_REINFORCEMENTS).setBaseValue(0);

        //Other stuff

        //invis
        entity.setInvisible(true);

        //can't pickup items
        entity.setCanPickupItems(false);

        //no random equipment
        entity.getEquipment().clear();

        entity.setShouldBurnInDay(false);

        //silent
        entity.setSilent(true);
    }
}
