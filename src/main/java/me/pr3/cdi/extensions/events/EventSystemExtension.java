package me.pr3.cdi.extensions.events;

import me.pr3.cdi.api.ScopeManagerExtension;
import me.pr3.cdi.extensions.events.annotations.Observes;
import me.pr3.cdi.extensions.events.annotations.filters.If;
import me.pr3.cdi.managers.ScopeManager;

import static me.pr3.cdi.extensions.events.annotations.filters.If.PLAYER_NON_NULL;

public class EventSystemExtension implements ScopeManagerExtension {


    @Override
    public void onScopeManagerInit(ScopeManager scopeManager) {
        EventManager eventManager = new EventManager(scopeManager);
    }

    @Override
    public void onCreateInstance(Class<?> clazz, Object object, ScopeManager scopeManager) {

    }

    @Override
    public void onInitScope(Class<?> scope, ScopeManager scopeManager) {

    }
}
