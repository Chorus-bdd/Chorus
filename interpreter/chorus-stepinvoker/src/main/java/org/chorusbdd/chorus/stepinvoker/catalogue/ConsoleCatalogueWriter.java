package org.chorusbdd.chorus.stepinvoker.catalogue;

import org.chorusbdd.chorus.stepinvoker.StepInvoker;

import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by nickebbutt on 13/03/2018.
 */
public class ConsoleCatalogueWriter implements CatalogueWriter {

    @Override
    public void writeCatalogue(Collection<StepInvoker> stepInvokers, PrintWriter writer) {
        Map<String, java.util.List<StepInvoker>> invokersByCategory = stepInvokers
                .stream()
                .collect(Collectors.groupingBy(StepInvoker::getCategory));
        
        SortedMap<String, List<StepInvoker>> sortedByCategory = new TreeMap<>(invokersByCategory);
        
        sortedByCategory.values().forEach(l -> {
            List<StepInvoker> snapshot = new LinkedList<>(l);
            
            Comparator<StepInvoker> c = Comparator.comparing(StepInvoker::getStepPattern, Comparator.comparing(Pattern::toString));
            Collections.sort(snapshot, c);
            
            snapshot.forEach(stepInvoker -> {
                writer.printf("Category: %-20s%-100s%n",  stepInvoker.getCategory(), stepInvoker.getStepPattern());
            });
            writer.flush();
            
        });
    }
}
