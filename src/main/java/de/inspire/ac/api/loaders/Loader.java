package de.inspire.ac.api.loaders;

import java.util.List;

@FunctionalInterface
public interface Loader<T> {
    List<T> load();
}
