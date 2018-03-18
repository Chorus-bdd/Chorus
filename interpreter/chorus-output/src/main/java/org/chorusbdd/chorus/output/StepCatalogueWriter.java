package org.chorusbdd.chorus.output;

import org.chorusbdd.chorus.results.CataloguedStep;

import java.io.PrintWriter;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Collections.sort;
import static java.util.Comparator.comparing;

/**
 * Created by nickebbutt on 13/03/2018.
 * 
 * Write out a catalogue of the step implementations which were encountered
 */
class StepCatalogueWriter {

    void printStepCatalogue(Set<CataloguedStep> cataloguedSteps, Consumer<String> messageWriter) {
        
        messageWriter.accept("");
        messageWriter.accept("Step Catalogue: (All Steps By Category) --> ");
        messageWriter.accept("");

        Map<String, List<CataloguedStep>> invokersByCategory = cataloguedSteps
                .stream()
                .collect(Collectors.groupingBy(CataloguedStep::getCategory));

        SortedMap<String, List<CataloguedStep>> sortedByCategory = new TreeMap<>(invokersByCategory);

        //work out how wide to make the output
        int maxPatternWidth = sortedByCategory.values().stream().flatMap(Collection::stream).mapToInt(cs -> cs.getPattern().length()).max().orElse(0);
        int maxCategoryWidth = sortedByCategory.keySet().stream().mapToInt(String::length).max().orElse(0);
        String format = "%-" + maxCategoryWidth + "s %-" + maxPatternWidth + "s %12s %12s %18s";

        messageWriter.accept(format(format, "Category:", "Pattern:", "Invocations:", "Failures:", "Cumulative Time:"));
        sortedByCategory.values().forEach(l -> {
            List<CataloguedStep> snapshot = new LinkedList<>(l);

            sort(snapshot, comparing(CataloguedStep::getPattern));
            printCataloguedSteps(format, snapshot, messageWriter, CataloguedStep::getCumulativeTime);
        });
        messageWriter.accept("");

        printTopSteps("Step Catalogue: (Longest Running, Top 6) --> ",
                comparing(CataloguedStep::getMaxTime).reversed(), 
                s -> s.getMaxTime() > 0, 
                cataloguedSteps, 
                format, 
                messageWriter, 
                CataloguedStep::getMaxTime,
                "Max Time:"
        );

        printTopSteps("Step Catalogue: (Cumulative Time, Top 6) --> ",
                comparing(CataloguedStep::getCumulativeTime).reversed(), 
                s -> s.getCumulativeTime() > 0, 
                cataloguedSteps, 
                format, 
                messageWriter, 
                CataloguedStep::getCumulativeTime,
                "Cumulative Time:"
        );

    }

    private void printTopSteps(
            String title, 
            Comparator<CataloguedStep> comparator, 
            Predicate<CataloguedStep> stepPredicate, 
            Set<CataloguedStep> cataloguedSteps, 
            String format, 
            Consumer<String> messageWriter,
            Function<CataloguedStep, Number> extractTimeFunction,
            String timeLabel
            ) {


        List<CataloguedStep> sortedSteps = sortCataloguedSteps(cataloguedSteps, comparator, stepPredicate, 6);
        if ( sortedSteps.size() > 0) {
            messageWriter.accept("");
            messageWriter.accept(title);
            messageWriter.accept("");
            messageWriter.accept(format(format, "Category: ", "Pattern: ", "Invocations: ", "Failed: ", timeLabel));
            printCataloguedSteps(format, sortedSteps, messageWriter, extractTimeFunction);
        }
        messageWriter.accept("");
    }

    private List<CataloguedStep> sortCataloguedSteps(Set<CataloguedStep> cataloguedSteps, Comparator<CataloguedStep> comparator, Predicate<CataloguedStep> stepPredicate, int stepCount) {
        return cataloguedSteps.stream()
                .filter(stepPredicate)
                .sorted(comparator)
                .limit(stepCount)
                .collect(Collectors.toList());
    }

    private void printCataloguedSteps(String format, List<CataloguedStep> snapshot, Consumer<String> messageWriter, Function<CataloguedStep, Number> extractTimeFunction) {
        snapshot.forEach(stepInvoker -> {
            messageWriter.accept(format(format,
                    stepInvoker.getCategory(),
                    stepInvoker.getPattern(),
                    stepInvoker.getInvocationCount(),
                    stepInvoker.getFailCount(),
                    extractTimeFunction.apply(stepInvoker)));
        });
    }    
}
