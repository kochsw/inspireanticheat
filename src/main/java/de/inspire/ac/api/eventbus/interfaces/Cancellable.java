package de.inspire.ac.api.eventbus.interfaces;

public interface Cancellable {

    void setCancelled(boolean cancelled);

    default void cancel() {
        setCancelled(true);
    }

    boolean isCancelled();
}
