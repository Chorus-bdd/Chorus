package org.chorusbdd.chorus.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 11/05/12
 * Time: 17:26
 *
 * Use to indicate that Chorus should attempt to load a Spring context and associate it with the annotated
 * Handler class, injecting resources into fields indicated by @Resource annotations
 */
@Target(ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface SpringContext {

    java.lang.String[] value() default {};
}