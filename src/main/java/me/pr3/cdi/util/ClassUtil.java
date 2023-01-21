package me.pr3.cdi.util;

import me.pr3.cdi.annotations.Inject;
import me.pr3.cdi.annotations.PostConstruct;
import me.pr3.cdi.annotations.scopes.Scope;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ClassUtil {

    public static void setFieldOfTypeForInstance(Class<?> fieldType, Object injectedInstance, @NotNull Object injectionTarget){
        Class<?> injectionTargetClass = injectionTarget.getClass();
        for (Field field : injectionTargetClass.getDeclaredFields()) {
            if(field.getType().equals(fieldType)){
                try {
                    field.setAccessible(true);
                    field.set(injectionTarget, injectedInstance);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static @NotNull Optional<Constructor<?>> getInjectConstructor(@NotNull Class<?> clazz) {
        return Arrays.stream(clazz.getConstructors()).filter(constructor -> constructor.isAnnotationPresent(Inject.class)).findFirst();
    }

    public static @NotNull Optional<Method> getPostConstruct(@NotNull Class<?> clazz){
        return Arrays.stream(clazz.getDeclaredMethods()).filter(method -> method.isAnnotationPresent(PostConstruct.class)).findFirst();
    }

    public static Set<Class<?>> generateScopes() {
        return new Reflections().getTypesAnnotatedWith(Scope.class).stream().filter(Class::isAnnotation).collect(Collectors.toSet());
    }

    public static Set<Field> getAllFields(Class<?> clazz){
        Set<Field> fields = new HashSet<>();
        while(!clazz.equals(Object.class)){
            for (Field field : clazz.getDeclaredFields()) {
                if(field.isAnnotationPresent(Inject.class)){
                    fields.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

}
