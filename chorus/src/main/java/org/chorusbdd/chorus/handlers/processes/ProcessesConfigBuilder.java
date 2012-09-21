package org.chorusbdd.chorus.handlers.processes;

import org.chorusbdd.chorus.ChorusException;
import org.chorusbdd.chorus.handlers.util.AbstractHandlerConfigBuilder;
import org.chorusbdd.chorus.handlers.util.HandlerConfigBuilder;

import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 21/09/12
 * Time: 11:22
 * To change this template use File | Settings | File Templates.
 */
public class ProcessesConfigBuilder extends AbstractHandlerConfigBuilder implements HandlerConfigBuilder<ProcessesConfig> {

    public ProcessesConfig createConfig(Properties p) {
        ProcessesConfig c = new ProcessesConfig();

        for (Map.Entry prop : p.entrySet()) {
            String key = prop.getKey().toString();
            String value = prop.getValue().toString();

            if ("name".equals(key)) {
                c.setName(value);
            } else if ("jre".equals(key)) {
                c.setJre(value);
            } else if ("classpath".equals(key)) {
                c.setClasspath(value);
            } else if ("args".equals(key)) {
                c.setArgs(value);
            } else if ("jvmargs".equals(key)) {
                c.setJvmargs(value);
            } else if ("mainclass".equals(key)) {
                c.setMainclass(value);
            } else if ("jmxport".equals(key)) {
                c.setJmxPort(parseIntProperty(value, "jmxport"));
            } else if ("debugport".equals(key)) {
                c.setDebugPort(parseIntProperty(value, "debugport"));
            } else if ("terminateWaitTime".equals(key)) {
                c.setTerminateWaitTime(parseIntProperty(value, "terminateWaitTime"));
            } else if ("logging".equals(key)) {
                c.setLogging(parseBooleanProperty(value, "logging"));
            }
        }
        return c;
    }

}
