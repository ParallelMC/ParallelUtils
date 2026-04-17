package parallelmc.parallelworlds;

import org.jetbrains.annotations.Nullable;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

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

    /*
    Jank Enum code from https://notes.highlysuspect.agency/blog/enum_reflection/
     */
    @SafeVarargs
    private static <T> T[] concat(Class<T> t, T[] a, T... b) {
        T[] result = (T[]) Array.newInstance(t, a.length + b.length); //i love java
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

//    private static Field definalizeField(Field f) throws Throwable {
//        f.setAccessible(true);
//
//        Field modifiersField = Field.class.getDeclaredField("modifiers");
//        modifiersField.setAccessible(true);
//        modifiersField.set(f, (f.getModifiers() | Modifier.PUBLIC) & ~(Modifier.FINAL | Modifier.PRIVATE | Modifier.PROTECTED));
//        return f;
//    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T makeEnum(Class<T> clazz, String name, int ord, Class<?>[] argTypes, Object... argValues) throws Throwable {
        //prepend the (String, int) arguments and find the constructor
        argTypes = concat(Class.class, new Class<?>[]{String.class, int.class}, argTypes);
        argValues = concat(Object.class, new Object[]{name, ord}, argValues);

        //find the constructor
        Constructor<T> cons = clazz.getDeclaredConstructor(argTypes);
        cons.setAccessible(true);
        MethodHandle consHandle = MethodHandles.lookup().unreflectConstructor(cons);

        //instantiate the enum (!!!)
        T result = (T) consHandle.invokeWithArguments(argValues);

//        //append to Enum.$VALUES
//        Field valuesField = definalizeField(clazz.getDeclaredField("$VALUES")); //hmm...
//        valuesField.set(null, concat(clazz, (T[]) valuesField.get(null), result));

        //fix up the caches in Class
//        Field enumConstantsField = Class.class.getDeclaredField("enumConstants");
//        enumConstantsField.setAccessible(true);
//        enumConstantsField.set(clazz, null);
//        Field enumConstantDirectoryField = Class.class.getDeclaredField("enumConstantDirectory");
//        enumConstantDirectoryField.setAccessible(true);
//        enumConstantDirectoryField.set(clazz, null);
        Field uField = Unsafe.class.getDeclaredField("theUnsafe");
        uField.setAccessible(true);
        Unsafe unsafe = (Unsafe) uField.get(null);

        unsafe.putObject(clazz, unsafe.objectFieldOffset(Class.class.getDeclaredField("enumConstants")), null);
        unsafe.putObject(clazz, unsafe.objectFieldOffset(Class.class.getDeclaredField("enumConstantDirectory")), null);

        return result;
    }
}
