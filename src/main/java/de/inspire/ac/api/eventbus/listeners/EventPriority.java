package de.inspire.ac.api.eventbus.listeners;

import lombok.Getter;

@Getter
public enum EventPriority {

    HIGHEST(200),
    HIGH(100),
    MEDIUM(0),
    LOW(-100),
    LOWEST(-200);

    private final int priority;
    EventPriority(int priority) {
        this.priority = priority;
    }
}
