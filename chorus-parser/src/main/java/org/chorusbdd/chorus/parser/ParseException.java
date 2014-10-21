package org.chorusbdd.chorus.parser;

/**
* Created by nick on 21/10/2014.
*/
public class ParseException extends Exception {

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    ParseException(String message, int lineNumber) {
        super(String.format("%s (at line:%s)", message, lineNumber));
    }
}
