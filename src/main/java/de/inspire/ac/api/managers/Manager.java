package de.inspire.ac.api.managers;

import de.inspire.ac.api.loaders.Loader;
import de.inspire.ac.api.loaders.impl.ReflectiveLoader;
import de.inspire.ac.api.loaders.impl.SPILoader;
import lombok.Getter;

import java.util.List;

@Getter
public class Manager<T> {

    private final List<T> elements;

    public Manager(String path, Class<T> clazz) {
        this(new ReflectiveLoader<>(path, clazz));
    }

    public Manager(Class<T> clazz) {
        this(new SPILoader<>(clazz));
    }

    public Manager(Loader<T> loader) {
        elements = loader.load();
    }
}
