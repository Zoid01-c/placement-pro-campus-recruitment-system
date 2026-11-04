package interfaces;

import models.PlacementDrive;
import models.Student;

/**
 * Interface for generating and exporting structured placement reports.
 */
public interface ReportGenerator {
    String generateStudentReport(Student student);
    String generateDriveReport(PlacementDrive drive);
    String generateSystemSummaryReport();
    boolean exportReportToFile(String filename, String content);
}
