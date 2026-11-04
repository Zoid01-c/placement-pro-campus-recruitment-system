package utils;

import java.util.Collection;

/**
 * Utility to generate formatted sequential IDs for entities.
 */
public class IDGenerator {

    public static String generateStudentId(int count) {
        return String.format("STU%04d", 1000 + count + 1);
    }

    public static String generateCompanyId(int count) {
        return String.format("COM%04d", 2000 + count + 1);
    }

    public static String generateDriveId(int count) {
        return String.format("DRV%04d", 3000 + count + 1);
    }

    public static String generateApplicationId(int count) {
        return String.format("APP%04d", 4000 + count + 1);
    }

    public static String generateNotificationId(int count) {
        return String.format("NOT%04d", 5000 + count + 1);
    }
}
