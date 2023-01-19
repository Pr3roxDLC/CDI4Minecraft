package me.pr3.cdi.extensions.events;

import me.pr3.cdi.extensions.events.annotations.Observes;
import me.pr3.cdi.managers.ScopeManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class EventManager {
    public static HashMap<Class<?>, Set<Object>> listeners; //Map<Event, Set<Listeners>>
    public static HashMap<Class<?>, HashMap<Class<?>, Method>> targets; //Map<ListenerClass,Map<EventClass, Method>>
    private final ScopeManager scopeManager;
    private static EventManager INSTANCE;

    public EventManager(ScopeManager scopeManager){
        MinecraftForge.EVENT_BUS.register(this);
        this.scopeManager = scopeManager;
        targets = new HashMap<>();
        listeners = new HashMap<>();
        scopeManager.getScopeMap().values().stream().flatMap(Collection::stream).forEach(clazz -> {
            targets.put(clazz, new HashMap<>());
            for (Method method : clazz.getMethods()) {
                if(method.getParameters().length == 0)continue;
                if(method.getParameters()[0].isAnnotationPresent(Observes.class)){
                    targets.get(clazz).put(method.getParameters()[0].getType(), method);
                    if(!listeners.containsKey(method.getParameters()[0].getType())){
                        listeners.put(method.getParameters()[0].getType(), new HashSet<>());
                    }
                }
            }
        });
        INSTANCE = this;
    }

    public static void subscribe(Object o){
        Class<?> clazz = o.getClass();
        Set<Class<?>> eventsThisClassListensTo = targets.get(clazz).keySet();
        eventsThisClassListensTo.forEach(eventClass -> {
            listeners.get(eventClass).add(o);
        });
    }

    public static void unsubscribe(Object o){

    }

    public static void post(Event event){
        INSTANCE.onEvent(event);
    }

    @SubscribeEvent
    public void onEvent(Event event){
        if(!listeners.containsKey(event.getClass()))return;
        listeners.get(event.getClass()).forEach(listener -> {
           Method target = targets.get(listener.getClass()).get(event.getClass());
            ArrayList<Object> parameterInstances = new ArrayList<>();
            parameterInstances.add(event);
            for (int i = 1; i < target.getParameters().length; i++) {
                parameterInstances.add(scopeManager.getInstanceIfPresent(target.getParameters()[i].getType()).orElse(null));
            }
            try {
                target.invoke(listener, parameterInstances.toArray());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
