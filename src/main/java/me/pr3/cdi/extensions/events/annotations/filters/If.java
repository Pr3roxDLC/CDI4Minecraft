package me.pr3.cdi.extensions.events.annotations.filters;

public @interface If {
    String[] value();
    String PLAYER_NON_NULL = "PLAYER_NON_NULL";
    String WORLD_NON_NULL = "WORLD_NON_NULL";
    String IN_MAIN_MENU = "IN_MAIN_MENU";
}
