package org.chorusbdd.chorus.pathscanner;

import org.chorusbdd.chorus.util.function.Supplier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

/**
* Created by nick on 15/04/15.
*/
public class FileReaderSupplier implements Supplier<Reader> {

    private final File featureFile;

    public FileReaderSupplier(File featureFile) {
        this.featureFile = featureFile;
    }

    @Override
    public Reader get() {
        try {
            return new FileReader(featureFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Could not find file for feature parsing", e);
        }
    }
}
