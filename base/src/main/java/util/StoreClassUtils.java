package util;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class StoreClassUtils extends ClassUtils {

    /**
     * @param className   required (name of searched class)
     * @param packageName not required (name of searched package name)
     * @return {@link Class}
     * orElseThrow {@link NoSuchFieldError}
     * @apiNote Return found class
     **/

    public static Class getClassForPackage(String className, String packageName) {
        var classes = getClassesForPackage(packageName);
        return classes.stream().filter(p -> StringUtils.equals(ClassUtils.getSimpleName(p), className)).
                findFirst().orElseThrow(() -> new RuntimeException("Sınıf bulunamadı.Lütfen dosya adını veya " +
                        "rest api üzerindeki parametrenizi kontrol ediniz"));
    }

    /**
     * @param className required (name of plain class name)
     * @param delimiter required (from rest api delimiter like '-')
     * @param prefix    required (Prefix for class like Model or Data)
     * @return {@link Class}
     * @apiNote String  absolute name
     **/
    public static String generateClassName(String className, String delimiter, String prefix) {

        var parts = StringUtils.split(className, delimiter);
        var plainName = Arrays.stream(parts).map(StringUtils::capitalize).collect(Collectors.joining());
        if (StringUtils.contains(plainName,"Model")){
            return plainName;
        }
        return StringUtils.join(plainName, prefix);
    }

    /**
     * @param packageName not required (name of searched package name)
     * @return {@link Class}
     * orElseThrow {@link NoSuchFieldError}
     * @apiNote Return found class
     **/

    public static Set<Class<? extends Serializable>> getClassesForPackage(String packageName) {
        Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
        var classes = new HashSet<>(reflections.getSubTypesOf(Serializable.class));
        return classes;
    }

    public static  <T> T create(Class<T> tClass) {
        try {
            return tClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new ClassCastException();
        }
    }

}
