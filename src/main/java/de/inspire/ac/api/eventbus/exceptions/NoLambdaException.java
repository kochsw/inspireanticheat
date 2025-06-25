package de.inspire.ac.api.eventbus.exceptions;

public class NoLambdaException extends RuntimeException {

    public NoLambdaException(Class<?> clazz) {
        super("No registered lambda listener for " + clazz.getName() + ".");
    }
}
