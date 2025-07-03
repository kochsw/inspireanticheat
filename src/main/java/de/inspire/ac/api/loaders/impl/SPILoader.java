package de.inspire.ac.api.loaders.impl;

import de.inspire.ac.api.loaders.Loader;

import java.util.List;
import java.util.ServiceLoader;

public class SPILoader<T> implements Loader<T> {

    private final ServiceLoader<T> loader;

    public SPILoader(Class<T> clazz) {
        loader = ServiceLoader.load(clazz);
    }

    public SPILoader(Class<T> clazz, ClassLoader classLoader) {
        loader = ServiceLoader.load(clazz, classLoader);
    }

    @Override
    public List<T> load() {
        return loader.stream().map(ServiceLoader.Provider::get).toList();
    }
}
