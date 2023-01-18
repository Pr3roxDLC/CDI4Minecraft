package me.pr3.cdi.extensions.settings;

import me.pr3.cdi.api.ScopeManagerExtension;
import me.pr3.cdi.extensions.settings.annotations.ClientSetting;
import me.pr3.cdi.managers.ScopeManager;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


public class SettingsExtension implements ScopeManagerExtension {

    Map<String, Object> settings = new HashMap<>();
    @Override
    public void onScopeManagerInit(ScopeManager scopeManager) {
        //Populate settings map from some file
    }

    @Override
    public void onCreateInstance(Class<?> clazz, Object object, ScopeManager scopeManager) {
        for(Field field : clazz.getFields()){
            if(field.isAnnotationPresent(ClientSetting.class)){
               String settingName = field.getAnnotation(ClientSetting.class).value();
               Object value = settings.get(settingName);
                try {
                    field.set(object, value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void onInitScope(Class<?> scope, ScopeManager scopeManager) {

    }
}
