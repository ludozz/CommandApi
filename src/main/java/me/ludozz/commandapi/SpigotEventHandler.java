package me.ludozz.commandapi;

import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SpigotEventHandler {

    @NotNull
    EventPriority priority() default EventPriority.NORMAL;

    boolean ignoreCancelled() default false;

    @NotNull
    String eventClass();
}
