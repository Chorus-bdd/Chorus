/**
 *  Copyright (C) 2000-2014 The Software Conservancy and Original Authors.
 *  All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to
 *  deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 *  sell copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *  IN THE SOFTWARE.
 *
 *  Nothing in this notice shall be deemed to grant any rights to trademarks,
 *  copyrights, patents, trade secrets or any other intellectual property of the
 *  licensor or any contributor except as expressly stated herein. No patent
 *  license is granted separate from the Software, for code that you delete from
 *  the Software, or for combinations of the Software with other software or
 *  hardware.
 */
package org.chorusbdd.chorus.processes.processmanager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 23/07/13
 * Time: 21:26
 * To change this template use File | Settings | File Templates.
 */
public class ReadAheadBufferedStreamTest extends Assert {

    private final int READ_AHEAD_LIMIT = 128;
    ReadAheadBufferedStream is;

    @Before
    public void before() {
        InputStream i = getClass().getResourceAsStream("testContents.txt");
        is = new ReadAheadBufferedStream(i, 24, READ_AHEAD_LIMIT);        
    }
    
    @Test
    public void testReadAhead() {
        is.startReadAhead();
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        assertEquals("Should have read " + READ_AHEAD_LIMIT + " bytes", READ_AHEAD_LIMIT, is.getReadAheadBytesRead());
    }

    @Test
    public void testReadReset() throws IOException {
        is.startReadAhead();
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        assertEquals("Should have read " + READ_AHEAD_LIMIT + " bytes", READ_AHEAD_LIMIT, is.getReadAheadBytesRead());
        
        byte[] readBytes = new byte[30];
        is.read(readBytes, 0, 30);
        
        assertEquals("Should reset to start and contain first line", "Beware the Jabberwock, my son!", new String(readBytes).trim());

        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        assertEquals("Should have read " + READ_AHEAD_LIMIT + " bytes", READ_AHEAD_LIMIT, is.getReadAheadBytesRead());

        readBytes = new byte[43];
        is.read(readBytes, 0, 43);
        assertEquals("Should contain second line", "The jaws that bite, the claws that catch!", new String(readBytes).trim());
    }
    
    @After
    public void tearDown() {
        is.stopReadAhead();
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
    
    
    
}
