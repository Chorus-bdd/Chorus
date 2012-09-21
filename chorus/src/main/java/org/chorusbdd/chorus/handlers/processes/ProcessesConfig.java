package org.chorusbdd.chorus.handlers.processes;

import org.chorusbdd.chorus.handlers.util.config.HandlerConfig;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 21/09/12
 * Time: 11:08
 * To change this template use File | Settings | File Templates.
 */
public class ProcessesConfig implements HandlerConfig {

    private static ChorusLog log = ChorusLogFactory.getLog(ProcessesConfig.class);

    private String name;
    private String jre = System.getProperty("java.home");
    private String classpath = System.getProperty("java.class.path");
    private String jvmargs;
    private String mainclass;
    private String args;
    private boolean logging;
    private int jmxPort = -1;
    private int debugPort = -1;
    private int terminateWaitTime = 30;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJre() {
        return jre;
    }

    public void setJre(String jre) {
        this.jre = jre;
    }

    public String getClasspath() {
        return classpath;
    }

    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }

    public String getJvmargs() {
        return jvmargs == null ? "" : jvmargs;
    }

    public void setJvmargs(String jvmargs) {
        this.jvmargs = jvmargs;
    }

    public String getMainclass() {
        return mainclass;
    }

    public void setMainclass(String mainclass) {
        this.mainclass = mainclass;
    }

    public String getArgs() {
        return args == null ? "" : args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public boolean isLogging() {
        return logging;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    public int getJmxPort() {
        return jmxPort;
    }

    public void setJmxPort(int jmxPort) {
        this.jmxPort = jmxPort;
    }

    public int getDebugPort() {
        return debugPort;
    }

    public void setDebugPort(int debugPort) {
        this.debugPort = debugPort;
    }

    public int getTerminateWaitTime() {
        return terminateWaitTime;
    }

    public void setTerminateWaitTime(int terminateWaitTime) {
        this.terminateWaitTime = terminateWaitTime;
    }

    public boolean isValid() {
        boolean valid = true;
        if ( name == null || name.trim().length() == 0) {
            log.warn("invalid config, name was null or empty");
            valid = false;
        }
        if ( jre == null || ! new File(jre).isDirectory() ) {
            log.warn("invalid config, jre property is null or jre path does not exist");
            valid = false;
        }
        if ( classpath == null ) {
            log.warn("invalid config, classpath was null");
            valid = false;
        }
        if ( mainclass == null || mainclass.trim().length() == 0 ) {
            log.warn("invalid config, main class was null or empty");
            valid = false;
        }
        return valid;
    }

    @Override
    public String toString() {
        return "ProcessesConfig{" +
                "name='" + name + '\'' +
                ", jre='" + jre + '\'' +
                ", classpath='" + classpath + '\'' +
                ", jvmargs='" + jvmargs + '\'' +
                ", mainclass='" + mainclass + '\'' +
                ", args='" + args + '\'' +
                ", logging=" + logging +
                ", jmxPort=" + jmxPort +
                ", debugPort=" + debugPort +
                ", terminateWaitTime=" + terminateWaitTime +
                '}';
    }
}
