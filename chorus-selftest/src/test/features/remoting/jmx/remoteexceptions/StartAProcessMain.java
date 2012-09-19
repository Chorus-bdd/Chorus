package remoting.jmx.remoteexceptions;

import org.chorusbdd.chorus.remoting.jmx.ChorusHandlerJmxExporter;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 04/07/12
 * Time: 09:16
 */
public class StartAProcessMain {

    public static void main(String[] args) throws InterruptedException {

        //write out all the arguments so we can test them
        for (String s : args) {
            System.out.println(s);
        }

        System.out.println("Exporting the handler");
        JmxRemoteExceptionsHandler handler = new JmxRemoteExceptionsHandler();
        ChorusHandlerJmxExporter exporter = new ChorusHandlerJmxExporter(handler);
        exporter.export();

        Thread.sleep(60000);
    }

}
