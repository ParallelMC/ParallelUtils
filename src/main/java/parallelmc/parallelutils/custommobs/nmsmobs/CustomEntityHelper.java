package parallelmc.parallelutils.custommobs.nmsmobs;

import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.PathfinderGoalSelector;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

public class CustomEntityHelper {

	public static void clearGoals(EntityInsentient entity) {
		Map goalC = (Map)getPrivateField("c", PathfinderGoalSelector.class, entity.goalSelector);
		goalC.clear();
		Set goalD = (Set) getPrivateField("d", PathfinderGoalSelector.class, entity.goalSelector);
		goalD.clear();
		Map targetC = (Map)getPrivateField("c", PathfinderGoalSelector.class, entity.targetSelector);
		targetC.clear();
		Set targetD = (Set)getPrivateField("d", PathfinderGoalSelector.class, entity.targetSelector);
		targetD.clear();
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
