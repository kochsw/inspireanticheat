package de.inspire.ac.impl.checks.api;

import de.inspire.ac.api.API;
import de.inspire.ac.impl.checks.api.annotations.CheckInfo;
import de.inspire.ac.impl.checks.api.enums.Category;
import de.inspire.ac.impl.checks.api.exceptions.ErrorCheckInitException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.lang.invoke.MethodHandles;

@Getter @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Check {

    String name, description;
    Category category;

    protected CheckInfo moduleInfo;

    public Check() {
        if (this.getClass().isAnnotationPresent(CheckInfo.class)) {
            moduleInfo = this.getClass().getAnnotation(CheckInfo.class);

            name = moduleInfo.name();
            description = moduleInfo.description();
            category = moduleInfo.category();

            API.INSTANCE.getEventBus().registerLambdaFactory(
                    getClass().getPackageName(),
                    (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup())
            );
        } else throw new ErrorCheckInitException("Module annotation not found.");
    }


}
