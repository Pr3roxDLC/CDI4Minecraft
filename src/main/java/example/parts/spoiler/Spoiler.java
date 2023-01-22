package example.parts.spoiler;

import me.pr3.cdi.annotations.scopes.ClientScoped;

@ClientScoped
public class Spoiler {
    public int width = 100;

    public Spoiler(){
        System.out.println("Created Spoiler");
    }
}
