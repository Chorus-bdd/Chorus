package org.chorusbdd.chorus.selftest;

import java.io.IOException;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 05/07/12
 * Time: 11:41
 * To change this template use File | Settings | File Templates.
 */
public interface ChorusSelfTestRunner {

    ChorusSelfTestResults runChorusInterpreter(Properties systemProperties) throws Exception;
}
