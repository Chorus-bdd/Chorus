package org.chorusbdd.chorus.util.function;

/**
 * Created by nick on 09/01/15.
 */
public interface BiPredicate<T, U> {

    boolean test(T t, U u);
}

