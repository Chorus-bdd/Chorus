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

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by nick on 23/02/15.
 */
public class UpdatePageLinks {

    public static final Charset charset = StandardCharsets.UTF_8;
    
    private static File siteDir = new File("site");

    public static void main(String[] args) throws IOException {
        File f = new File(siteDir, "pages");
        if ( ! f.canRead()) {
            System.err.println("Can't find pages dir");
            System.exit(1);
        }

        List<Path> markdownFiles = findMarkdownFiles(f);

        Map<String, Path> paths = new HashMap<String, Path>();
        markdownFiles.stream().forEach(p -> {
            paths.put(p.getFileName().toString(), p.subpath(1, p.getNameCount()));
        });

        generateSiteMap(markdownFiles);

        markdownFiles = findMarkdownFiles(new File(".")); //also need to update links on the index page which is not under pages dir

        markdownFiles.stream().forEach(p -> {
            try {
                String content = new String(Files.readAllBytes(p), charset);
                Pattern pattern = Pattern.compile("\\[(.*?)\\]\\((.*?)\\)");

                Matcher m = pattern.matcher(content);
                StringBuffer sb = new StringBuffer();
                while (m.find()) {
                   String address = m.group(2);
                   if ( ! address.contains("http")) {
                       File newPath = new File(address);
                       Path path = newPath.toPath();
                       String fileName = path.getFileName().toString() + ".md";
                       if ( paths.containsKey(fileName)) {
                           address = "/" + paths.get(fileName).toString().replaceAll("\\\\", "/").replace(".md", "");
                       }
                   }
                   m.appendReplacement(sb, "[" + m.group(1) + "](" + address + ")");
                }
                m.appendTail(sb);

                Files.write(p.resolveSibling(p.getFileName().toString()), sb.toString().getBytes(charset));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void generateSiteMap(List<Path> markdownFiles) throws IOException {
        LinkedHashMap<String, Section> sections = getSections();
        addPages(markdownFiles, sections);

        BufferedWriter siteMapContents = new BufferedWriter(new FileWriter(new File(siteDir,"pages" + File.separator + "siteMap.md")));
        siteMapContents.append("---\n");
        siteMapContents.append("layout: page\n");
        siteMapContents.append("title: Site Map\n");
        siteMapContents.append("---\n\n");
        for (Section s : sections.values()) {
            siteMapContents.append("###");
            if ( s.getLevel() > 1) {
                siteMapContents.append("# &nbsp;&nbsp;&nbsp;");
            } else {
                siteMapContents.append(" ");
            }
            siteMapContents.append(s.getSectionName());
            siteMapContents.write("\n\n");
            for ( Page p : s.getOrderedPages()) {
                String fileName = p.getPath().getFileName().toString();
                fileName = fileName.split(".md")[0];
                siteMapContents.append(" * [").append(p.getTitle()).append("](/").append(fileName).append(")\n");
            }
            siteMapContents.append("\n\n");
        }
        siteMapContents.close();
    }

    private static void addPages(List<Path> markdownFiles, LinkedHashMap<String, Section> sections) throws IOException {
        Pattern indexPattern = Pattern.compile("sectionIndex: (\\d+)");
        Pattern sectionPattern = Pattern.compile("section: (.*)");
        Pattern titlePattern = Pattern.compile("title: (.*)");
        for (Path p : markdownFiles) {
            Optional<Page> page = createPage(indexPattern, sectionPattern, titlePattern, p);
            page.ifPresent( pg -> {
                Section s = sections.get(pg.getSection());
                if ( s != null) {
                    s.addPage(pg);
                } else {
                    System.out.println("Could not find section " + pg.getSection() + " for page " + pg.getTitle() + " will omit");
                }
            });
        }
    }

    private static LinkedHashMap<String, Section> getSections() throws IOException {
        LinkedHashMap<String, Section> sections = new LinkedHashMap<>();

        try ( BufferedReader sectionReader = new BufferedReader(new FileReader(new File(siteDir, "sections.txt")));
              Stream<String> lines = sectionReader.lines(); ) {
            lines.map(l -> l.trim()).forEach(l -> {
                String[] s = l.split(",");
                sections.put(s[1], new Section(Integer.parseInt(s[0]), s[1]));
            });
        }
        return sections;
    }

    private static Optional<Page> createPage(Pattern indexPattern, Pattern sectionPattern, Pattern titlePattern, Path p) throws IOException {
        Optional<Page> page = Optional.empty();
        String content = new String(Files.readAllBytes(p), charset);
        Matcher indexMatcher = indexPattern.matcher(content);
        Matcher sectionMatcher = sectionPattern.matcher(content);
        Matcher titleMatcher = titlePattern.matcher(content);
        boolean indexFound = indexMatcher.find();
        boolean sectionFound = sectionMatcher.find();
        boolean titleFound = titleMatcher.find();
        if ( indexFound && sectionFound && titleFound) {
            String section = sectionMatcher.group(1).trim();
            int index = Integer.parseInt(indexMatcher.group(1).trim());
            String title = titleMatcher.group(1).trim();
            page = Optional.of(new Page(p, index, title, section));
        }
        return page;
    }

    private static List<Path> findMarkdownFiles(File f) throws IOException {
        Path p = f.toPath();
        return Files.find(
            p,
            Integer.MAX_VALUE,
            (path, attr) -> path.toString().endsWith("md")
        ).collect(Collectors.toList());
    }

}
