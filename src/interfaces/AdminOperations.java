package interfaces;

import models.Application;
import models.Company;
import models.PlacementDrive;
import models.Student;

import java.util.List;
import java.util.Map;

/**
 * Interface defining all management and oversight operations for Administrator.
 */
public interface AdminOperations {
    List<Student> getAllStudents();
    List<Company> getAllCompanies();
    List<PlacementDrive> getAllDrives();
    List<Application> getAllApplications();
    boolean removeUser(String userId);
    boolean toggleDriveStatus(String driveId);
    Map<String, Object> generatePlacementStatistics();
    String generateSummaryReport();
}
