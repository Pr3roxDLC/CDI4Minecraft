package example.parts.engine;

import example.parts.engine.Engine;
import me.pr3.cdi.annotations.Specializes;

@Specializes
public class VR6 extends Engine {
    public VR6() {
        System.out.println("Created VR6");
    }
}
