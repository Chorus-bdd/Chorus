package org.chorusbdd.chorus.handlers.processes;

import org.chorusbdd.chorus.core.interpreter.ChorusContext;
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
    private InputStreamAndReader stdOutInputStreams;
    private InputStreamAndReader stdErrInputStreams;

    private OutputStream outputStream;
    private BufferedWriter outputWriter;
    
    protected String name;
    private ProcessLogOutput logOutput;

    protected Process process;

    public AbstractChorusProcess(String name, ProcessLogOutput logOutput) {
        this.name = name;
        this.logOutput = logOutput;
    }
    
    protected abstract ChorusLog getLog();

    public String toString() {
        return getClass().getSimpleName() + "{" +
                "name='" + name + '}';
    }

    /**
     * Write the text to the std in of the process
     * @param newLine append a new line
     */
    public void writeToStdIn(String text, boolean newLine) {
        if ( outputStream == null) {
            outputStream = new BufferedOutputStream(process.getOutputStream());
            outputWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        }
        
        try {
            outputWriter.write(text);
            if ( newLine ) {
                outputWriter.newLine();
            }
            outputWriter.flush();
            outputStream.flush();
        } catch (IOException e) {
            getLog().debug("Error when writing to process in for " + this, e);
            ChorusAssert.fail("IOException when writing line to process");
        }
    }
    
    public void waitForMatchInStdOut(String pattern, boolean searchWithinLines) {
        if ( stdOutInputStreams == null) {
            stdOutInputStreams = new InputStreamAndReader("Std Out");
            InputStream inputStream = process.getInputStream();
            stdOutInputStreams.createStreams(logOutput, inputStream);
        }
        waitForOutputPattern(pattern, stdOutInputStreams, false);
    }

    public void waitForMatchInStdErr(String pattern, boolean searchWithinLines) {
        if ( stdErrInputStreams == null) {
            stdErrInputStreams = new InputStreamAndReader("Std Err");
            InputStream inputStream = process.getErrorStream();
            stdErrInputStreams.createStreams(logOutput, inputStream);
        }
        waitForOutputPattern(pattern, stdErrInputStreams, false);
    }

    private void waitForOutputPattern(String pattern, InputStreamAndReader i, boolean searchWithinLines) {
        Pattern p = Pattern.compile(pattern);
        long timeout = System.currentTimeMillis() + (logOutput.getReadTimeoutSeconds() * 1000);
        try {
            String matched = waitForPattern(timeout, i.reader, p, searchWithinLines);
            
            //store into the ChorusContext the exact string which matched the pattern so this can be used
            //in subsequent test steps
            ChorusContext.getContext().put("ProcessesHandler.match", matched);
        } catch (IOException e) {
            getLog().warn("Failed while matching pattern " + p, e);
            ChorusAssert.fail("Failed while matching pattern");
        }
    }

    //read ahead without blocking and attempt to match the pattern
    private String waitForPattern(long timeout, BufferedReader bufferedReader, Pattern pattern, boolean searchWithinLines) throws IOException {
        StringBuilder sb = new StringBuilder();
        String result = null;
        label:
        while(true) {
            while ( bufferedReader.ready() ) {
                int c = bufferedReader.read();
                if ( c == -1 ) {
                    ChorusAssert.fail("End of stream while waiting for match");
                }
                
                if (c == '\n' || c == '\r' ) {
                    if ( sb.length() > 0) {
                        Matcher m = pattern.matcher(sb);
                        if ( m.matches() ) {
                            result = sb.toString();
                            break label;    
                        } else {
                            sb.setLength(0);
                        }
                    }
                } else {
                    sb.append((char)c);                    
                }
            } 
            
            //nothing more to read, does the current output match the pattern?
            if ( searchWithinLines) {
                Matcher m = pattern.matcher(sb);
                if ( m.matches() ) {
                    result = sb.toString();
                    break label;
                }
            }
            
            try {
                Thread.sleep(10); //avoid a busy loop since we are using nonblocking ready() / read()
            } catch (InterruptedException e) {}

            checkTimeout(timeout);

            if ( isStopped() && ! bufferedReader.ready()) {
                ChorusAssert.fail(
                    isExitCodeFailure() ? 
                        "Process stopped with error code " + getExitCode() + " while waiting for match" :
                        "Process stopped while waiting for match"
                );
            }
        }
        return result;
    }

    private void checkTimeout(long timeout) {
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
        if ( stdOutInputStreams != null) {
           stdOutInputStreams.close();
        }

        if ( stdErrInputStreams != null) {
          stdErrInputStreams.close();
        }
        
        if ( outputStream != null ) {
            try {
                getLog().trace("Closing output stream for process " + this);
                outputStream.close();
                outputStream = null;
            } catch (IOException e) {}
        }

        if ( outputWriter != null ) {
            try {
                getLog().trace("Closing output writer for process " + this);
                outputWriter.close();
                outputWriter = null;
            } catch (IOException e) {}
        }
    }


    /**
     * Input stream and reader for process std out or std error
     */
    private class InputStreamAndReader {
        
        String name;
        BufferedInputStream inputStream;
        BufferedReader reader;

        private InputStreamAndReader(String name) {
            this.name = name;
        }

        void createStreams(ProcessLogOutput logOutput, InputStream processErrorStream) {
            int readAheadBuffer = logOutput.getReadAheadBufferSize();
            if ( readAheadBuffer > 0 ) {
                getLog().trace("Creating new read ahead buffered input stream " + name + " for process " + this + " in captured mode");
                inputStream = new ReadAheadBufferedStream(processErrorStream, 8192, logOutput.getReadAheadBufferSize()).startReadAhead();
            } else {
                getLog().trace("Creating new simple buffered input stream " + name + " for process " + this + " in captured mode");
                inputStream = new BufferedInputStream(processErrorStream);
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
        }

        void close() {
            if ( inputStream != null) {
                try {
                    getLog().trace("Closing input stream " + name + " for process " + this);
                    inputStream.close();
                } catch (IOException e) {}
            }
            
            if ( reader != null ) {
                try {
                    getLog().trace("Closing reader " + name + " for process " + this);
                    reader.close();
                } catch (IOException e) {}            
            }
        }
    }
}
