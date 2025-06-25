package de.inspire.ac.api.eventbus.interfaces;

import de.inspire.ac.api.eventbus.listeners.Listener;
import de.inspire.ac.api.eventbus.listeners.impl.Factory;

public interface EventBus {

    void registerLambdaFactory(String packagePrefix, Factory factory);

    boolean isListening(Class<?> eventClass);

    <T> void post(T event);

    <T extends Cancellable> T post(T event);

    void subscribe(Object object);

    void subscribe(Class<?> clazz);

    void subscribe(Listener listener);

    void unsubscribe(Object object);

    void unsubscribe(Class<?> clazz);

    void unsubscribe(Listener listener);
}
