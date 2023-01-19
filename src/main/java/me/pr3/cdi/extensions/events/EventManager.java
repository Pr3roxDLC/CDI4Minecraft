package me.pr3.cdi.extensions.events;

import me.pr3.cdi.extensions.events.annotations.Observes;
import me.pr3.cdi.extensions.events.annotations.filters.If;
import me.pr3.cdi.managers.ScopeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import scala.reflect.internal.Trees;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static me.pr3.cdi.extensions.events.annotations.filters.If.*;

public class EventManager {
    private static HashMap<Class<?>, Set<Object>> listeners; //Map<Event, Set<Listeners>>
    private static HashMap<Class<?>, HashMap<Class<?>, Method>> targets; //Map<ListenerClass,Map<EventClass, Method>>

    private static HashMap<String, Supplier<Boolean>> filters;
    private final ScopeManager scopeManager;
    private static EventManager INSTANCE;

    public EventManager(ScopeManager scopeManager) {
        this.scopeManager = scopeManager;
        targets = new HashMap<>();
        listeners = new HashMap<>();
        filters = new HashMap<>();
        //Build the lookup map
        scopeManager.getScopeMap().values().stream().flatMap(Collection::stream).forEach(clazz -> {
            targets.put(clazz, new HashMap<>());
            for (Method method : clazz.getMethods()) {
                if (method.getParameters().length == 0) continue;
                if (method.getParameters()[0].isAnnotationPresent(Observes.class)) {
                    targets.get(clazz).put(method.getParameters()[0].getType(), method);
                    if (!listeners.containsKey(method.getParameters()[0].getType())) {
                        listeners.put(method.getParameters()[0].getType(), new HashSet<>());
                    }
                }
            }
        });

        //Install Default Filters
        installFilter(PLAYER_NON_NULL, () -> Minecraft.getMinecraft().player!=null);
        installFilter(WORLD_NON_NULL, () -> Minecraft.getMinecraft().world!=null);
        installFilter(IN_MAIN_MENU, () -> Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu);

        //Listen to Minecraft's EventBus
        MinecraftForge.EVENT_BUS.register(this);
        INSTANCE = this;
    }

    public static void subscribe(Object o) {
        Class<?> clazz = o.getClass();
        Set<Class<?>> eventsThisClassListensTo = targets.get(clazz).keySet();
        eventsThisClassListensTo.forEach(eventClass -> {
            listeners.get(eventClass).add(o);
        });
    }

    public static void unsubscribe(Object o) {
        Class<?> clazz = o.getClass();
        Set<Class<?>> eventsThisClassListensTo = targets.get(clazz).keySet();
        eventsThisClassListensTo.forEach(eventClass -> {
            listeners.get(eventClass).remove(o);
        });
    }

    public static void installFilter(String key, Supplier<Boolean> filter) {
        filters.put(key, filter);
    }

    public static void post(Event event) {
        INSTANCE.onEvent(event);
    }

    @SubscribeEvent
    public void onEvent(Event event) {
        if (!listeners.containsKey(event.getClass())) return;
        listeners.get(event.getClass()).forEach(listener -> {
            Method target = targets.get(listener.getClass()).get(event.getClass());
            ArrayList<Object> parameterInstances = new ArrayList<>();
            boolean passEvent = true;
            if (target.getParameters()[0].isAnnotationPresent(If.class)) {
                for (String filter : target.getParameters()[0].getAnnotation(If.class).value()) {
                    if (!filters.get(filter).get()) passEvent = false;
                    break;
                }
            }
            if (passEvent) {
                parameterInstances.add(event);
                for (int i = 1; i < target.getParameters().length; i++) {
                    parameterInstances.add(scopeManager.getInstanceIfPresent(target.getParameters()[i].getType()).orElse(null));
                }
                try {
                    target.invoke(listener, parameterInstances.toArray());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

}
