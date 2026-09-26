package quirkle.game.debug;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Simple console input and output for the debug mode.
 * @note the read methods ask again until the input is valid
 */
public final class DebugConsole {

    private static final int SEPARATOR_WIDTH = 45;

    private static final Scanner SCANNER = new Scanner(System.in);

    private DebugConsole() {}

    /** Prints one line. */
    public static void println(String line) {
        System.out.println(line);
    }

    /** Prints an empty line. */
    public static void printBlank() {
        System.out.println();
    }

    /** prints a title between two separator lines */
    public static void printHeading(String title) {
        System.out.println();
        System.out.println("-".repeat(SEPARATOR_WIDTH));
        System.out.println("  " + title);
        System.out.println("-".repeat(SEPARATOR_WIDTH));
    }

    /** Prints a separator line. */
    public static void printSeparator() {
        System.out.println("-".repeat(SEPARATOR_WIDTH));
    }

    /**
     * @return the next input line, trimmed
     * @throws IllegalStateException if there is no console (game not started from a terminal)
     */
    public static String readLine(String prompt) {
        System.out.print(prompt + " > ");
        try {
            return SCANNER.nextLine().trim();
        } catch (NoSuchElementException e) {
            throw new IllegalStateException("no console input available; start the game from a terminal");
        }
    }

    /** asks until a whole number is entered */
    public static int readInt(String prompt) {
        while (true) {
            String input = readLine(prompt);
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                println("  '" + input + "' is not a whole number.");
            }
        }
    }

    /** asks until a number between min and max (inclusive) is entered */
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt + " (" + min + ".." + max + ")");
            if (value >= min && value <= max) return value;
            println("  " + value + " is outside " + min + ".." + max + ".");
        }
    }

    /** asks a yes/no question until y or n is entered ("j" works too) */
    public static boolean readYesNo(String prompt) {
        while (true) {
            String input = readLine(prompt + " (y/n)").toLowerCase();
            if (input.equals("y") || input.equals("j")) return true;
            if (input.equals("n")) return false;
            println("  please answer y or n.");
        }
    }
}