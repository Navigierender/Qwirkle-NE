package quirkle.testing;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A minimal test runner ("test engine") for this project.
 *
 * <p>It scans test classes for methods annotated with {@link Test}, calls each of them via
 * reflection, and prints a simple pass/fail report to the console. No external dependency
 * (e.g. JUnit) is needed - everything to discover, run and report tests lives in this class.
 */
public final class TestEngine {

    // utility class: only static methods, so nobody should create an instance of this
    private TestEngine() {}

    /**
     * Entry point: runs all tests of {@code ScoreCalculatorTest} and {@code PlacementValidatorTest}
     * and exits with a non-zero status code if at least one test failed.
     */
    public static void main(String[] args) {
        boolean allPassed = run(
                quirkle.game.gameplay.board.ScoreCalculatorTest.class,
                quirkle.game.gameplay.board.PlacementValidatorTest.class,
                quirkle.game.gameplay.GameTest.class);
        if (!allPassed) {
            System.exit(1);
        }
    }

    /**
     * Runs every {@code @Test}-annotated method of each given class and prints how many tests
     * passed/failed in total.
     *
     * @param testClasses the test classes to run
     * @return {@code true} if every test in every class passed, {@code false} otherwise
     */
    public static boolean run(Class<?>... testClasses) {
        int passed = 0;
        int failed = 0;

        // run the tests of every given class, one class after another
        for (Class<?> testClass : testClasses) {
            // a test class needs exactly one instance, since the test methods are not static
            Object instance = instantiate(testClass);
            ClassResult result = startTestClassMethods(testClass, instance);
            passed += result.passed();
            failed += result.failed();
        }

        System.out.println();
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        return failed == 0;
    }

    /**
     * Runs every {@code @Test}-annotated method of a single test class, printing a header first
     * so the console output makes clear which tests belong to which class.
     *
     * @param testClass the class whose {@code @Test} methods are run
     * @param instance  the instance of {@code testClass} the methods are called on
     * @return how many of {@code testClass}'s tests passed and how many failed
     */
    private static ClassResult startTestClassMethods(Class<?> testClass, Object instance) {
        // print a header before this class's tests, so it's visible in the console which
        // following [OK]/[FAIL] lines belong to which test class
        System.out.println("\n" + testClass.getSimpleName() + ":");

        // passed/failed only count this one class's tests, since they're local to this method
        // (run() adds them to its own, overall passed/failed afterward)
        int passed = 0;
        int failed = 0;
        for (Method testMethod : testMethodsOf(testClass)) {
            if (runOne(instance, testMethod)) {
                passed++;
            } else {
                failed++;
            }
        }
        return new ClassResult(passed, failed);
    }

    /** How many of one test class's tests passed/failed, returned by {@link #startTestClassMethods}. */
    private record ClassResult(int passed, int failed) {
    }

    /**
     * Runs a single test method on the given test-class instance and prints whether it passed.
     *
     * @param instance   the test-class instance the method is called on
     * @param testMethod the (no-arg) method to run
     * @return {@code true} if the method completed without throwing, {@code false} on failure
     */
    private static boolean runOne(Object instance, Method testMethod) {
        // Build display name for test (e.g. TestClass.testMethode)
        String testName = instance.getClass().getSimpleName() +  "." + testMethod.getName();

        try {
            // actually call the test method via reflection
            testMethod.invoke(instance);
            System.out.println("[OK]   " + testName);
            return true;
        } catch (InvocationTargetException e) {
            // the test method itself threw (e.g. a failed assertion) -> this is a normal test failure.
            // invoke() always wraps the real exception in an InvocationTargetException, so we need
            // getCause() to get back the exception the test method actually threw
            System.out.println("[FAIL] " + testName + " -> " + e.getCause().getMessage());
            return false;
        } catch (IllegalAccessException e) {
            // this means the engine itself is broken (e.g. missing setAccessible), not the test
            // -> fail loudly with a RuntimeException instead of reporting it as a normal [FAIL]
            throw new RuntimeException("Cannot invoke " + testName, e);
        }
    }

    /**
     * Finds all methods of a test class that are annotated with {@link Test} and makes them
     * accessible via reflection (so private test methods can be called too), sorted alphabetically
     * by name so the test output always has the same, predictable order.
     *
     * @param testClass the class to search for test methods
     * @return the {@code @Test}-annotated methods of {@code testClass}, sorted by name
     */
    private static List<Method> testMethodsOf(Class<?> testClass) {
        List<Method> methods = new ArrayList<Method>();

        // iterate through all methods of the class
        for (Method method : testClass.getDeclaredMethods()) {
            // check if methode is marked (annotated) as test (with @Test)
            if (method.isAnnotationPresent(Test.class)) {
                method.setAccessible(true);
                methods.add(method);
            }
        }
        // sort fetched methods by name
        methods.sort(Comparator.comparing(Method::getName));
        return methods;
    }

    /**
     * Creates a new instance of a test class via its no-arg constructor, even if that constructor
     * is private (test classes don't need a public constructor since only this engine calls it).
     *
     * @param testClass the test class to instantiate
     * @return a fresh instance of {@code testClass}
     * @throws RuntimeException if {@code testClass} has no no-arg constructor or can't be created
     */
    private static Object instantiate(Class<?> testClass) {
        try {
            Constructor<?> constructor = testClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Cannot instantiate " + testClass.getName(), e);
        }
    }
}
