package org.chorusbdd.chorus.core.interpreter.results;

/**
 * Rather than using clone (where the semantics of the copy are unclear), use this interface to indicate that the object
 * supports a deep copy method. Implementors must ensure that they return a copy of the current object whose state is
 * independent on the current object - i.e. all mutable fields should be copied by value, not reference.
 *
 * Created by: Steve Neal
 * Date: 17/01/12
 */
public interface DeepCopy<T> {
    public T deepCopy();
}
