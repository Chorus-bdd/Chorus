package org.chorusbdd.chorus.handlers.processes;

import org.chorusbdd.chorus.util.assertion.ChorusAssert;
import org.chorusbdd.chorus.util.logging.ChorusLog;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: nick
 * Date: 24/07/13
 * Time: 08:55
 */
public abstract class AbstractChorusProcess implements ChorusProcess {

    //when we are in CAPTURED mode
    protected BufferedInputStream stdOutInputStream;
    private BufferedReader stdOutReader;

    protected BufferedInputStream stdErrInputStream;
    private BufferedReader stdErrReader;
    
    protected String name;
    private ProcessLogOutput logOutput;

    protected Process process;
    private OutputStream outputStream;
    private BufferedWriter outputWriter;

    public AbstractChorusProcess(String name, ProcessLogOutput logOutput) {
        this.name = name;
        this.logOutput = logOutput;
    }
    
    protected abstract ChorusLog getLog();

    protected void createCapturedErrReader(ProcessLogOutput logOutput, InputStream processErrorStream) {
        int readAheadBuffer = logOutput.getReadAheadBufferSize();
        if ( readAheadBuffer > 0 ) {
            getLog().trace("Creating new read ahead buffered error stream for process " + this + " std error in captured mode");
            this.stdErrInputStream = new ReadAheadBufferedStream(processErrorStream, 8192, logOutput.getReadAheadBufferSize()).startReadAhead();
        } else {
            getLog().trace("Creating new simple buffered input stream for process " + this + " std error in captured mode");
            this.stdErrInputStream = new BufferedInputStream(processErrorStream);
        }
        this.stdErrReader = new BufferedReader(new InputStreamReader(stdErrInputStream));
    }

    protected void createCapturedOutReader(ProcessLogOutput logOutput, InputStream processOutStream) {
        int readAheadBuffer = logOutput.getReadAheadBufferSize();
        if ( readAheadBuffer > 0 ) {
            getLog().trace("Creating new read ahead buffered input stream for process " + this + " std out in captured mode");
            this.stdOutInputStream = new ReadAheadBufferedStream(processOutStream, 8192, logOutput.getReadAheadBufferSize()).startReadAhead();
        } else {
            getLog().trace("Creating new simple buffered input stream for process " + this + " std out in captured mode");
            this.stdOutInputStream = new BufferedInputStream(processOutStream);
        }
        this.stdOutReader = new BufferedReader(new InputStreamReader(stdOutInputStream));
    }

    public String toString() {
        return getClass().getSimpleName() + "{" +
                "name='" + name + '}';
    }
    
    public void writeToStdIn(String line) {
        if ( outputStream == null) {
            outputStream = new BufferedOutputStream(process.getOutputStream());
            outputWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        }
        try {
            outputWriter.write(line);
            outputWriter.newLine();
            outputWriter.flush();
            outputStream.flush();
        } catch (IOException e) {
            getLog().debug("Error when writing to process in for " + this, e);
            ChorusAssert.fail("IOException when writing line to process");
        }
    }
    
    public void waitForLineMatchInStdOut(String pattern) {
        if ( stdOutInputStream == null) {
            InputStream inputStream = process.getInputStream();
            createCapturedOutReader(logOutput, inputStream);
        }
        Pattern p = Pattern.compile(pattern);
        
        long timeout = System.currentTimeMillis() + (logOutput.getReadTimeoutSeconds() * 1000);
        matchPattern(p, timeout);
    }

    private void matchPattern(Pattern p, long timeout) {
        try {
            while ( true) {
                checkTimeout(timeout, p);
                waitForLineTerminator(timeout, p);
                //since we know there is a line terminator ahead and the buffer has been reset
                //we know the next call to readLine will succeed and not block
                String line = stdOutReader.readLine();
                Matcher m = p.matcher(line);
                boolean matched = m.matches();
                if ( matched ) {
                    break;
                    //or we will be in the look until we timeout
                }
            }
        } catch (IOException e) {
            getLog().warn("Failed while matching pattern " + p, e);
            ChorusAssert.fail("Failed while matching pattern");
        }
    }

    //wait for line end by looking ahead without blocking
    private void waitForLineTerminator(long timeout, Pattern pattern) throws IOException {
        stdOutReader.mark(8192);
        label:
        while(true) {
            checkTimeout(timeout, pattern);
            while ( stdOutReader.ready() ) {
                int c = stdOutReader.read();
                if (c == '\n' || c == '\r' ) {
                    break label;    
                }
            } 

            try {
                Thread.sleep(10); //avoid a busy loop since we are using nonblocking ready() / read()
            } catch (InterruptedException e) {}
            
            if ( isStopped() && ! stdOutReader.ready()) {
                ChorusAssert.fail(
                    isExitCodeFailure() ? 
                        "Process stopped with error code " + getExitCode() + " while waiting for match" :
                        "Process stopped while waiting for match"
                );
            }
        }
        stdOutReader.reset();
    }

    private void checkTimeout(long timeout, Pattern pattern) {
        if ( System.currentTimeMillis() > timeout ) {
            ChorusAssert.fail("Timed out after " + logOutput.getReadTimeoutSeconds() + " seconds");
        }
    }

    public boolean isExitCodeFailure() {
        return process.exitValue() != 0;
    }
    
    public int getExitCode() {
        return process.exitValue();
    }


    protected void closeStreams() {
        if ( stdOutInputStream != null) {
            try {
                getLog().trace("Closing input stream for std out for process " + this);
                stdOutInputStream.close();
                stdOutReader.close();
                stdOutReader = null;
                stdOutInputStream = null;
            } catch (IOException e) {}
        }

        if ( stdErrInputStream != null) {
            try {
                getLog().trace("Closing input stream for std err for process " + this);
                stdErrInputStream.close();
                stdErrReader.close();
                stdErrInputStream = null;
                stdErrReader = null;
            } catch (IOException e) {}
        }
        
        if ( outputStream != null ) {
            try {
                getLog().trace("Closing output stream for process " + this);
                outputStream.close();
                outputWriter.close();
                outputStream = null;
                outputWriter = null;
            } catch (IOException e) {}
        }
    }
}
