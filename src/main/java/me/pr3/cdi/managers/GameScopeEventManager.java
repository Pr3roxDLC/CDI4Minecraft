package me.pr3.cdi.managers;

import me.pr3.cdi.annotations.scopes.LifeScoped;
import me.pr3.cdi.annotations.scopes.SessionScoped;
import me.pr3.cdi.annotations.scopes.WorldScoped;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class GameScopeEventManager {
    public GameScopeEventManager(){
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerConnectedEvent(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        System.out.println("Connected to Server: " + e.getConnectionType());
        ScopeManager.INSTANCE.initScope(SessionScoped.class);
        ScopeManager.INSTANCE.initScope(WorldScoped.class);
        ScopeManager.INSTANCE.initScope(LifeScoped.class);
    }

}
