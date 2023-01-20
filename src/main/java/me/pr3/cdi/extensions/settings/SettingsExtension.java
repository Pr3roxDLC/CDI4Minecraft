package me.pr3.cdi.extensions.settings;

import me.pr3.cdi.api.ScopeManagerExtension;
import me.pr3.cdi.extensions.settings.annotations.ClientSetting;
import me.pr3.cdi.managers.ScopeManager;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class SettingsExtension implements ScopeManagerExtension {

    Map<String, Optional<Object>> settings = new HashMap<>();
    @Override
    public void onScopeManagerInit(@NotNull ScopeManager scopeManager) {
        //Populate settings map from some file
        scopeManager.getScopeMap().forEach((scope, classes) -> {
            classes.forEach(clazz -> {
                for (Field field : clazz.getDeclaredFields()) {
                    if(field.isAnnotationPresent(ClientSetting.class)){
                        settings.put(field.getAnnotation(ClientSetting.class).value(), getValue(field.getAnnotation(ClientSetting.class).value()));
                    }
                }
            });
        });
    }

    private Optional<Object> getValue(String name){
        return settings.getOrDefault(name, Optional.empty());
    }

    @Override
    public void onCreateInstance(@NotNull Class<?> clazz, Object object, ScopeManager scopeManager) {
        for(Field field : clazz.getDeclaredFields()){
            if(field.isAnnotationPresent(ClientSetting.class)){
               String settingName = field.getAnnotation(ClientSetting.class).value();
               Optional<Object> optionalValue = settings.get(settingName);
               optionalValue.ifPresent(value -> {
                   try {
                       field.setAccessible(true);
                       field.set(object, value);
                   } catch (IllegalAccessException e) {
                       throw new RuntimeException(e);
                   }
               });
            }
        }
    }

    @Override
    public void onInitScope(Class<?> scope, ScopeManager scopeManager) {
    }
}
