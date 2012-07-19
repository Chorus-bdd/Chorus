package processhandler.processwithconfigurations;

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
        Thread.sleep(10000);
    }

}
