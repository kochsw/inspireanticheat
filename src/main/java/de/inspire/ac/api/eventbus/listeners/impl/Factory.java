package de.inspire.ac.api.eventbus.listeners.impl;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface Factory {
    MethodHandles.Lookup create(Method lookupInMethod, Class<?> clazz) throws InvocationTargetException, IllegalAccessException;
}