package example.parts.engine;

import example.parts.spoiler.Spoiler;
import me.pr3.cdi.annotations.Inject;
import me.pr3.cdi.annotations.Specializes;

@Specializes
public class VR6 extends Engine {

    @Inject
    Spoiler spoiler;
    public VR6() {
        System.out.println("Created VR6");
    }
}
