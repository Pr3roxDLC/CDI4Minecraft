package example;

import example.parts.spoiler.Spoiler;
import me.pr3.cdi.annotations.Inject;
import me.pr3.cdi.annotations.PostConstruct;
import me.pr3.cdi.annotations.scopes.ClientScoped;

@ClientScoped
public class Corrado extends Car {

    @Inject
    Spoiler spoiler;

    @PostConstruct
    public void postConstructInChild(){
        super.postConstruct();
        System.out.println("Created Corrado with " + engine + " and Spoiler with width: " + spoiler.width);
    }

}
