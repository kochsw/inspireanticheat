package de.inspire.ac.impl.checks.api;

import de.inspire.ac.api.managers.Manager;

public class CheckManager extends Manager<Check> {

    public CheckManager() {
        super("de.inspire.ac.impl.checks.impl", Check.class);
    }
}
