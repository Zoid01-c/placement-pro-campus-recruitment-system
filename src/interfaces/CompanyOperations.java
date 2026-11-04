package interfaces;

import models.Application;
import models.Company;
import models.PlacementDrive;
import models.Student;

import java.util.List;

/**
 * Interface defining all recruitment operations available to a Company.
 */
public interface CompanyOperations {
    Company getCompanyProfile(String companyId);
    boolean updateCompanyProfile(Company company);
    PlacementDrive createDrive(PlacementDrive drive);
    boolean updateDrive(PlacementDrive drive);
    List<PlacementDrive> getDrivesByCompany(String companyId);
    List<Application> getApplicantsForDrive(String driveId);
    boolean updateApplicationStatus(String applicationId, Application.ApplicationStatus status, String remarks);
    Student getApplicantProfile(String studentId);
}
