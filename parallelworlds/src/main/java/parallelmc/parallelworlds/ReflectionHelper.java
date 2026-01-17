package parallelmc.parallelworlds;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class ReflectionHelper {

    @Nullable
    public static <T, U> U getPrivateField(String fieldName, Class<T> clazz, T object, Class<U> retClazz) throws ClassCastException{
        Field field;

        try {
            field = clazz.getDeclaredField(fieldName);

            field.setAccessible(true);

            Object o = field.get(object);

            return (U) o;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
