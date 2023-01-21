package example.parts.engine;

import me.pr3.cdi.annotations.scopes.SessionScoped;

@SessionScoped
public class Engine {
    public Engine(){
        System.out.println("Created Engine");
    }
}
