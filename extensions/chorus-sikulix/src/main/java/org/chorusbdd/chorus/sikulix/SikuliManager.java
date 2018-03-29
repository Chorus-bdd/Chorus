/**
 * MIT License
 *
 * Copyright (c) 2018 Chorus BDD Organisation.
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
package org.chorusbdd.chorus.sikulix;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.sikulix.discovery.SikuliDiscovery;
import org.chorusbdd.chorus.sikulix.discovery.SikuliDiscoveryBuilder;
import org.chorusbdd.chorus.sikulix.discovery.SikuliPackage;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;
import org.chorusbdd.chorus.stepinvoker.StepInvokerProvider;
import org.python.core.PyFunction;
import org.python.core.PyObject;
import org.python.core.PyStringMap;
import org.python.util.PythonInterpreter;
import org.sikuli.script.SikulixForJython;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Enumerate the xxxx.sikuli's and provide all the StepInvokers for each method within the sikuly python class.
 *
 * @author Stephen Lake
 */
public class SikuliManager implements StepInvokerProvider {

    private ChorusLog log = ChorusLogFactory.getLog(this.getClass());

    private final Path sikuliRoot;
    private JythonStepRegexBuilder jythonStepRegexBuilder = new JythonStepRegexBuilder();

    public SikuliManager(Path sikuliRoot) {
        this.sikuliRoot = sikuliRoot;
    }

    @Override
    public List<StepInvoker> getStepInvokers() {
        SikuliDiscovery sikuliDiscovery = new SikuliDiscoveryBuilder().discover(sikuliRoot);
        PythonInterpreter interpreter = new PythonInterpreter();
        populateSikuliPathFromRootsInPythonInterpretor(sikuliDiscovery.getSikuliRoots(), interpreter);
        List<StepInvoker> result = buildStepInvokers(interpreter, sikuliDiscovery.getSikuliPackages());
        return result;
    }

    private void populateSikuliPathFromRootsInPythonInterpretor(Set<Path> sikuliDirectories, PythonInterpreter interpreter) {
        // This rather nasty construct finds the jar where the sikuli python files are embedded
        // and includes it (offsetting to the /Lib directory). It is effectively an initialisation for Sikuli.
        SikulixForJython.get();

        interpreter.exec("import sys");
        interpreter.exec("from sikuli.Sikuli import *");

        for (Path sikuliDirectory : sikuliDirectories) {
            String escapedSikuliPath = sikuliDirectory.toString().replace("\\", "\\\\");
            log.info("Adding Sikuli Root [" + escapedSikuliPath + "]");
            interpreter.exec("sys.path.append(\"" + escapedSikuliPath + "\")");
        }
        interpreter.exec("print sys.path");
    }

    private List<StepInvoker> buildStepInvokers(PythonInterpreter interpreter, Set<SikuliPackage> sikuliPackages) {
        List<StepInvoker> result = new ArrayList<>();

        for (SikuliPackage sikuliPackage : sikuliPackages) {
            String sikuliClass = sikuliPackage.getClassName();
            String packageId = "";
            for (String element : sikuliPackage.getPackageNameElements()) {
                packageId += element;
                packageId += ".";
            }

            interpreter.exec("import " + sikuliClass);

            PyStringMap dict = (PyStringMap) interpreter.eval(sikuliClass + "." + sikuliClass + ".__dict__");
            PyObject instance = interpreter.eval(sikuliClass + "." + sikuliClass + "()");

            for (Object function : dict.values()) {
                if (function instanceof PyFunction) {
                    PyFunction pyFunction = (PyFunction) function;
                    CharSequence regex = jythonStepRegexBuilder.buildStepRegexForFunction(pyFunction, sikuliPackage);
                    StepInvoker stepInvoker = new JythonStepInvoker(regex, pyFunction, instance, packageId + pyFunction.__name__);
                    log.info("Adding Step Invoker [" + stepInvoker + "]");
                    result.add(stepInvoker);
                }
            }
        }
        return result;
    }
}
