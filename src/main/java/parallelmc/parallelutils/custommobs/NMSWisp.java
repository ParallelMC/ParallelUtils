package parallelmc.parallelutils.custommobs;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NMSWisp extends EntityZombie {
    public NMSWisp(EntityTypes<? extends EntityZombie> entitytypes, World world) {
        super(entitytypes, world);
        goalSetting();
    }

    public NMSWisp(World world) {
        super(world);
        goalSetting();
    }

    private void goalSetting(){
        //clearing Zombie goals
        Map goalC = (Map)getPrivateField("c", PathfinderGoalSelector.class, goalSelector);
        goalC.clear();
        Set goalD = (Set) getPrivateField("d", PathfinderGoalSelector.class, goalSelector);
        goalD.clear();
        Map targetC = (Map)getPrivateField("c", PathfinderGoalSelector.class, targetSelector);
        targetC.clear();
        Set targetD = (Set)getPrivateField("d", PathfinderGoalSelector.class, targetSelector);
        targetD.clear();

        this.goalSelector.a(0, new PathfinderGoalMeleeAttack(this,1.0, false));
        this.goalSelector.a(1, new PathfinderGoalRandomStroll(this, 1.0));

        this.targetSelector.a(0, new PathfinderGoalHurtByTarget(this));
    }

    public static Object getPrivateField(String fieldName, Class clazz, Object object)
    {
        Field field;
        Object o = null;

        try {
            field = clazz.getDeclaredField(fieldName);

            field.setAccessible(true);

            o = field.get(object);
        }
        catch(NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return o;
    }
}
