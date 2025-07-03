package de.inspire.ac;

import de.inspire.ac.impl.checks.api.CheckManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Getter @FieldDefaults(level = AccessLevel.PRIVATE)
public enum Inspire {

    INSTANCE;

    final Logger logger = LogManager.getLogger("inspire");

    final CheckManager checkManager = new CheckManager();
}
