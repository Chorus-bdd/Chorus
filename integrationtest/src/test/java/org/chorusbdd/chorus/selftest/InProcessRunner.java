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
package org.chorusbdd.chorus.selftest;

import org.chorusbdd.chorus.Chorus;
import org.chorusbdd.chorus.logging.ChorusOut;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Nick E
 * Date: 05/07/12
 * Time: 11:42
 *
 * Fork an interpreter process and capture the std out, std err and exit code into ChorusSelfTestResults
 */
public class InProcessRunner implements ChorusSelfTestRunner {

    public ChorusSelfTestResults runChorusInterpreter(Properties sysPropsForTest) {

         setSystemProperties(sysPropsForTest);

         ByteArrayOutputStream out = new ByteArrayOutputStream();
         PrintStream outStream = new PrintStreamMultiplexer(out, System.out);

         ByteArrayOutputStream err = new ByteArrayOutputStream();
         PrintStream errStream = new PrintStreamMultiplexer(err, System.err);

         boolean success = false;
         try {
             ChorusOut.setStdOutStream(outStream);
             ChorusOut.setStdErrStream(errStream);

             try {
                Chorus chorus = new Chorus(new String[0]);
                success = chorus.run();
             } catch (Exception e) {
                System.err.println("Failed while running tests in line " + e.getMessage() + e);
                e.printStackTrace();
             }

         } finally {
             ChorusOut.setStdOutStream(System.out);
             ChorusOut.setStdErrStream(System.err);
         }

         return new ChorusSelfTestResults(out.toString(), err.toString(), success ? 0 : 1);
    }


    private void setSystemProperties(Properties testProperties) {
        clearChorusProperties();
        Iterator<Map.Entry<Object, Object>> i;


        //set new chorus sys props
        i = testProperties.entrySet().iterator();
        while(i.hasNext()) {
            Map.Entry<Object,Object> e = i.next();
            System.setProperty(e.getKey().toString(), e.getValue().toString());
        }
    }

    public static void clearChorusProperties() {
        //clear any existing chorus sys props
        Iterator<Map.Entry<Object,Object>> i = System.getProperties().entrySet().iterator();
        while(i.hasNext()) {
            Map.Entry<Object,Object> e = i.next();
            if ( e.getKey().toString().startsWith("chorus")) {
                i.remove();
            }
        }
    }

    private static class PrintStreamMultiplexer extends PrintStream {

        private PrintStream wrappedStream;

        public PrintStreamMultiplexer(ByteArrayOutputStream out, PrintStream wrappedStream) {
            super(out);
            this.wrappedStream = wrappedStream;
        }

        public void write(int b) {
            wrappedStream.write(b);
            super.write(b);
        }

        public void write(byte buf[], int off, int len) {
            wrappedStream.write(buf, off, len);
            super.write(buf, off, len);
        }

        @Override
        public void print(boolean b) {
            wrappedStream.print(b);
            super.print(b);
        }

        @Override
        public void print(char c) {
            wrappedStream.print(c);
            super.print(c);
        }

        @Override
        public void print(int i) {
            wrappedStream.print(i);
            super.print(i);
        }

        @Override
        public void print(long l) {
            wrappedStream.print(l);
            super.print(l);
        }

        @Override
        public void print(float f) {
            wrappedStream.print(f);
            super.print(f);
        }

        @Override
        public void print(double d) {
            wrappedStream.print(d);
            super.print(d);
        }

        @Override
        public void print(char[] s) {
            wrappedStream.print(s);
            super.print(s);
        }

        @Override
        public void print(String s) {
            wrappedStream.print(s);
            super.print(s);
        }

        @Override
        public void print(Object obj) {
            wrappedStream.print(obj);
            super.print(obj);
        }

        @Override
        public void println() {
            wrappedStream.println();
            super.println();
        }

        @Override
        public void println(boolean x) {
            wrappedStream.print(x);
            super.println(x);
        }

        @Override
        public void println(char x) {
            wrappedStream.println(x);
            super.println(x);
        }

        @Override
        public void println(int x) {
            wrappedStream.println(x);
            super.println(x);
        }

        @Override
        public void println(long x) {
            wrappedStream.println(x);
            super.println(x);
        }

        @Override
        public void println(float x) {
            wrappedStream.println(x);
            super.println(x);
        }

        @Override
        public void println(double x) {
            wrappedStream.println(x);
            super.println(x);
        }

        @Override
        public void println(char[] x) {
            wrappedStream.println(x);
            super.println(x);
        }

        @Override
        public void println(String x) {
            wrappedStream.println(x);
            super.println(x);
        }

        @Override
        public void println(Object x) {
            wrappedStream.println(x);
            super.println(x);
        }

        @Override
        public PrintStream printf(String format, Object... args) {
            wrappedStream.printf(format, args);
            return super.printf(format, args);
        }

        @Override
        public PrintStream printf(Locale l, String format, Object... args) {
            wrappedStream.printf(l, format, args);
            return super.printf(l, format, args);
        }

        @Override
        public PrintStream format(String format, Object... args) {
            wrappedStream.format(format, args);
            return super.format(format, args);
        }

        @Override
        public PrintStream format(Locale l, String format, Object... args) {
            wrappedStream.format(l, format, args);
            return super.format(l, format, args);
        }

        @Override
        public PrintStream append(CharSequence csq) {
            wrappedStream.append(csq);
            return super.append(csq);
        }

        @Override
        public PrintStream append(CharSequence csq, int start, int end) {
            wrappedStream.append(csq, start, end);
            return super.append(csq, start, end);
        }

        @Override
        public PrintStream append(char c) {
            wrappedStream.append(c);
            return super.append(c);
        }
    }
}
