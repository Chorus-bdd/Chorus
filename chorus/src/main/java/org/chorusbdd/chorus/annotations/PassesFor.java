package org.chorusbdd.chorus.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * User: nick
 * Date: 18/09/13
 * Time: 08:54
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PassesFor {
       
    int count() default 10;
    
    TimeUnit timeUnit() default TimeUnit.SECONDS;
    
    int pollFrequencyInMilliseconds() default 200;
}
