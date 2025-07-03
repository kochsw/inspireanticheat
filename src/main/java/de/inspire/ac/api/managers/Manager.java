package de.inspire.ac.api.managers;

import de.inspire.ac.api.API;
import de.inspire.ac.api.loaders.Loader;
import de.inspire.ac.api.loaders.impl.ReflectiveLoader;
import de.inspire.ac.api.loaders.impl.SPILoader;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Getter
public class Manager<T> {

    private final List<T> elements;

    public static final Logger LOGGER = LogManager.getLogger("manager");

    public Manager(String path, Class<T> clazz) {
        this(new ReflectiveLoader<>(path, clazz, API.INSTANCE.getDefaultClassLoader()));
    }

    public Manager(Class<T> clazz) {
        this(new SPILoader<>(clazz));
    }

    public Manager(Loader<T> loader) {
        elements = loader.load();

        LOGGER.info("Loaded {} element(s)", elements.size());
    }

    public T getElement(Class<?> clazz) {
        return getElements().stream().filter(m -> m.getClass() == clazz).findFirst().orElse(null);
    }

    protected void add(T element) {
        getElements().add(element);
    }
}
