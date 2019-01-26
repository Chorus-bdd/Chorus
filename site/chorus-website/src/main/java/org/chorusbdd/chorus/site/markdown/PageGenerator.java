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
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.handlerconfig.ConfigPropertySource;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.pathscanner.ClasspathScanner;
import org.chorusbdd.chorus.pathscanner.filter.ClassFilter;
import org.chorusbdd.chorus.pathscanner.filter.ClassFilterDecorator;
import org.chorusbdd.chorus.pathscanner.filter.HandlerAnnotationFilter;
import org.chorusbdd.chorus.util.ChorusConstants;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class PageGenerator {


    private ChorusLog log = ChorusLogFactory.getLog(PageGenerator.class);
    
    public PageGenerator() throws IOException, TemplateException {


        HandlerAnnotationFilter handlerAnnotationFilter = new HandlerAnnotationFilter();
        ClassFilter filter = new ClassFilterDecorator().decorateWithPackageNameFilters(handlerAnnotationFilter, asList(ChorusConstants.BUILT_IN_PACKAGE_PREFIXES));

        Set<Class> classSet = ClasspathScanner.doScan(filter, log);
        
        for (Class handlerClass : classSet) {
            Map<String,Object> freemarkerModel = new HashMap<>();

            String handlerName = ((Handler) handlerClass.getAnnotation(Handler.class)).value();
            Map<String,Object> handlerProperties = new HashMap<>();
            handlerProperties.put("name", handlerName);
            freemarkerModel.put("handler", handlerProperties);
            
            Map<String,Object> siteProperties = new HashMap<>();
            siteProperties.put("section", "Handlers");
            siteProperties.put("sectionIndex", 30);
            freemarkerModel.put("site", siteProperties);

            String handlerNameNoSpaces = handlerName.replace(" ", "");
            File ouputFile = new File("site/pages/BuiltInHandlers/" +  handlerNameNoSpaces, handlerNameNoSpaces + "ConfigProperties.md");
            generateHandlerPropertiesPage(handlerClass, freemarkerModel, handlerProperties, ouputFile);
        }
    }

    private void generateHandlerPropertiesPage(Class handlerClass, Map<String, Object> freemarkerModel, Map<String, Object> handlerProperties, File ouputFile) throws IOException, TemplateException {
        Configuration cfg = FreemarkerConfig.createFreemarkerConfiguration();
        
        List<TemplateConfigProperty> configProperties = Collections.emptyList();
        if (ConfigPropertySource.class.isAssignableFrom(handlerClass)) {
            try {
                ConfigPropertySource c = (ConfigPropertySource)handlerClass.getDeclaredConstructor().newInstance();
                configProperties = c.getConfigProperties()
                        .stream()
                        .map(TemplateConfigProperty::new)
                        .sorted(TemplateConfigProperty.getComparator())
                        .collect(Collectors.toList());
            } catch (Exception e) {
                log.error("Failed to instantiate handler class " + handlerClass, e);
            }
        }
        handlerProperties.put("configProperties", configProperties);

        Template template = cfg.getTemplate("handlerPropertiesPageTemplate.ftl");
//        Writer out = new OutputStreamWriter(System.out);
        FileWriter fileWriter = new FileWriter(ouputFile);
        template.process(freemarkerModel, fileWriter);
    }


    public static void main(String[] arga) throws IOException, TemplateException {
        new PageGenerator();
    }
}
