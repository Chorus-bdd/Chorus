package org.chorusbdd.chorus.util.logging;

/**
* Created by nick on 04/02/14.
*/
public enum LogLevel {
    FATAL(0),
    ERROR(1),
    WARN(2),
    INFO(3),
    DEBUG(4),
    TRACE(5);

    private int level;

    LogLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
