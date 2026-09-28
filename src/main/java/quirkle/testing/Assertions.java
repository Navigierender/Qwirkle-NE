package quirkle.testing;

import java.util.Objects;

public final class Assertions {

    private Assertions() {}

    public static void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + " -> expected " + expected + ", got " + actual);
        }
    }

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message);
        }
    }

    public static void assertThrows(Class<? extends Throwable> expectedType, Runnable action, String message) {
        try {
            action.run();
        } catch (Throwable actual) {
            if (expectedType.isInstance(actual)) {
                return;
            }
            throw new AssertionError(message + " -> expected" + expectedType.getSimpleName() + " but got " + actual.getClass().getSimpleName(), actual);
        }
        throw new AssertionError(message + " -> no exception thrown");
    }
}
