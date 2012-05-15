package handlers.configurations_for_processes;

import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

/**
 * Simple main class which will terminate itself after the number of seconds specified in the first
 * command line argument.
 * <p/>
 * Created by: Steve Neal
 * Date: 11/01/12
 */
public class Lemming {

    static ChorusLog log = ChorusLogFactory.getLog(Lemming.class);

    public static void main(String[] args) throws Exception {
        log.info("Lemming will sleep for " + args[0] + " seconds before dying");
        Thread.sleep(Integer.parseInt(args[0]) * 1000);
        System.exit(0);
    }
}
