package de.inspire.ac.api.loaders.impl;

import de.inspire.ac.api.loaders.Loader;

import java.util.ArrayList;
import java.util.List;

public class MixedLoader<T> implements Loader<T> {

    private final Loader<T>[] loaders;

    @SafeVarargs
    public MixedLoader(Loader<T>... loaders) {
        this.loaders = loaders;
    }

    @Override
    public List<T> load() {
        List<T> result = new ArrayList<>();
        for (Loader<T> loader : loaders)
            result.addAll(loader.load());
        return result;
    }
}
