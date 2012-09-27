package org.chorusbdd.chorus.selftest;

import org.chorusbdd.chorus.util.assertion.ChorusAssert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 27/09/12
 * Time: 08:35
 */
public class SelftestUtils {

    public static void checkFileContainsLine(String line, String path) {
           BufferedReader r = null;
           try {
               r = new BufferedReader(new FileReader(new File(path)));
               line = line.trim();

               String l = r.readLine();
               boolean result = false;
               while(l != null) {
                   if ( line.equals(l.trim())) {
                       result = true;
                       break;
                   }
                   l = r.readLine();
               }
               ChorusAssert.assertTrue("check contains line", result);
           } catch ( Exception e) {
               ChorusAssert.fail("File at path " + path + " did not exist or did not contain the line " + line);
           } finally {
               if ( r != null) {
                   try {
                       r.close();
                   } catch (Exception e) {
                   }
               }
           }
       }
}
