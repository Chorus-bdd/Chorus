package org.chorusbdd.chorus.site;

import java.nio.file.Path;

/**
 * Created by nick on 13/03/15.
 */
public class Page {

    private final Path path;
    private final int index;
    private String title;
    private String section;

    public Page(Path path, int index, String title, String section) {
        this.path = path;
        this.index = index;
        this.title = title;
        this.section = section;
    }

    public Path getPath() {
        return path;
    }

    public int getIndex() {
        return index;
    }

    public String getTitle() {
        return title;
    }

    public String getSection() {
        return section;
    }
}
