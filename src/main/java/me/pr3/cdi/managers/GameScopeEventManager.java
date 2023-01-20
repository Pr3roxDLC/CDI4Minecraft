package me.pr3.cdi.managers;

import me.pr3.cdi.annotations.scopes.LifeScoped;
import me.pr3.cdi.annotations.scopes.SessionScoped;
import me.pr3.cdi.annotations.scopes.WorldScoped;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.jetbrains.annotations.NotNull;

import static me.pr3.cdi.managers.ScopeManager.INSTANCE;
import static net.minecraftforge.fml.common.network.FMLNetworkEvent.*;

public class GameScopeEventManager {
    public GameScopeEventManager(){
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerConnectedEvent(@NotNull ClientConnectedToServerEvent e) {
        System.out.println("Connected to Server: " + e.getConnectionType());
        INSTANCE.initScope(SessionScoped.class);
        INSTANCE.initScope(WorldScoped.class);
        INSTANCE.initScope(LifeScoped.class);
    }

    @SubscribeEvent
    public void onPlayerDeath(@NotNull LivingDeathEvent e) {
        if (e.getEntity().equals(Minecraft.getMinecraft().player)) {
            INSTANCE.initScope(LifeScoped.class);
        }
    }

    public void onWorldChange(PlayerEvent.PlayerChangedDimensionEvent event){
        INSTANCE.initScope(WorldScoped.class);
    }

}
