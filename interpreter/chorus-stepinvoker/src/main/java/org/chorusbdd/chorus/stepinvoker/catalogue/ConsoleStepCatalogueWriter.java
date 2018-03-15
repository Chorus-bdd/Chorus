package org.chorusbdd.chorus.stepinvoker.catalogue;

import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.sort;
import static java.util.Comparator.comparing;

/**
 * Created by nickebbutt on 13/03/2018.
 */
public class ConsoleStepCatalogueWriter implements StepCatalogueWriter {
    
    @Override
    public void writeCatalogue(Collection<CataloguedStepInvoker> stepInvokers, PrintWriter writer) {

        writer.printf("Catalogue of All Available Steps:%n%n");

        Map<String, java.util.List<CataloguedStepInvoker>> invokersByCategory = stepInvokers
                .stream()
                .collect(Collectors.groupingBy(CataloguedStepInvoker::getCategory));
        
        SortedMap<String, List<CataloguedStepInvoker>> sortedByCategory = new TreeMap<>(invokersByCategory);
        
       
        String format = "%-20s%-120s%-14s%-14s%-14s%n";
        writer.printf(format, "Category: ", "Pattern: ", "Invocations: ", "Failed: ", "TotalTime: ");
        
        sortedByCategory.values().forEach(l -> {
            List<CataloguedStepInvoker> snapshot = new LinkedList<>(l);

            sort(snapshot, comparing(CataloguedStepInvoker::getPattern));
            
            snapshot.forEach(stepInvoker -> {
                writer.printf(format,  
                    stepInvoker.getCategory(), 
                    stepInvoker.getPattern(), 
                    stepInvoker.getInvocationCount(), 
                    stepInvoker.getFailCount(), 
                    stepInvoker.getCumulativeTime());
            });
            writer.flush();
        });
    }
}
