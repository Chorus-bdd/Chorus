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
import freemarker.template.TemplateExceptionHandler;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.handlerconfig.ConfigPropertySource;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigurationProperty;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.pathscanner.ClasspathScanner;
import org.chorusbdd.chorus.pathscanner.filter.ClassFilter;
import org.chorusbdd.chorus.pathscanner.filter.ClassFilterDecorator;
import org.chorusbdd.chorus.pathscanner.filter.HandlerAnnotationFilter;
import org.chorusbdd.chorus.util.ChorusConstants;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class PageGenerator {


    private ChorusLog log = ChorusLogFactory.getLog(PageGenerator.class);
    
    public PageGenerator() throws IOException, TemplateException {
        Configuration cfg = createFreemarkerConfiguration();


        HandlerAnnotationFilter handlerAnnotationFilter = new HandlerAnnotationFilter();
        ClassFilter filter = new ClassFilterDecorator().decorateWithPackageNameFilters(handlerAnnotationFilter, asList(ChorusConstants.BUILT_IN_PACKAGE_PREFIXES));

        Set<Class> classSet = ClasspathScanner.doScan(filter, log);
        
        for (Class handlerClass : classSet) {
            Map<String,Object> freemarkerModel = new HashMap<>();
            
            Map<String,Object> handlerProperties = new HashMap<>();
            handlerProperties.put("name", ((Handler)handlerClass.getAnnotation(Handler.class)).value());
            freemarkerModel.put("handler", handlerProperties);
            
            Map<String,Object> siteProperties = new HashMap<>();
            siteProperties.put("section", "Handlers");
            siteProperties.put("sectionIndex", 30);
            freemarkerModel.put("site", siteProperties);
            
            List<TemplateConfigProperty> handlerConfigProperties = Collections.emptyList();
            if (ConfigPropertySource.class.isAssignableFrom(handlerClass)) {
                try {
                    ConfigPropertySource c = (ConfigPropertySource)handlerClass.getDeclaredConstructor().newInstance();
                    handlerConfigProperties = c.getConfigProperties()
                            .stream()
                            .map(TemplateConfigProperty::new)
                            .sorted(TemplateConfigProperty.getComparator())
                            .collect(Collectors.toList());
                } catch (Exception e) {
                    log.error("Failed to instantiate handler class " + handlerClass, e);
                }
            }
            handlerProperties.put("properties", handlerConfigProperties);
            
            Template template = cfg.getTemplate("handlerPropertiesPageTemplate.ftl"); 
            Writer out = new OutputStreamWriter(System.out);
            template.process(freemarkerModel, out);
        }
    }


    private Function<ConfigurationProperty, HashMap<String, String>> convertForFreemarker() {
        return cp -> {
            HashMap<String, String> m = new HashMap<>();
            m.put("name", cp.getName());
            m.put("isMandatory", Boolean.toString(cp.isMandatory()));
            m.put("defaultValue", cp.getDefaultValue().map(Object::toString).orElse(""));
            m.put("description", cp.getDescription());
            return m;
        };
    }
    
    public static class TemplateConfigProperty {

        private ConfigurationProperty configurationProperty;

        public TemplateConfigProperty(ConfigurationProperty configurationProperty) {
            this.configurationProperty = configurationProperty;
        }
        
        public String getName() {
            return this.configurationProperty.getName();
        }
        
        public String getMandatory() {
            return this.configurationProperty.isMandatory() ? "yes" : "no";
        }
        
        public boolean isMandatory() {
            return this.configurationProperty.isMandatory();
        }
        
        public String getDefaultValue() {
            return this.configurationProperty.getDefaultValue().map(Object::toString).orElse("");
        }
        
        public String getDescription() {
            return this.configurationProperty.getDescription();
        }
        
        public static final Comparator<TemplateConfigProperty> getComparator() {
            return Comparator.comparing(TemplateConfigProperty::isMandatory).reversed().thenComparing(TemplateConfigProperty::getName);
        }

    }


    private static Configuration createFreemarkerConfiguration() throws IOException {
        // Create your Configuration instance, and specify if up to what FreeMarker
        // version (here 2.3.27) do you want to apply the fixes that are not 100%
        // backward-compatible. See the Configuration JavaDoc for details.
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);

        // Specify the source where the template files come from. Here I set a
        // plain directory for it, but non-file-system sources are possible too:
        cfg.setDirectoryForTemplateLoading(new File("src/main/resources/"));

        // Set the preferred charset template files are stored in. UTF-8 is
        // a good choice in most applications:
        cfg.setDefaultEncoding("UTF-8");

        // Sets how errors will appear.
        // During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        // Don't log exceptions inside FreeMarker that it will thrown at you anyway:
        cfg.setLogTemplateExceptions(false);

        // Wrap unchecked exceptions thrown during template processing into TemplateException-s.
        cfg.setWrapUncheckedExceptions(true);
        return cfg;
    }

    public static void main(String[] arga) throws IOException, TemplateException {
        new PageGenerator();
    }
}
