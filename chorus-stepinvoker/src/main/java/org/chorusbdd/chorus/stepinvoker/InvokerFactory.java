package org.chorusbdd.chorus.stepinvoker;

import java.util.List;

/**
 * Created by nick on 21/10/2014.
 */
public interface InvokerFactory {

    List<StepInvoker> createStepInvokers();
}
