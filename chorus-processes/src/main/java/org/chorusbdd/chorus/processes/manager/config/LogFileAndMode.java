package org.chorusbdd.chorus.processes.manager.config;

import org.chorusbdd.chorus.processes.manager.config.OutputMode;

import java.io.File;

/**
* Created by nick on 10/11/14.
*/
public class LogFileAndMode {

    private final File file;
    private final OutputMode mode;
    private final String streamDescription;
    private final boolean isStdError;

    public LogFileAndMode(File file, OutputMode mode, String streamDescription, boolean isStdError) {
        this.file = file;
        this.mode = mode;
        this.streamDescription = streamDescription;
        this.isStdError = isStdError;
    }

    public OutputMode getMode() {
        return mode;
    }

    public File getFile() {
        return file;
    }

    public String getStreamDescription() {
        return streamDescription;
    }

    public boolean isStdError() {
        return isStdError;
    }

    @Override
    public String toString() {
        return "LogFileAndMode{" +
                "file=" + file +
                ", mode=" + mode +
                ", streamDescription='" + streamDescription + '\'' +
                '}';
    }

}
