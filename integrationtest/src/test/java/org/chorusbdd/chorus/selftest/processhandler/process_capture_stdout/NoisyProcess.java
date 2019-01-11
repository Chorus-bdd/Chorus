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
package org.chorusbdd.chorus.selftest.processhandler.process_capture_stdout;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 04/07/12
 * Time: 09:16
 */
public class NoisyProcess {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Start with this");
        writeManyLinesOfNoisyGibberish();
        System.out.println("Then read this");
        writeManyLinesOfNoisyGibberish();
        System.out.println("Finally a big fat pattern");
        writeManyLinesOfNoisyGibberish();
        
        //allow the timeout scenario more time to timeout when looking for a match which never arrives 
        Thread.sleep(2000);
    }

    private static void writeManyLinesOfNoisyGibberish() {
        long stopTime = System.currentTimeMillis() + 500;
        int written = 0;
        while ( System.currentTimeMillis() < stopTime) {
            StringBuilder sb = new StringBuilder();
            int lineLength = (int)(Math.random() * 10000);
            for ( int c = 0 ; c < lineLength ; c ++) {
                sb.append((char)(Math.random() * 1000));
            }
            System.out.println(sb.toString());
            written++;
        }
//        System.err.println("written : " + written);
    }

}
