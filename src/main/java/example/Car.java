package example;

import example.parts.engine.Engine;
import me.pr3.cdi.annotations.Inject;
import me.pr3.cdi.annotations.PostConstruct;
import me.pr3.cdi.extensions.events.EventManager;
import me.pr3.cdi.extensions.events.annotations.Observes;
import me.pr3.cdi.extensions.events.annotations.filters.If;
import me.pr3.cdi.extensions.settings.annotations.ClientSetting;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static me.pr3.cdi.extensions.events.annotations.filters.If.PLAYER_NON_NULL;

public class Car {

    @ClientSetting("hp")
    String hp = "400";

    @Inject
    public Engine engine;

    @PostConstruct
    public void postConstruct() {
        System.out.println("Created a generic Car");
    }

}
