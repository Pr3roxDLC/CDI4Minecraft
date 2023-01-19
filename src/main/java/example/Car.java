package example;

import me.pr3.cdi.annotations.Inject;
import me.pr3.cdi.annotations.PostConstruct;
import me.pr3.cdi.annotations.scopes.ClientScoped;
import me.pr3.cdi.extensions.settings.annotations.ClientSetting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@ClientScoped
public class    Car {
    @Inject
    Engine engine;

    @ClientSetting("hp")
    String hp = "400";

    public Car(){
    }

    @PostConstruct
    public void postConstruct(){
        System.out.println("Created Car with engine " + engine + " with " + hp + " Horse Power");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e){
        System.out.println(engine);
    }
}
