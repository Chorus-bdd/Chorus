package processhandler.processwithlog4jconfig;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 04/07/12
 * Time: 09:16
 */
public class ProcessWithLog4jMain {

    private static Logger l = Logger.getLogger(ProcessWithLog4jMain.class);

    public static void main(String[] args) throws InterruptedException {
        l.info("Started the process " + ProcessWithLog4jMain.class.getSimpleName());
    }

}
