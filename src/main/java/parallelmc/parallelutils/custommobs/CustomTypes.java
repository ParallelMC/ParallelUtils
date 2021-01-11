package parallelmc.parallelutils.custommobs;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CustomTypes {
    private final HashMap<String, EntityType> types = new HashMap<>();

    public CustomTypes(){

    }

    public void addEntityType(String name, Class<? extends Entity> entity, short id) throws Exception{
        Constructor<EntityType> constructor = EntityType.class.getDeclaredConstructor(String.class, Class.class, int.class);
        constructor.setAccessible(true);
        EntityType type = constructor.newInstance(name, entity, id);

        Field field = EntityType.class.getDeclaredField("NAME_MAP");
        field.setAccessible(true);
        Map<String, EntityType> map = (Map<String, EntityType>) field.get(null);
        map.put(name, type);

        Field id_map_field = EntityType.class.getDeclaredField("ID_MAP");
        id_map_field.setAccessible(true);
        Map<Short, EntityType> id_map = (Map<Short, EntityType>) id_map_field.get(null);
        id_map.put(id, type);

        types.put(name,type);
    }

    public EntityType getType(String name){
        return types.get(name);
    }
}
