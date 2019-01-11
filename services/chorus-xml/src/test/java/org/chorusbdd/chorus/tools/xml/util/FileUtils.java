/**
 * MIT License
 *
 * Copyright (c) 2019 Chorus BDD Organisation.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
