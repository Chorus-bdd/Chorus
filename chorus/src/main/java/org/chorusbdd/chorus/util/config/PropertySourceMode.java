package org.chorusbdd.chorus.util.config;

/**
 * User: nick
 * Date: 15/01/13
 * Time: 18:24
 *
 * Where more than one property source provide value(s) for a property, each source may either append values
 * to the list of values for that property or may override current values (in this case the eventual value will be
 * the value supplied by the highest priority source)
 */
public enum PropertySourceMode {
    OVERRIDE,
    APPEND
}
