package org.chorusbdd.chorus.tools.xml.util;

import java.io.*;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 18/12/12
 * Time: 22:16
 * To change this template use File | Settings | File Templates.
 */
public class FileUtils {

    public static String readToString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            String s = r.readLine();
            while( s != null) {
                sb.append(s + "\n");
                s = r.readLine();
            }
        } finally {
            try {
                if ( is != null) {
                    is.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * @return a file path with correct platform file separators constructed from the pathElement list
     */
    public static String getFilePath(String... pathElements) {
        StringBuilder path = new StringBuilder();
        Iterator<String> i = Arrays.asList(pathElements).iterator();
        while(i.hasNext()) {
            path.append(i.next());
            if ( i.hasNext()) {
                path.append(File.separator);
            }
        }
        return path.toString();
    }
}
