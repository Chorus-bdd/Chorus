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
package org.chorusbdd.chorus.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

public class LayeredArchitectureTest {

    @Test
    public void testLayers() {
        JavaClasses importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_ARCHIVES)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPath(new File("build/classes/java/main").toPath());

        ArchRule cycles = slices().matching("org.chorusbdd.chorus.(*)..").should().beFreeOfCycles();
        cycles.check(importedClasses);

        checkLayers(importedClasses,
            "handlers",
            "processes",
            "remoting",
            "handlerconfig",
            "interpreter",
            "config",
            "context",
            "output",
            "subsystem",
            "stepinvoker",
            "executionlistener"
        );
    }


    public static void checkLayers(JavaClasses javaClasses, String... orderedSubpackages) {;
        String packagePrefix = "org.chorusbdd.chorus.";

        List<String> dependentPackages = new ArrayList();
        dependentPackages.add("org.chorusbdd.chorus");
        dependentPackages.add("org.chorusbdd");

        for ( String layer : orderedSubpackages) {
            String packagePattern = packagePrefix + layer + "..";
            dependentPackages.add(packagePattern);

            ArchRule rule = classes()
                    .that().resideInAPackage(packagePattern)
                    .should().onlyBeAccessed().byAnyPackage(dependentPackages.toArray(new String[dependentPackages.size()]));

            rule.check(javaClasses);
        }
    }

}
