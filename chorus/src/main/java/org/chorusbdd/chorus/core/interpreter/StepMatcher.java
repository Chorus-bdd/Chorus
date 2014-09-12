package org.chorusbdd.chorus.core.interpreter;

import org.chorusbdd.chorus.core.interpreter.invoker.StepInvoker;
import org.chorusbdd.chorus.util.RegexpUtils;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;

import java.util.regex.Matcher;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 11/09/14
 * Time: 17:54
 *
 */
public class StepMatcher {

    private static ChorusLog log = ChorusLogFactory.getLog(RegexpUtils.class);

    /**
     * TODO
     * Yuk, this is ugly, we're doing two things here, both matching the step and funding the types of the
     * parameters, this should be split into separate functions
     *
     * Extracts the groups in a regular expression into typed data
     *
     * @return an array of typed data or null if regex does not match the text, or the types are not compatible
     */
    public static Object[] extractGroupsAndCheckMethodParams(StepInvoker invoker, String stepText) {
        Matcher matcher = invoker.getStepPattern().matcher(stepText);
        if (matcher.matches()) {
            int groupCount = matcher.groupCount();

            //check that there are the same number of expected values as there are regex groups
            if (groupCount != invoker.getParameterTypes().length) {
                //I think this is always an error in the handler's step definition - group should always match param count
                //it's worth logging it to warn level, or people may spend hours looking and may not spot the problem
                log.warn("Number of method parameters does not match regex groups");
                return null;
            }

            //collect the regex group values
            String[] regexGroupValues = new String[groupCount];
            for (int i = 0; i < groupCount; i++) {
                regexGroupValues[i] = matcher.group(i + 1);
            }

            //convert the strings from the regex groups into an Object[]
            Object[] values = new Object[groupCount];
            for (int i = 0; i < groupCount; i++) {
                String valueStr = regexGroupValues[i];
                Class type = invoker.getParameterTypes()[i];
                Object coercedValue = TypeCoercion.coerceType(valueStr, type);
                if (("null".equals(valueStr) && coercedValue == null ) || coercedValue != null) {
                    values[i] = coercedValue;
                } else {
                    //the type coercion failed for this method parameter
                    //return null to indicate this reg exp / method is not a match
                    //log at info level that we found a match but could not perform the coercion  - this will not show
                    //at the default log level warn, but will show as soon as user increases it
                    //It seems valid to support a form of method parameter overloading here, where two methods have
                    //the same regex but different class types for their parameters, logging at warn by default might
                    //get irritating in this case
                    log.info("Matched step but could not coerce " + valueStr + " to type " + type);
                    values = null;
                    break;
                }
            }
            return values;

        } else {
            return null;
        }
    }
}
