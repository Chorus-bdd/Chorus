package processhandler;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 04/07/12
 * Time: 09:16
 */
public class StartAProcessMain {

    public static void main(String[] args) {


        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //write out all the arguments so we can test them
        for (String s : args) {
            System.out.println(s);
        }


        System.out.println("Woohoo, we have started a process");
        System.err.println("Eeek, an error might have occurred");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.flush();
        System.err.flush();
    }

}
