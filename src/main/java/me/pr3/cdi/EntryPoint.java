package me.pr3.cdi;

import me.pr3.cdi.annotations.scopes.ClientScoped;
import me.pr3.cdi.api.ScopeManagerExtension;
import me.pr3.cdi.managers.ScopeManager;
import org.jetbrains.annotations.NotNull;

public class EntryPoint {

    private static ScopeManager scopeManager;

    public static void init(ScopeManagerExtension @NotNull ... extensions){
        scopeManager = new ScopeManager();
        for (ScopeManagerExtension extension : extensions) {
            scopeManager.installExtension(extension);
        }
        scopeManager.init();
        scopeManager.initScope(ClientScoped.class);
    }
}
