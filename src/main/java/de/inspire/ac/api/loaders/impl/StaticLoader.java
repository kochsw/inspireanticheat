package de.inspire.ac.api.loaders.impl;

import de.inspire.ac.api.loaders.Loader;

import java.util.List;

public abstract class StaticLoader<T> implements Loader<T> {

    public abstract List<T> loadStatic();

    @Override
    public List<T> load() {
        return loadStatic();
    }
}
