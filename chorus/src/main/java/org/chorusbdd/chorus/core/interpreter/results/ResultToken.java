package org.chorusbdd.chorus.core.interpreter.results;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 15/05/12
 * Time: 15:12
 *
 * All tokens should implement DeepCopy and Serializable
 */
public interface ResultToken extends Serializable, DeepCopy {

    /**
     * @return true if all necessary handlers and step definitions were available
     */
    boolean isFullyImplemented();

    /**
     * @return true if all executed steps passed (steps may be unimplemented or skipped and this will not cause test tests to 'fail')
     */
    boolean isPassed();
}
