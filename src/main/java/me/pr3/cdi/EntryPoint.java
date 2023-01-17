package me.pr3.cdi;

import me.pr3.cdi.annotations.scopes.ClientScoped;
import me.pr3.cdi.managers.ScopeManager;

public class EntryPoint {

    private static ScopeManager scopeManager;

    public static void init(){
        scopeManager = new ScopeManager();
        scopeManager.initScope(ClientScoped.class);
    }
}
