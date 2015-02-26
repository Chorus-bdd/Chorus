package org.chorusbdd.chorus.jeckyltools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by nick on 23/02/15.
 */
public class UpdatePageLinks {

    public static void main(String[] args) throws IOException {
        File f = new File("pages");
        if ( ! f.canRead()) {
            System.err.println("Can't find pages dir");
            System.exit(1);
        }

        List<Path> markdownFiles = findMarkdownFiles(f);

        Map<String, Path> paths = new HashMap<String, Path>();
        markdownFiles.stream().forEach(p -> {
            paths.put(p.getFileName().toString(), p);
        });

        markdownFiles = findMarkdownFiles(new File(".")); //also need to update links on the index page which is not under pages dir

        markdownFiles.stream().forEach(p -> {
            try {
                Charset charset = StandardCharsets.UTF_8;
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

    private static List<Path> findMarkdownFiles(File f) throws IOException {
        Path p = f.toPath();
        return Files.find(
            p,
            Integer.MAX_VALUE,
            (path, attr) -> path.toString().endsWith("md")
        ).collect(Collectors.toList());
    }

}
