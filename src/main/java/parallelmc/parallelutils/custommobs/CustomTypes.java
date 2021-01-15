package parallelmc.parallelutils.custommobs;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import sun.misc.Unsafe;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

public class CustomTypes {
    private final HashMap<String, EntityType> types = new HashMap<>();

    public CustomTypes(){

    }

    public void addEntityType(String name, Class<? extends Entity> entity, short id) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {
        Constructor<?> constructor = Unsafe.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        Unsafe unsafe = (Unsafe) constructor.newInstance();
        EntityType type = (EntityType) unsafe.allocateInstance(EntityType.class);

        Field ordinalField = Enum.class.getDeclaredField("ordinal");
        makeAccessible(ordinalField);
        ordinalField.setInt(type, 99);

        Field nameField = Enum.class.getDeclaredField("name");
        makeAccessible(nameField);
        nameField.set(type, name);

        Field entityClassField = EntityType.class.getDeclaredField("clazz");
        makeAccessible(entityClassField);
        entityClassField.set(type, entity);

        Field entityIdField = EntityType.class.getDeclaredField("typeId");
        makeAccessible(entityIdField);
        entityIdField.set(type, id);

        Field indepField = EntityType.class.getDeclaredField("independent");
        makeAccessible(indepField);
        indepField.set(type, true);

        Field livingField = EntityType.class.getDeclaredField("living");
        makeAccessible(livingField);
        livingField.set(type, entity != null && LivingEntity.class.isAssignableFrom(entity));

        Field keyField = EntityType.class.getDeclaredField("key");
        makeAccessible(keyField);
        keyField.set(type, name == null ? null : NamespacedKey.minecraft(name));


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

    private static void makeAccessible(Field field) throws NoSuchFieldException, IllegalAccessException {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~ Modifier.FINAL);
    }
}
