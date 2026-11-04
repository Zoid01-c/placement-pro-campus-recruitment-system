package utils;

/**
 * Utility class for styling console output with modern typography,
 * borders, status badges, and optional ANSI color support.
 */
public class ConsoleUI {

    // ANSI Color Codes
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String DIM = "\u001B[2m";

    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    public static final String BG_BLUE = "\u001B[44m";
    public static final String BG_CYAN = "\u001B[46m";
    public static final String BG_GRAY = "\u001B[100m";

    private static boolean useColors = true;

    public static void setUseColors(boolean enable) {
        useColors = enable;
    }

    private static String colorize(String color, String text) {
        if (!useColors) return text;
        return color + text + RESET;
    }

    public static String bold(String text) { return colorize(BOLD, text); }
    public static String green(String text) { return colorize(GREEN, text); }
    public static String red(String text) { return colorize(RED, text); }
    public static String yellow(String text) { return colorize(YELLOW, text); }
    public static String cyan(String text) { return colorize(CYAN, text); }
    public static String blue(String text) { return colorize(BLUE, text); }
    public static String magenta(String text) { return colorize(MAGENTA, text); }
    public static String dim(String text) { return colorize(DIM, text); }

    public static void printHeader(String title) {
        int width = 64;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < width; i++) sb.append("=");
        String line = sb.toString();

        System.out.println();
        System.out.println(cyan("+" + line + "+"));
        int pad = (width - title.length()) / 2;
        int rightPad = width - title.length() - pad;
        StringBuilder padLeft = new StringBuilder();
        for (int i = 0; i < Math.max(0, pad); i++) padLeft.append(" ");
        StringBuilder padRight = new StringBuilder();
        for (int i = 0; i < Math.max(0, rightPad); i++) padRight.append(" ");

        System.out.println(cyan("|") + padLeft.toString() + bold(title) + padRight.toString() + cyan("|"));
        System.out.println(cyan("+" + line + "+"));
    }

    public static void printSubHeader(String subtitle) {
        int dashes = Math.max(0, 58 - subtitle.length());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dashes; i++) sb.append("-");
        System.out.println("\n" + cyan("--- ") + bold(subtitle) + cyan(" " + sb.toString()));
    }

    public static void printSuccess(String message) {
        System.out.println(green("\n  [+] SUCCESS: ") + message);
    }

    public static void printError(String message) {
        System.out.println(red("\n  [X] ERROR: ") + message);
    }

    public static void printWarning(String message) {
        System.out.println(yellow("\n  [!] WARNING: ") + message);
    }

    public static void printInfo(String message) {
        System.out.println(cyan("\n  [i] INFO: ") + message);
    }

    public static void printDivider() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 66; i++) sb.append("-");
        System.out.println(dim(sb.toString()));
    }

    public static void printThickDivider() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 66; i++) sb.append("=");
        System.out.println(cyan(sb.toString()));
    }

    public static String getStatusBadge(String status) {
        if (status == null) return "[UNKNOWN]";
        switch (status.toUpperCase()) {
            case "APPLIED":
                return cyan("[APPLIED]");
            case "SHORTLISTED":
                return yellow("[* SHORTLISTED]");
            case "SELECTED":
                return green("[+ SELECTED]");
            case "REJECTED":
                return red("[- REJECTED]");
            case "ACTIVE":
                return green("[ACTIVE]");
            case "INACTIVE":
                return dim("[INACTIVE]");
            default:
                return "[" + status + "]";
        }
    }

    public static void printScoreBar(int score, int maxScore) {
        int barLength = 25;
        int filled = (int) (((double) score / maxScore) * barLength);
        if (filled > barLength) filled = barLength;
        if (filled < 0) filled = 0;

        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < filled; i++) bar.append("#");
        for (int i = filled; i < barLength; i++) bar.append("-");

        String color = score >= 75 ? GREEN : (score >= 50 ? YELLOW : RED);
        System.out.printf("  Progress: [%s] %s%d/%d%s%n",
                colorize(color, bar.toString()), BOLD, score, maxScore, RESET);
    }

    public static void clearScreen() {
        // Safe clear or print newline spacers for cross-platform terminals
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.out.println("\n".repeat(3));
        }
    }
}
