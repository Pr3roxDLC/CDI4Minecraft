package example;

import me.pr3.cdi.annotations.Inject;
import me.pr3.cdi.annotations.PostConstruct;
import me.pr3.cdi.annotations.scopes.ClientScoped;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@ClientScoped
public class    Car {
    @Inject
    public Engine engine;

    public Car(){
    }

    @PostConstruct
    public void postConstruct(){
        System.out.println("Created Car with engine " + engine);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e){
        System.out.println(engine);
    }
}
