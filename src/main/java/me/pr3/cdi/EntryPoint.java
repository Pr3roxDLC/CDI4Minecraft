package me.pr3.cdi;

import me.pr3.cdi.annotations.scopes.ClientScoped;
import me.pr3.cdi.api.ScopeManagerExtension;
import me.pr3.cdi.managers.ScopeManager;

public class EntryPoint {

    private static ScopeManager scopeManager;

    public static void init(ScopeManagerExtension... extensions){
        scopeManager = new ScopeManager();
        for (ScopeManagerExtension extension : extensions) {
            scopeManager.installExtension(extension);
        }
        scopeManager.initScope(ClientScoped.class);
    }
}
