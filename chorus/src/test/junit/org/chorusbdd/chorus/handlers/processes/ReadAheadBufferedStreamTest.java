package org.chorusbdd.chorus.handlers.processes;

import junit.framework.TestCase;
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
        
        assertEquals("Should reset to start and contain first line", "Beware the Jabberwock, my son!", new String(readBytes));

        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        assertEquals("Should have read " + READ_AHEAD_LIMIT + " bytes", READ_AHEAD_LIMIT, is.getReadAheadBytesRead());

        readBytes = new byte[43];
        is.read(readBytes, 0, 43);
        assertEquals("Should contain second line", "\r\nThe jaws that bite, the claws that catch!", new String(readBytes));
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
