package example;

import me.pr3.cdi.annotations.Inject;
import me.pr3.cdi.annotations.PostConstruct;
import me.pr3.cdi.annotations.scopes.ClientScoped;
import me.pr3.cdi.extensions.events.EventManager;
import me.pr3.cdi.extensions.events.annotations.Observes;
import me.pr3.cdi.extensions.events.annotations.filters.If;
import me.pr3.cdi.extensions.settings.annotations.ClientSetting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static me.pr3.cdi.extensions.events.annotations.filters.If.PLAYER_NON_NULL;

@ClientScoped
public class Car {
    @ClientSetting("hp")
    String hp = "400";

    public Car() {
    }

    @PostConstruct
    public void postConstruct() {
      //  System.out.println("Created Car with engine " + engine + " with " + hp + " Horse Power");
        EventManager.subscribe(this);
    }

    public void onClientTick(@Observes @If(PLAYER_NON_NULL) TickEvent.ClientTickEvent e, Engine engine) {
        System.out.println(engine);
    }
}
