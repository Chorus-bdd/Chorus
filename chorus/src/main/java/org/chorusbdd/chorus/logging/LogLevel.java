package org.chorusbdd.chorus.logging;

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

    public static LogLevel getLogLevel(String logLevel) {
        LogLevel result = null;
        for (LogLevel l : LogLevel.values()) {
            if ( l.name().equalsIgnoreCase(logLevel)) {
                result = l;
                break;
            }
        }

        if ( result == null ) {
            ChorusOut.out.println("Did not recognise log level sys property " + logLevel + " will default to WARN");
            result = LogLevel.WARN;
        }

        return result;
    }
}
