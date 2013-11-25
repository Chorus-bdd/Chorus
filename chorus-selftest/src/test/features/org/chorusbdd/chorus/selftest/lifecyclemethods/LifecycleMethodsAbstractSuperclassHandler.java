package org.chorusbdd.chorus.selftest.lifecyclemethods;

import org.chorusbdd.chorus.annotations.Destroy;
import org.chorusbdd.chorus.annotations.HandlerScope;
import org.chorusbdd.chorus.annotations.Initialize;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.util.logging.ChorusOut;

/**
 * User: nick
 * Date: 25/11/13
 * Time: 19:03
 */
public abstract class LifecycleMethodsAbstractSuperclassHandler {

    private String name;
    int initFeatureCount;
    int initScenarioCount;
    int destroyFeatureCount;
    int destroyScenarioCount;

    public LifecycleMethodsAbstractSuperclassHandler(String name) {
        this.name = name;
    }

    @Initialize(scope = HandlerScope.FEATURE)
    public void initFeature() {
        initFeatureCount++;
    }

    @Initialize(scope = HandlerScope.SCENARIO)
    public void initScenario() {
        initScenarioCount++;
    }

    @Destroy(scope = HandlerScope.FEATURE)
    public void destroyFeature() {
        destroyFeatureCount++;
        ChorusOut.out.print(toString());
    }

    @Destroy(scope = HandlerScope.SCENARIO)
    public void destroyScenario() {
        destroyScenarioCount++;
    }

    public String toString() {
        return "LifecycleMethodsFeatureScopedHandler{\n" +
                "name='" + name + '\'' + "\n" +
                ", instanceCreationCount=" + getInstanceCreationCount() + "\n" +
                ", initFeatureCount=" + initFeatureCount + "\n" +
                ", initScenarioCount=" + initScenarioCount + "\n" +
                ", destroyFeatureCount=" + destroyFeatureCount + "\n" +
                ", destroyScenarioCount=" + destroyScenarioCount + "\n" +
                '}';
    }

    abstract int getInstanceCreationCount();


    @Step("Chorus is working properly")
    public void isWorkingProperly() {

    }

    @Step("I can run a feature start scenario")
    public void runFeatureStart() {

    }

    @Step("I can run a feature with a single scenario")
    public void canRunAFeature() {

    }

    @Step("I can run a feature end scenario")
    public void canRunFeatureEnd() {

    }
}
