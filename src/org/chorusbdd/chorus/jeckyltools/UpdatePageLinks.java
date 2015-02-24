package org.chorusbdd.chorus.jeckyltools;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by nick on 23/02/15.
 */
public class UpdatePageLinks {

    public static void main(String[] args) {
        File f = new File("pages");
        if ( ! f.canRead()) {
            System.err.println("Can't find pages dir");
            System.exit(1);
        }

        List<File> markdownFiles = findMarkdownFiles(f);
        updateLinks(f);
    }

    private static List<File> findMarkdownFiles(File f) {
        Path p = f.toPath();
        Files.find(p, )
    }

    private static void updateLinks(File f) {

    }
}
