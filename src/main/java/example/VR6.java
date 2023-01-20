package example;

import me.pr3.cdi.annotations.Specializes;

@Specializes
public class VR6 extends Engine{
    public VR6() {
        System.out.println("Created VR6");
    }
}
