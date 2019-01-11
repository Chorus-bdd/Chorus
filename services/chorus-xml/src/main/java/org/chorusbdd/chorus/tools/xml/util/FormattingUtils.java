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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * User: nick
 * Date: 31/12/12
 * Time: 00:44
 */
public class FormattingUtils {

    private static final ThreadLocal<SimpleDateFormat> startTimeFormatter = new ThreadLocal<SimpleDateFormat>() {
        public SimpleDateFormat initialValue() {
            return new SimpleDateFormat("dd MMM yyyy HH:mm:ss zzz");
        }
    };

    private static ThreadLocal<DecimalFormat> secondsFormatter = new ThreadLocal<DecimalFormat>() {
        public DecimalFormat initialValue() {
            return new DecimalFormat("##########.#");
        }
    };

    public static SimpleDateFormat getStartTimeFormatter() {
        return startTimeFormatter.get();
    }

    public static DecimalFormat getSecondsFormatter() {
        return secondsFormatter.get();
    }

    public static String getTimeTakenAsSecondsString(long timeTakenMillis) {
        String result = "0";
        if ( timeTakenMillis > 0) {
            float timeSeconds = timeTakenMillis / 1000f;
            result = getSecondsFormatter().format(timeSeconds);
        }
        return result;
    }

    public static String getAsCsv(String[] usesHandlers) {
        StringBuilder sb = new StringBuilder();
        List<String> l = Arrays.asList(usesHandlers);
        Iterator<String> i = l.iterator();
        while(i.hasNext()) {
            sb.append(i.next());
            if ( i.hasNext()) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public static String getAsCsv(List<String> tags) {
        return getAsCsv(tags.toArray(new String[tags.size()]));
    }

    public static List<String> getStringListFromCsv(String tags) {
        return Arrays.asList(tags.split("\\s*,\\s*"));
    }

    public static String[] getStringArrayFromCsv(String tags) {
        List<String> l = getStringListFromCsv(tags);
        return l.toArray(new String[l.size()]);
    }


}
