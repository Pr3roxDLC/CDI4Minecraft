package me.pr3.cdi.managers;

import me.pr3.cdi.annotations.Inject;
import me.pr3.cdi.annotations.PostConstruct;
import me.pr3.cdi.annotations.scopes.Scope;
import me.pr3.cdi.api.Injectable;
import me.pr3.cdi.api.ScopeManagerExtension;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


@SuppressWarnings({"unused","rawtypes","unchecked"})
public class ScopeManager {
    public static ScopeManager INSTANCE;
    public GameScopeEventManager GAME_SCOPE_EVENT_MANAGER;
    Set<Class<?>> scopes;    //All Registered Scopes
    HashMap<Class<?>, Set<Class<?>>> scopeMap; //Map<ScopeClass,Set<ScopedClass>>
    HashMap<Class<?>, HashMap<Class<?>, Object>> scopedObjectsMap; //Map<ScopeClass,Set<Instance>>
    HashMap<Class<?>, Set<Class<?>>> injectionMap; //Map<InjectedClass,Set<TargetClass>>

    private final List<ScopeManagerExtension> installedExtensions = new ArrayList<>();

    public ScopeManager() {
        INSTANCE = this;
        GAME_SCOPE_EVENT_MANAGER = new GameScopeEventManager();
    }

    public void init(){
        scopes = generateScopes();
        scopeMap = generateScopeMap();
        scopedObjectsMap = generateEmptyScopedObjectsMap();
        injectionMap = generateInjectionMap();
        installedExtensions.forEach(extension -> extension.onScopeManagerInit(this));
    }

    public void installExtension(ScopeManagerExtension scopeManagerExtension){
        installedExtensions.add(scopeManagerExtension);
    }

    private @NotNull HashMap<Class<?>, Set<Class<?>>> generateInjectionMap() {
        HashMap<Class<?>, Set<Class<?>>> injectionMap = new HashMap<>();
        Reflections ref = new Reflections();
        HashSet<Class<?>> allScopedClasses = new HashSet<>();
        for (Class scope : scopes) {
            allScopedClasses.addAll(ref.getTypesAnnotatedWith(scope));
        }
        for (Class<?> injectedClass : allScopedClasses) {
            HashSet<Class<?>> targetsForInjectedClass = new HashSet<>();
            for (Class<?> targetClass : allScopedClasses) {
                for (Field field : targetClass.getDeclaredFields()) {
                    if (field.isAnnotationPresent(Inject.class) && field.getType().equals(injectedClass)) {
                        targetsForInjectedClass.add(targetClass);
                    }
                }
            }
            injectionMap.put(injectedClass, targetsForInjectedClass);
        }
        return injectionMap;
    }

    private Set<Class<?>> generateScopes() {
        return new Reflections().getTypesAnnotatedWith(Scope.class).stream().filter(Class::isAnnotation).collect(Collectors.toSet());
    }

    private @NotNull HashMap<Class<?>, Set<Class<?>>> generateScopeMap() {
        Reflections reflections = new Reflections();
        HashMap<Class<?>, Set<Class<?>>> scopeMap = new HashMap<>();
        for (Class clazz : scopes) {
            Set<Class<?>> scopedClasses = reflections.getTypesAnnotatedWith(clazz);
            scopeMap.put(clazz, scopedClasses);
        }
        return scopeMap;
    }

    @Contract(" -> new")
    private @NotNull HashMap<Class<?>, HashMap<Class<?>, Object>> generateEmptyScopedObjectsMap() {
        return new HashMap<Class<?>, HashMap<Class<?>, Object>>() {{
            for (Class<?> scope : scopes) {
                put(scope, new HashMap<>());
            }
        }};
    }

    public void initScope(Class<?> scope) {
        Set<Class<?>> scopedClasses = scopeMap.get(scope);
        scopedObjectsMap.get(scope).values().forEach(object -> {
            //Call destroy method if object implements Injectable
            if (object instanceof Injectable) {
                ((Injectable) object).destroy();
            }
            //Unsubscribe all objects from any event systems
            MinecraftForge.EVENT_BUS.unregister(object);
        });
        scopedObjectsMap.get(scope).clear();
        for (Class<?> clazz : scopedClasses) {
            createNewInstance(clazz);
        }
        //Now update all the injected Instances in their target instances
        for (Class<?> clazz : scopedClasses) {
            getInstanceIfPresent(clazz).ifPresent(injectedInstance -> {
                for (Class<?> target : injectionMap.get(clazz)) {
                    getInstanceIfPresent(target).ifPresent(injectionTarget -> {
                        setFieldOfTypeForInstance(clazz, injectedInstance, injectionTarget);
                    });
                }
            });
        }
        installedExtensions.forEach(extension -> extension.onInitScope(scope, this));
    }

    public Object createNewInstance(Class<?> clazz) {
        AtomicReference<Object> atomicReference = new AtomicReference<>();
        //Create Populate Instance with injected instances from constructor
        getInjectConstructor(clazz).ifPresent(constructor -> {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            List<Object> parameterInstances = new ArrayList<>();
            for (Class<?> parameterType : parameterTypes) {
                parameterInstances.add(getInstanceIfPresent(parameterType).orElseGet(() -> createNewInstance(parameterType)));
            }
            try {
                atomicReference.set(constructor.newInstance(parameterInstances));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
        //If no instance was created using the constructor, call the no-args constructor, so we have an instance of which we can fill the
        //@Inject Annotated Fields
        if (atomicReference.get() == null) {
            try {
                atomicReference.set(clazz.getDeclaredConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        //Now populate fields
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                Class<?> fieldType = field.getType();
                try {
                    field.setAccessible(true);
                    field.set(atomicReference.get(), getInstanceIfPresent(fieldType).orElseGet(() -> createNewInstance(fieldType)));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        getScopeForClass(clazz).ifPresent(scope -> {
            scopedObjectsMap.get(scope).put(clazz, atomicReference.get());
        });
        installedExtensions.forEach(extension -> extension.onCreateInstance(clazz, atomicReference.get(), this));
        getPostConstruct(clazz).ifPresent(method -> {
            try {
                method.invoke(atomicReference.get());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
        return atomicReference.get();
    }

    private @NotNull Optional<Constructor<?>> getInjectConstructor(@NotNull Class<?> clazz) {
        return Arrays.stream(clazz.getConstructors()).filter(constructor -> constructor.isAnnotationPresent(Inject.class)).findFirst();
    }

    public Optional<Object> getInstanceIfPresent(Class<?> clazz) {
        for (Map.Entry<Class<?>, Set<Class<?>>> classSetEntry : scopeMap.entrySet()) {
            if (classSetEntry.getValue().contains(clazz)) {
                if (scopedObjectsMap.get(classSetEntry.getKey()).containsKey(clazz))
                    return Optional.of(scopedObjectsMap.get(classSetEntry.getKey()).get(clazz));
            }
        }
        return Optional.empty();
    }

    private Optional<Class<?>> getScopeForClass(Class<?> clazz) {
        for (Map.Entry<Class<?>, Set<Class<?>>> scope : scopeMap.entrySet()) {
            if (scope.getValue().contains(clazz)) {
                return Optional.of(scope.getKey());
            }
        }
        return Optional.empty();
    }

    private void setFieldOfTypeForInstance(Class<?> fieldType, Object injectedInstance, @NotNull Object injectionTarget){
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

    private @NotNull Optional<Method> getPostConstruct(@NotNull Class<?> clazz){
        return Arrays.stream(clazz.getMethods()).filter(method -> method.isAnnotationPresent(PostConstruct.class)).findFirst();
    }

    public Set<Class<?>> getScopes() {
        return scopes;
    }

    public HashMap<Class<?>, Set<Class<?>>> getScopeMap() {
        return scopeMap;
    }

    public HashMap<Class<?>, HashMap<Class<?>, Object>> getScopedObjectsMap() {
        return scopedObjectsMap;
    }

    public HashMap<Class<?>, Set<Class<?>>> getInjectionMap() {
        return injectionMap;
    }

    public List<ScopeManagerExtension> getInstalledExtensions() {
        return installedExtensions;
    }
}
