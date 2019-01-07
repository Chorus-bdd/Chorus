package org.chorusbdd.chorus.site;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by nick on 13/03/15.
 */
public class Section {

    private int level;
    private String sectionName;

    private List<Page> pages = new LinkedList<Page>();

    public Section(int level, String sectionName) {
        this.level = level;
        this.sectionName = sectionName;
    }

    public void addPage(Page p) {
        pages.add(p);
    }

    public List<Page> getOrderedPages() {
        sortPages();
        return pages;
    }

    public String getSectionName() {
        return sectionName;
    }

    public int getLevel() {
        return level;
    }

    private void sortPages() {
        Collections.sort(pages, new Comparator<Page>() {
            @Override
            public int compare(Page o1, Page o2) {
                return ((Integer) o1.getIndex()).compareTo(o2.getIndex());
            }
        });
    }
}
