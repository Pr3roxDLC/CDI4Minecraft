package me.pr3.cdi.api;

import me.pr3.cdi.managers.ScopeManager;

public interface ScopeManagerExtension {
    void onScopeManagerInit(ScopeManager scopeManager);
    void onCreateInstance(Class<?> clazz, Object object, ScopeManager scopeManager);
    void onInitScope(Class<?> scope, ScopeManager scopeManager);
}
