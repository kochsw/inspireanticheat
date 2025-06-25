package de.inspire.ac.api.loaders.impl;

import de.inspire.ac.api.loaders.Loader;
import de.inspire.ac.api.loaders.impl.exceptions.ReflectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ReflectiveLoader<T> implements Loader<T> {

    private final String path;
    private final Class<T> clazz;
    private final Logger logger = LogManager.getLogger(ReflectiveLoader.class);

    public ReflectiveLoader(String path, Class<T> clazz) {
        this.path = path;
        this.clazz = clazz;
    }

    @Override
    public List<T> load() {
        Set<Class<? extends T>> pluginClasses = findPluginClasses(path);

        List<T> plugins = new ArrayList<>(pluginClasses.size());
        for (Class<? extends T> pluginClass : pluginClasses) {
            try {
                Constructor<? extends T> constructor = pluginClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                plugins.add(constructor.newInstance());
            } catch (ReflectiveOperationException e) {
                throw new ReflectionException("Could not instantiate plugin class " + pluginClass.getName(), e);
            }
        }

        return plugins;
    }

    private Set<Class<? extends T>> findPluginClasses(String packageName) {
        Set<Class<? extends T>> classes = new HashSet<>();
        String path = packageName.replace('.', '/');

        try {
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                switch (resource.getProtocol()) {
                    case "file" -> classes.addAll(loadFromDirectory(new File(resource.getFile()), packageName));
                    case "jar" -> {
                        String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
                        classes.addAll(loadFromJar(jarPath, packageName));
                    }
                    default -> logger.error("Unsupported protocol: {}", resource.getProtocol());
                }
            }
        } catch (IOException e) {
            throw new ReflectionException("Error while loading plugin classes from " + packageName, e);
        }

        return classes;
    }

    private Set<Class<? extends T>> loadFromDirectory(File dir, String packageName) {
        Set<Class<? extends T>> classes = new HashSet<>();
        if (!dir.exists() || !dir.isDirectory()) return classes;

        File[] files = dir.listFiles();
        if (files == null) return classes;

        for (File file : files) {
            if (file.isDirectory())
                classes.addAll(loadFromDirectory(file, packageName + "." + file.getName()));
            else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().replace(".class", "");
                tryLoadClass(className, classes);
            }
        }
        return classes;
    }

    private Set<Class<? extends T>> loadFromJar(String jarPath, String packageName) {
        Set<Class<? extends T>> classes = new HashSet<>();
        String packagePath = packageName.replace('.', '/');

        try (JarFile jar = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();

                if (!entry.isDirectory() && entry.getName().endsWith(".class") && entry.getName().startsWith(packagePath)) {
                    String className = entry.getName()
                            .replace('/', '.')
                            .replace(".class", "");

                    tryLoadClass(className, classes);
                }
            }
        } catch (IOException e) {
            throw new ReflectionException("Error reading .jar " + jarPath, e);
        }

        return classes;
    }

    @SuppressWarnings("unchecked")
    private void tryLoadClass(String className, Set<Class<? extends T>> classes) {
        try {
            Class<?> cls = Class.forName(className);
            if (clazz.isAssignableFrom(cls)
                    && cls != clazz
                    && !cls.isInterface()
                    && !Modifier.isAbstract(cls.getModifiers()))
                classes.add((Class<? extends T>) cls);
        } catch (ClassNotFoundException e) {
            logger.error("Class {} not found", className, e);
        } catch (Throwable t) {
            throw new ReflectionException("Error while loading plugin class " + className, t);
        }
    }
}
