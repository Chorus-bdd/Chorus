package org.chorusbdd.chorus.sikulix;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;
import org.chorusbdd.chorus.stepinvoker.StepInvokerProvider;
import org.python.core.PyBaseCode;
import org.python.core.PyFunction;
import org.python.core.PyObject;
import org.python.core.PyStringMap;
import org.python.util.PythonInterpreter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ga2lakn on 28/09/2015.
 */
public class SikuliManager implements StepInvokerProvider {

    private ChorusLog log = ChorusLogFactory.getLog(this.getClass());

    private final Path sikuliRoot;

    public SikuliManager(Path sikuliRoot) {
        this.sikuliRoot = sikuliRoot;
    }

    @Override
    public List<StepInvoker> getStepInvokers() {
        PythonInterpreter interpreter = new PythonInterpreter();

        SikuliDiscoverer sikuliDiscoverer = getSikuliLocationInfo();
        populateSikuliPath(sikuliDiscoverer.getSikuliRoots(), interpreter);

        List<StepInvoker> result = new ArrayList<>();

        Set<SikuliDiscoveryInfo> sikuliDiscoveryInfos = sikuliDiscoverer.getSikuliDiscoveryInfos();
        for (SikuliDiscoveryInfo sikuliDiscoveryInfo : sikuliDiscoveryInfos) {
            String sikuliClass = sikuliDiscoveryInfo.getClassName();
            String packageId = "";
            for (String element : sikuliDiscoveryInfo.getPackageNameElements()) {
                packageId += element;
                packageId += ".";
            }

            interpreter.exec("import " + sikuliClass);

            PyStringMap dict = (PyStringMap) interpreter.eval(sikuliClass + "." + sikuliClass + ".__dict__");
            PyObject instance = interpreter.eval(sikuliClass + "." + sikuliClass + "()");

            for (Object function : dict.values()) {
                if (function instanceof PyFunction) {
                    PyFunction pyFunction = (PyFunction) function;
                    CharSequence regex = buildStepInvokerForFunction(pyFunction, sikuliDiscoveryInfo);
                    StepInvoker stepInvoker = new JythonStepInvoker(regex, pyFunction, instance, packageId + pyFunction.__name__);
                    log.info("Adding Step Invoker [" + stepInvoker + "]");
                    result.add(stepInvoker);
                }
            }
        }

        return result;
    }

    private void populateSikuliPath(Set<Path> sikuliDirectories, PythonInterpreter interpreter) {
        interpreter.exec("from sikuli.Sikuli import *");

        for (Path sikuliDirectory : sikuliDirectories) {
            String escapedSikuliPath = sikuliDirectory.toString().replace("\\", "\\\\");
            log.info("Adding Sikuli Root [" + escapedSikuliPath + "]");
            interpreter.exec("sys.path.append(\"" + escapedSikuliPath + "\")");
        }
        interpreter.exec("print sys.path");
    }

    private SikuliDiscoverer getSikuliLocationInfo() {
        try {
            SikuliDiscoverer sikuliDiscoverer = new SikuliDiscoverer(sikuliRoot);
            Files.walkFileTree(sikuliRoot, sikuliDiscoverer);
            return sikuliDiscoverer;
        }
        catch (IOException e) {
            throw new RuntimeException("Can't extract sikuli paths", e);
        }
    }

    private static Pattern NO_ARG_NO_RETURN_ACTION_PATTERN = Pattern.compile("(push|click)([\\w]+)");
    private static Pattern SET_VALUE_PATTERN = Pattern.compile("(set)([\\w]+)");
    private static Pattern NO_ARG_WITH_RETURN_ACTION_PATTERN = Pattern.compile("(get)([\\w]+)");


    /**
     * From the PyFunction function descriptor, extract the StepInvoker instance.
     * We have some pre-canned patterns for certain methods with friendly regex generated. Any that do not
     * fit into the pre-canned format will get a generic invoker pattern.
     *
     * NOTE: Package local for test only
     */
    CharSequence buildStepInvokerForFunction(PyFunction pyFunction, SikuliDiscoveryInfo sikuliDiscoveryInfo) {

        // Click / push action
        Matcher clickActionMatcher = NO_ARG_NO_RETURN_ACTION_PATTERN.matcher(pyFunction.__name__);
        if (clickActionMatcher.matches()) {
            return buildNoArgNoReturnStepInvokerRegex(clickActionMatcher, sikuliDiscoveryInfo.getPackageNameElements());
        }

        // Set value
        Matcher setValueMatcher = SET_VALUE_PATTERN.matcher(pyFunction.__name__);
        if (setValueMatcher.matches()) {
            int argcount = ((PyBaseCode)pyFunction.__code__).co_argcount;
            return buildArgBasedStepInvokerRegex(argcount, setValueMatcher, sikuliDiscoveryInfo.getPackageNameElements());
        }

        // Get value
        Matcher getValueMatcher = NO_ARG_WITH_RETURN_ACTION_PATTERN.matcher(pyFunction.__name__);
        if (getValueMatcher.matches()) {
            return buildNoArgWithReturnStepInvokerRegex(getValueMatcher, sikuliDiscoveryInfo.getPackageNameElements());
        }

        return "";
    }

    private CharSequence buildArgBasedStepInvokerRegex(int argcount, Matcher matcher, List<String> packageNameElements) {
        return new StringBuffer()
                .append("(?i)") //Case insensitive
                .append(matcher.group(1))
                .append(getCamelCaseSplits(matcher.group(2)))
                .append(" to")
                .append(getArgsRegex(argcount))
                .append(" on")
                .append(getPackageNameSplits(packageNameElements));
    }

    private CharSequence buildNoArgNoReturnStepInvokerRegex(Matcher matcher, List<String> packageNameElements) {
        return new StringBuffer()
                .append("(?i)") //Case insensitive
                .append(matcher.group(1))
                .append(getCamelCaseSplits(matcher.group(2)))
                .append(" on")
                .append(getPackageNameSplits(packageNameElements));
    }

    private CharSequence buildNoArgWithReturnStepInvokerRegex(Matcher matcher, List<String> packageNameElements) {
        return new StringBuffer()
                .append("(?i)") //Case insensitive
                .append(matcher.group(1))
                .append(" the value of")
                .append(getCamelCaseSplits(matcher.group(2)))
                .append(" on")
                .append(getPackageNameSplits(packageNameElements));
    }

    private static Pattern SPLIT_CAMEL_CASE_PATTERN = Pattern.compile("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");

    private CharSequence getCamelCaseSplits(String action) {
        StringBuffer buff = new StringBuffer();
        for (String actionSubString : SPLIT_CAMEL_CASE_PATTERN.split(action)) {
            buff.append(" ").append(actionSubString.toLowerCase());
        }
        return buff;
    }

    public static final String QUALIFIED_WORD = "([\\w\\.]+)";

    private CharSequence getArgsRegex(int argcount) {
        StringBuffer buff = new StringBuffer();
        for(int argIndex=0;argIndex<argcount-1;argIndex++) {
            buff.append((argIndex==0)?" ":",");
            buff.append(QUALIFIED_WORD);
        }
        return buff;
    }


    private CharSequence getPackageNameSplits(List<String> packageNameElements) {
        StringBuffer buff = new StringBuffer();
        for (String packageNameElement : packageNameElements) {
            buff.append(" ").append(packageNameElement);
        }
        return buff;
    }
}
