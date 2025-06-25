package de.inspire.ac.api.eventbus.listeners.impl;

import de.inspire.ac.api.eventbus.exceptions.LambdaException;
import de.inspire.ac.api.eventbus.interfaces.EventHandler;
import de.inspire.ac.api.eventbus.listeners.EventPriority;
import de.inspire.ac.api.eventbus.listeners.Listener;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

public class LambdaListener implements Listener {

    private static final Method privateLookupInMethod;

    private final Class<?> target;
    private final boolean isStatic;
    private final EventPriority priority;
    private final Consumer<Object> executor;

    @SuppressWarnings("unchecked")
    public LambdaListener(Factory factory, Class<?> clazz, Object object, Method method) {
        this.target = method.getParameters()[0].getType();
        this.isStatic = Modifier.isStatic(method.getModifiers());
        this.priority = method.getAnnotation(EventHandler.class).priority();

        try {
            String name = method.getName();
            MethodHandles.Lookup lookup;

            lookup = factory.create(privateLookupInMethod, clazz);

            MethodType methodType = MethodType.methodType(
                    void.class,
                    method.getParameters()[0].getType()
            );

            MethodHandle methodHandle;
            MethodType invokedType;

            if (isStatic) {
                methodHandle = lookup.findStatic(clazz, name, methodType);
                invokedType = MethodType.methodType(Consumer.class);
            } else {
                methodHandle = lookup.findVirtual(clazz, name, methodType);
                invokedType = MethodType.methodType(Consumer.class, clazz);
            }



            // TODO: Translte
            // LambdaMetafactory гораздо быстрее Reflection API.
            // LambdaMetafactory создает инстанс функционального интерфейса (Consumer) с прямой ссылкой на метод (invokedynamic).
            // Это почти эквивалентно по скорости обычному вызову.
            MethodHandle lambdaFactory = LambdaMetafactory.metafactory(
                    lookup,
                    "accept",
                    invokedType,
                    MethodType.methodType(void.class, Object.class),
                    methodHandle,
                    methodType
            ).getTarget();

            if (isStatic) this.executor = (Consumer<Object>) lambdaFactory.invoke();
            else this.executor = (Consumer<Object>) lambdaFactory.invoke(object);
        } catch (Throwable throwable) {
            throw new LambdaException("Initialization error.");
        }
    }

    @Override
    public void call(Object event) {
        executor.accept(event);
    }

    @Override
    public Class<?> getTarget() {
        return target;
    }

    @Override
    public EventPriority getPriority() {
        return priority;
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    static {
        try {
            privateLookupInMethod = MethodHandles.class.getDeclaredMethod(
                    "privateLookupIn",
                    Class.class,
                    MethodHandles.Lookup.class
            );
        } catch (NoSuchMethodException e) {
            throw new LambdaException("Could not find public method handles.");
        }
    }
}
