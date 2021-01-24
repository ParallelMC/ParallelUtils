package parallelmc.parallelutils.custommobs;

import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftZombie;
import org.jetbrains.annotations.NotNull;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import parallelmc.parallelutils.Parallelutils;

import static org.bukkit.attribute.Attribute.*;

public class EntityWisp extends CraftZombie{
    Plugin plugin;

    public EntityWisp(CraftServer server, NMSWisp entity, Plugin plugin) {
        super(server, entity);
        this.plugin = plugin;
        setupNBT(plugin, this);
    }

    @NotNull
    @Override
    public EntityType getType(){
        return Parallelutils.mobTypes.getType("wisp");
    }

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
        //Loot table
        //entity.setLootTable(new WispLootTable(plugin));

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
