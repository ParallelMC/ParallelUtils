package parallelmc.parallelutils.custommobs;

import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftZombie;
import org.jetbrains.annotations.NotNull;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import parallelmc.parallelutils.Parallelutils;

import static org.bukkit.attribute.Attribute.*;

public class EntityWisp extends CraftZombie {
    Plugin plugin;

    public EntityWisp(CraftServer server, NMSWisp entity, Plugin plugin) {
        super(server, entity);
        this.plugin = plugin;
        editNBT();
    }

    @NotNull
    @Override
    public EntityType getType(){
        return Parallelutils.mobTypes.getType("wisp");
    }

    private void editNBT(){
        //Attributes
        //Health = 30
        this.getAttribute(GENERIC_MAX_HEALTH).setBaseValue(30.0);
        this.setHealth(this.getAttribute(GENERIC_MAX_HEALTH).getBaseValue());

        //Damage = 8
        this.getAttribute(GENERIC_ATTACK_DAMAGE).setBaseValue(8.0);

        //Follow range = 16
        this.getAttribute(GENERIC_FOLLOW_RANGE).setBaseValue(16.0);

        //Movement speed = 0.25
        this.getAttribute(GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);

        //Other stuff
        //Loot table
        this.setLootTable(new WispLootTable(plugin));

        //invis
        //this.setInvisible(true);

        //can't pickup items
        this.setCanPickupItems(false);

        //no random equipment
        this.getEquipment().clear();

        //TODO: prevent sunburn

        //silent
        this.setSilent(true);
    }

    @Override
    public void setCustomName(String name){

    }
}
