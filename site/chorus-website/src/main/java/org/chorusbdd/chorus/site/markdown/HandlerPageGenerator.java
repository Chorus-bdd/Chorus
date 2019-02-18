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

public class HandlerPageGenerator {

    private ChorusLog log = ChorusLogFactory.getLog(HandlerPageGenerator.class);
    

    public void generateHandlerPages() throws IOException, TemplateException {
        HandlerAnnotationFilter handlerAnnotationFilter = new HandlerAnnotationFilter();
        ClassFilter filter = new ClassFilterDecorator().decorateWithPackageNameFilters(handlerAnnotationFilter, asList(ChorusConstants.BUILT_IN_PACKAGE_PREFIXES));

        Set<Class> classSet = ClasspathScanner.doScan(filter, log);

        for (Class handlerClass : classSet) {
            String handlerName = ((Handler) handlerClass.getAnnotation(Handler.class)).value();

            Map<String,Object> freemarkerModel = new HashMap<>();

            addSiteSectionProperties(handlerName, freemarkerModel);
            
            addHandlerSectionProperties(handlerClass, handlerName, freemarkerModel);

            writeDetailsPage(handlerName, freemarkerModel);
        }
    }

    private void addSiteSectionProperties(String handlerName, Map<String, Object> freemarkerModel) {
        Map<String,Object> siteProperties = new HashMap<>();
        freemarkerModel.put("site", siteProperties);
        siteProperties.put("section", handlerName);
        siteProperties.put("sectionIndex", 30);
    }

    private void addHandlerSectionProperties(Class handlerClass, String handlerName, Map<String, Object> freemarkerModel) throws IOException, TemplateException {
        Map<String,Object> handlerProperties = new HashMap<>();
        freemarkerModel.put("handler", handlerProperties);
        handlerProperties.put("name", handlerName);

        addHandlerConfigPropertyMetadata(handlerClass, handlerProperties);
        addHandlerStepMetadata(handlerClass, handlerProperties);
        addHandlerDocumentation(handlerClass, handlerProperties);
    }

    private void addHandlerDocumentation(Class handlerClass, Map<String, Object> handlerProperties) {
        String description = "";
        if ( handlerClass.isAnnotationPresent(Documentation.class)) {
            Documentation d = (Documentation)handlerClass.getAnnotation(Documentation.class);
            description = d.description();
        }
        handlerProperties.put("description", description);
    }

    private void addHandlerConfigPropertyMetadata(Class handlerClass, Map<String, Object> handlerProperties) throws IOException, TemplateException {
        
        List<FreemarkerTemplateConfigProperty> configProperties = Collections.emptyList();
        if (ConfigPropertySource.class.isAssignableFrom(handlerClass)) {
            try {
                ConfigPropertySource c = (ConfigPropertySource)handlerClass.getDeclaredConstructor().newInstance();
                configProperties = c.getConfigProperties()
                        .stream()
                        .map(FreemarkerTemplateConfigProperty::new)
                        .sorted(FreemarkerTemplateConfigProperty.getComparator())
                        .collect(Collectors.toList());
            } catch (Exception e) {
                log.error("Failed to instantiate handler class " + handlerClass, e);
            }
        }
        handlerProperties.put("configProperties", configProperties);

    }
    
    private void addHandlerStepMetadata(Class handlerClass, Map<String, Object> handlerProperties) throws IOException, TemplateException {

        Method[] methods = handlerClass.getMethods();
        List<FreemarkerTemplateStep> steps = Stream.of(methods)
                .filter(m -> m.isAnnotationPresent(Step.class))
                .map(m -> Tuple3.tuple3(m.getAnnotation(Step.class), m.isAnnotationPresent(Deprecated.class), m.isAnnotationPresent(Documentation.class) ? m.getAnnotation(Documentation.class) : null))
                .map(t -> new FreemarkerTemplateStep(t.getOne(), t.getTwo(), t.getThree()))
                .sorted(Comparator.comparing(FreemarkerTemplateStep::getOrder))
                .collect(Collectors.toList());
        handlerProperties.put("steps", steps);
    }

    private void writeDetailsPage(String handlerName, Map<String, Object> freemarkerModel) throws IOException, TemplateException {
        Configuration cfg = FreemarkerConfig.createFreemarkerConfiguration();

        String customTemplateName = handlerName.replace(" ", "") + "HandlerPageTemplate.ftl";
        String basicTemplateName = "basicHandlerPageTemplate.ftl";

        Template template;
        try {
            template = cfg.getTemplate(customTemplateName);
            log.info("Using custom handler template at " + customTemplateName);
        } catch (TemplateNotFoundException tnf) {
            log.info("Could not find a custom template at " + customTemplateName + " will use basic template for this handler");
            template = cfg.getTemplate(basicTemplateName);
        }
        
//        Writer out = new OutputStreamWriter(System.out);
        String handlerNameNoSpaces = handlerName.replace(" ", "");
        File ouputFile = new File("site/pages/BuiltInHandlers/" +  handlerNameNoSpaces, handlerNameNoSpaces + "HandlerDetails.md");
        FileWriter fileWriter = new FileWriter(ouputFile);
        template.process(freemarkerModel, fileWriter);
    }


    public static void main(String[] arga) throws IOException, TemplateException {
        new HandlerPageGenerator();
    }
}
