package me.pr3.cdi.annotations.scopes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Scope
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LifeScoped {
}
