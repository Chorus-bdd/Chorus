package org.chorusbdd.chorus.selftest.handlers.calculator;

import java.util.Stack;

/**
 * Created by: Steve Neal
 * Date: 12/10/11
 */
public class Calculator {

    public enum Operator {
        ADD, SUBTRACT, MULTIPLY, DIVIDE
    }

    private Stack<Double> stack = new Stack<Double>();

    private double lastResult = 0;

    public void enterNumber(Double number) {
        stack.push(number);
    }

    public void press(Operator operator) {
        double d2 = stack.pop();
        double d1 = stack.pop();

        switch (operator) {
            case ADD:
                lastResult = d1 + d2;
                break;
            case SUBTRACT:
                lastResult = d1 - d2;
                break;
            case MULTIPLY:
                lastResult = d1 * d2;
                break;
            case DIVIDE:
                lastResult = d1 / d2;
                break;
        }
    }

    public double getResult() {
        return lastResult;
    }
}