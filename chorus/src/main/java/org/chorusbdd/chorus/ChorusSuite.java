package org.chorusbdd.chorus;

import org.chorusbdd.chorus.results.EndState;
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.util.config.InterpreterPropertyException;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: nick
 * Date: 27/08/13
 * Time: 08:36
 */
public class ChorusSuite extends ParentRunner<ChorusSuite.ChorusTest> {

    private Class clazz;
    private List<FeatureToken> features;
    private Chorus chorus;
    private ExecutionToken executionToken;

    public ChorusSuite(Class clazz) throws InitializationError {
        super(clazz);
        this.clazz = clazz;
    }

    protected Statement classBlock(final RunNotifier notifier) {
        Statement statement= super.classBlock(notifier);
        statement = new EndChorusSuite(statement);
        return statement;
    }

    @Override
    protected List<ChorusTest> getChildren() {
        Method method;
        try {
            method = clazz.getMethod("getChorusArgs");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to find getChorusArgs method");
        }
        
        if (! Modifier.isStatic(method.getModifiers())) {
            throw new RuntimeException("The getChorusArgs method is not static");    
        }
        
        if ( ! String.class.equals(method.getReturnType())) {
            throw new RuntimeException("The getChorusArgs method does not return a String");
        }

        String args = "";
        try {
            args = (String)method.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed during call to getChorusArgs", e);
        }

        try {
             chorus = new Chorus(args.split(" "));
        } catch (InterpreterPropertyException e) {
            throw new RuntimeException("Unsupported property", e);
        }

        executionToken = chorus.startTests();
        try {
             features = chorus.getFeatureList(executionToken);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get feature list", e);
        }
        
        List<ChorusTest> tests = new ArrayList<ChorusTest>();
        for (FeatureToken f  : features) {
            tests.add(new ChorusTest(chorus, executionToken, f));        
        }
        return tests;
    }
    
    protected Description describeChild(ChorusTest child) {
        return child.getDescription();
    }

    protected void runChild(ChorusTest child, RunNotifier notifier) {
        notifier.fireTestStarted(child.getDescription());
        try {
            child.run();
            if ( ! child.isSuccess()) {
                notifier.fireTestFailure(new FailureWithNoException(child.getDescription()));
            }
        } catch (Exception e) {
            notifier.fireTestFailure(new Failure(child.getDescription(), e));    
        }
        notifier.fireTestFinished(child.getDescription());
    }
    
    public class ChorusTest {

        private Chorus chorus;
        private ExecutionToken t;
        private FeatureToken featureToken;

        public ChorusTest(Chorus chorus, ExecutionToken t, FeatureToken featureToken) {
            this.chorus = chorus;
            this.t = t;
            //To change body of created methods use File | Settings | File Templates.
            this.featureToken = featureToken;          
        }

        public Description getDescription() {
            return Description.createTestDescription(clazz, featureToken.getNameWithConfiguration());
        }

        public void run() throws Exception {
            chorus.processFeatures(t, Collections.singletonList(featureToken));
        }
        
        public boolean isSuccess() {
            return isJUnitPass(featureToken);
        }
    }

    private class FailureWithNoException extends Failure {
        public FailureWithNoException(Description description) {
            super(description, null);
        }
        
        public String toString() {
            return getTestHeader();    
        }
    }

    public static boolean isJUnitPass(FeatureToken featureToken) {
        return featureToken.getEndState() == EndState.PASSED || featureToken.getEndState() == EndState.PENDING;
    }

    private class EndChorusSuite extends Statement {
        private Statement statement;

        public EndChorusSuite(Statement statement) {
            this.statement = statement;
        }

        public void evaluate() throws Throwable {
            statement.evaluate();
            chorus.endTests(executionToken, features);
        }
    }
}
