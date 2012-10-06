package org.chorusbdd;

/**
 * Created with IntelliJ IDEA.
 * User: Nick
 * Date: 06/10/12
 * Time: 10:29
 *
 * Alternative bootstrapping class for Chorus interpreter
 * So we are supporting -
 *
 * short
 * java org.chorusbdd.Chorus
 * or long
 * java org.chorusbdd.chorus.Chorus
 */
public class Chorus {

    public static void main(String[] args) throws Exception {
        org.chorusbdd.chorus.Chorus.main(args);
    }
}
