package uk.co.smartkey.chorus.remoting.jmx;

import uk.co.smartkey.chorus.calculator.CalculatorHandler;

/**
 * Created by: Steve Neal
 * Date: 14/10/11
 */
public class RunChorusJmxFeatureExporter {
    public static void main(String[] args) throws Exception {
        new ChorusHandlerJmxExporter(new CalculatorHandler());
        Thread.sleep(1000 * 60 * 30); //30 minutes
    }
}
