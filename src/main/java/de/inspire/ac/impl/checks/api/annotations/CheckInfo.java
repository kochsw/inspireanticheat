package de.inspire.ac.impl.checks.api.annotations;

import de.inspire.ac.impl.checks.api.enums.Category;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CheckInfo {
    String name();
    String description();
    Category category();
}
