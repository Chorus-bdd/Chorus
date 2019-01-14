package org.chorusbdd.chorus.site.markdown;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.chorusbdd.chorus.annotations.Handler;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
            
            Template template = cfg.getTemplate("handlerPropertiesPageTemplate.ftl"); 
            Writer out = new OutputStreamWriter(System.out);
            template.process(freemarkerModel, out);
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
