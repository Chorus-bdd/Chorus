package org.chorusbdd.chorus.selftest.featurestartandend.featurestartscoping;

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.remoting.jmx.ChorusHandlerJmxExporter;

/**
 * Created by nick on 02/03/15.
 */
public class FeatureStartScopingMain {

    public FeatureStartScopingMain(String[] args) {
        if ( args.length == 1 && args[0].equals("scoped")) {
            new ChorusHandlerJmxExporter(new ScopedHandler()).export();
        } else {
            new ChorusHandlerJmxExporter(new NotScopedHandler()).export();
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Handler("NotScoped")
    public static class NotScopedHandler {

        @Step("I can call an exported method on myNotScoped")
        public String callAMethod() {
            return "OK";
        }
    }

    @Handler("Scoped")
    public static class ScopedHandler {

        @Step("I can call an exported method on scoped")
        public String callAMethod() {
            return "OK";
        }
    }

    public static void main(String[] args) {
        new FeatureStartScopingMain(args);
    }
}
