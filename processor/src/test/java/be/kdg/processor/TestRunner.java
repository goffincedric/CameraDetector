package be.kdg.processor;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

/**
 * @author Cédric Goffin
 * 16/10/2018 16:20
 */
public class TestRunner {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(ProcessorTestsSuite.class);

        result.getFailures().forEach(failure -> System.out.println("Failed test: " + failure.toString()));
    }
}
