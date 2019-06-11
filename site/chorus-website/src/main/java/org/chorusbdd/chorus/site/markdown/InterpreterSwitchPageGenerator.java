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
package org.chorusbdd.chorus.site.markdown;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import org.chorusbdd.chorus.annotations.Documentation;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.handlerconfig.ConfigPropertySource;
import org.chorusbdd.chorus.interpreter.startup.ChorusConfigProperty;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.pathscanner.ClasspathScanner;
import org.chorusbdd.chorus.pathscanner.filter.ClassFilter;
import org.chorusbdd.chorus.pathscanner.filter.ClassFilterDecorator;
import org.chorusbdd.chorus.pathscanner.filter.HandlerAnnotationFilter;
import org.chorusbdd.chorus.util.ChorusConstants;
import org.chorusbdd.chorus.util.function.Tuple3;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

public class InterpreterSwitchPageGenerator {

    private ChorusLog log = ChorusLogFactory.getLog(InterpreterSwitchPageGenerator.class);
    
    public void generateSwitchPage() throws IOException, TemplateException {
        Map<String,Object> freemarkerModel = new HashMap<>();
        addSiteSectionProperties(freemarkerModel);
        addSwitchProperties(freemarkerModel);
        writeDetailsPage(freemarkerModel);
    }

    private void addSwitchProperties(Map<String, Object> freemarkerModel) {
        Map<String,Object> interpreterProperties = new HashMap<>();
        freemarkerModel.put("interpreter", interpreterProperties);
        
        List<FreemarkerInterpreterSwitch> switches = Stream.of(ChorusConfigProperty.values())
                .map(FreemarkerInterpreterSwitch::new)
                .collect(Collectors.toList());
        
        interpreterProperties.put("switches", switches);
    }


    private void addSiteSectionProperties(Map<String, Object> freemarkerModel) {
        Map<String,Object> siteProperties = new HashMap<>();
        freemarkerModel.put("site", siteProperties);
        siteProperties.put("section", "Running Chorus");
        siteProperties.put("sectionIndex", 40);
    }


    private void writeDetailsPage(Map<String, Object> freemarkerModel) throws IOException, TemplateException {
        Configuration cfg = FreemarkerConfig.createFreemarkerConfiguration();

        String templateName = "InterpreterSwitches.ftl";

        Template template = cfg.getTemplate(templateName);
        log.info("Using custom handler template at " + templateName);

//        Writer out = new OutputStreamWriter(System.out);
        File ouputFile = new File("site/pages/RunningChorus/InterpreterSwitches.md");
        FileWriter fileWriter = new FileWriter(ouputFile);
        template.process(freemarkerModel, fileWriter);
    }
}
