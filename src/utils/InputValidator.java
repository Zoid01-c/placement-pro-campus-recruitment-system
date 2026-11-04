package utils;

import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Handles safe user input from Scanner and validates standard inputs
 * such as emails, numbers, CGPA, and telephone numbers.
 */
public class InputValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10}$");

    public static String readString(Scanner scanner, String prompt, boolean allowEmpty) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty() && !allowEmpty) {
                ConsoleUI.printError("Input cannot be empty. Please enter a valid value.");
                continue;
            }
            return input;
        }
    }

    public static String readNonEmptyString(Scanner scanner, String prompt) {
        return readString(scanner, prompt, false);
    }

    public static String readEmail(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                ConsoleUI.printError("Email cannot be empty.");
                continue;
            }
            if (!EMAIL_PATTERN.matcher(input).matches()) {
                ConsoleUI.printError("Invalid email format (e.g. user@domain.com). Try again.");
                continue;
            }
            return input.toLowerCase();
        }
    }

    public static int readInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value < min || value > max) {
                    ConsoleUI.printError(String.format("Please enter an integer between %d and %d.", min, max));
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                ConsoleUI.printError("Invalid number format. Please enter a valid integer.");
            }
        }
    }

    public static double readDouble(Scanner scanner, String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(line);
                if (value < min || value > max) {
                    ConsoleUI.printError(String.format("Please enter a number between %.2f and %.2f.", min, max));
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                ConsoleUI.printError("Invalid decimal format. Please enter a valid number (e.g. 8.5).");
            }
        }
    }

    public static String readPhone(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                ConsoleUI.printError("Phone number cannot be empty.");
                continue;
            }
            // Strip any spaces or dashes
            String clean = line.replaceAll("[\\s-]", "");
            if (!PHONE_PATTERN.matcher(clean).matches()) {
                ConsoleUI.printError("Phone number must be exactly 10 digits (e.g. 9876543210).");
                continue;
            }
            return clean;
        }
    }

    public static boolean readConfirmation(Scanner scanner, String prompt) {
        System.out.print(prompt + " (y/n): ");
        String answer = scanner.nextLine().trim().toLowerCase();
        return answer.equals("y") || answer.equals("yes");
    }

    public static void pressEnterToContinue(Scanner scanner) {
        System.out.print(ConsoleUI.dim("\n  Press Enter to continue..."));
        scanner.nextLine();
    }
}
