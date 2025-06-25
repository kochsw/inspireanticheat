package de.inspire.ac.api.eventbus;

import de.inspire.ac.api.eventbus.exceptions.NoLambdaException;
import de.inspire.ac.api.eventbus.interfaces.Cancellable;
import de.inspire.ac.api.eventbus.interfaces.EventHandler;
import de.inspire.ac.api.eventbus.listeners.Listener;
import de.inspire.ac.api.eventbus.listeners.impl.Factory;
import de.inspire.ac.api.eventbus.listeners.impl.LambdaListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class EventBus implements de.inspire.ac.api.eventbus.interfaces.EventBus {

    private final Map<Object, List<Listener>> listenerCache = new ConcurrentHashMap<>();
    private final Map<Class<?>, List<Listener>> staticListenerCache = new ConcurrentHashMap<>();

    private final Map<Class<?>, List<Listener>> listenerMap = new ConcurrentHashMap<>();

    private final List<LambdaFactoryInfo> lambdaFactoryInfos = new ArrayList<>();

    @Override
    public void registerLambdaFactory(String packagePrefix, Factory factory) {
        synchronized (lambdaFactoryInfos) {
            lambdaFactoryInfos.add(new LambdaFactoryInfo(packagePrefix, factory));
        }
    }

    @Override
    public boolean isListening(Class<?> eventClazz) {
        List<Listener> listeners = listenerMap.get(eventClazz);
        return listeners != null && !listeners.isEmpty();
    }

    @Override
    public <T> void post(T event) {
        List<Listener> listeners = listenerMap.get(event.getClass());

        if (listeners != null) {
            for (Listener listener : listeners) listener.call(event);
        }

    }

    @Override
    public <T extends Cancellable> T post(T event) {
        List<Listener> listeners = listenerMap.get(event.getClass());

        if (listeners != null) {
            event.setCancelled(false);

            for (Listener listener : listeners) {
                listener.call(event);
                if (event.isCancelled()) break;
            }
        }

        return event;
    }

    @Override
    public void subscribe(Object object) {
        subscribe(getListeners(object.getClass(), object), false);
    }

    @Override
    public void subscribe(Class<?> clazz) {
        subscribe(getListeners(clazz, null), true);
    }

    @Override
    public void subscribe(Listener listener) {
        subscribe(listener, false);
    }

    private void subscribe(List<Listener> listeners, boolean onlyStatic) {
        for (Listener listener : listeners) subscribe(listener, onlyStatic);
    }

    private void subscribe(Listener listener, boolean onlyStatic) {
        if (onlyStatic) {
            if (listener.isStatic())
                insert(
                        listenerMap.computeIfAbsent(
                                listener.getTarget(),
                                e -> new CopyOnWriteArrayList<>()
                        ), listener
                );
        } else insert(
                listenerMap.computeIfAbsent(
                        listener.getTarget(),
                        e -> new CopyOnWriteArrayList<>()
                ), listener
        );

    }

    private void insert(List<Listener> listeners, Listener listener) {
        int i = 0;
        for (; i < listeners.size(); i++)
            if (listener.getPriority().getPriority() > listeners.get(i).getPriority().getPriority()) break;

        listeners.add(i, listener);
    }

    @Override
    public void unsubscribe(Object object) {
        unsubscribe(getListeners(object.getClass(), object), false);
    }

    @Override
    public void unsubscribe(Class<?> clazz) {
        unsubscribe(getListeners(clazz, null), true);
    }

    @Override
    public void unsubscribe(Listener listener) {
        unsubscribe(listener, false);
    }

    private void unsubscribe(List<Listener> listeners, boolean staticOnly) {
        for (Listener listener : listeners) unsubscribe(listener, staticOnly);
    }

    private void unsubscribe(Listener listener, boolean staticOnly) {
        List<Listener> l = listenerMap.get(listener.getTarget());

        if (l != null) {
            if (staticOnly) if (listener.isStatic()) l.remove(listener);
            else l.remove(listener);
        }
    }

    private List<Listener> getListeners(Class<?> clazz, Object object) {
        Function<Object, List<Listener>> func = e -> {
            List<Listener> listeners = new CopyOnWriteArrayList<>();

            getListeners(listeners, clazz, object);

            return listeners;
        };

        if (object == null) return staticListenerCache.computeIfAbsent(clazz, func);

        for (Object key : listenerCache.keySet())
            if (key == object) return listenerCache.get(object);

        List<Listener> listeners = func.apply(object);
        listenerCache.put(object, listeners);

        return listeners;
    }

    private void getListeners(List<Listener> listeners, Class<?> clazz, Object object) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (isValid(method)) listeners.add(new LambdaListener(
                        getLambdaFactory(clazz),
                        clazz,
                        object,
                        method
                ));
        }

        if (clazz.getSuperclass() != null)
            getListeners(listeners, clazz.getSuperclass(), object);
    }

    private boolean isValid(Method method) {
        if (!method.isAnnotationPresent(EventHandler.class)) return false;
        if (method.getReturnType() != void.class) return false;
        if (method.getParameterCount() != 1) return false;
        return !method.getParameters()[0].getType().isPrimitive();
    }

    private Factory getLambdaFactory(Class<?> clazz) {
        synchronized (lambdaFactoryInfos) {
            for (LambdaFactoryInfo info : lambdaFactoryInfos)
                if (clazz.getName().startsWith(info.packagePrefix))
                    return info.factory;
        }

        throw new NoLambdaException(clazz);
    }

    private record LambdaFactoryInfo(String packagePrefix, Factory factory) {}
}
