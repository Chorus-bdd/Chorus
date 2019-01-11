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
