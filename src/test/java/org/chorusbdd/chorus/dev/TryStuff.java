package org.chorusbdd.chorus.dev;

import java.io.FileReader;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Created by: Steve Neal
 * Date: 16/11/11
 */
public class TryStuff {
    public static void main(String[] args) throws Exception {
        Pattern p = Pattern.compile("the '(.*)' portfolio has (spread|quantity) multiplier of ([0-9]+\\.[0-9]{1})");
        Matcher m = p.matcher("the '< 4 yrs' portfolio has spread multiplier of 0.5");
        System.out.println(m.matches());
        System.out.println(m.group(1));
        System.out.println(m.group(2));
        System.out.println(m.group(3));
    }

    public static List<String> getTableRowData(String line) {
        String[] headers = line.trim().split("\\|");
        List<String> results = new ArrayList<String>();
        for (String header : headers) {
            if (header.trim().length() > 0) {
                results.add(header.trim());
            }
        }
        return results;
    }
}
