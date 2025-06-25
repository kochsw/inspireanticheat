package de.inspire.ac.api.eventbus.listeners;

public interface Listener {
    void call(Object event);
    Class<?> getTarget();
    EventPriority getPriority();
    boolean isStatic();
}
