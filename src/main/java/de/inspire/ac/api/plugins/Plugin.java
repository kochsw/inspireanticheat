package de.inspire.ac.api.plugins;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class Plugin {

    static Map<Class<? extends Plugin>, String> CLASS_IDS = new ConcurrentHashMap<>();

    String pluginId;

    protected Plugin() {
        this.pluginId = CLASS_IDS.computeIfAbsent(getClass(), cls ->
                "plugin-" + UUID.randomUUID()
        );
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " - " + pluginId;
    }
}
