package services;

import interfaces.CompanyOperations;
import models.Application;
import models.Company;
import models.PlacementDrive;
import models.Student;
import models.User;
import utils.IDGenerator;

import java.util.List;

/**
 * Implements Company recruitment operations, drive management, and candidate evaluation.
 */
public class CompanyService implements CompanyOperations {

    private final AuthenticationService authService;
    private final PlacementService placementService;
    private final ApplicationService applicationService;
    private final NotificationService notificationService;

    public CompanyService(AuthenticationService authService,
                          PlacementService placementService,
                          ApplicationService applicationService,
                          NotificationService notificationService) {
        this.authService = authService;
        this.placementService = placementService;
        this.applicationService = applicationService;
        this.notificationService = notificationService;
    }

    @Override
    public Company getCompanyProfile(String companyId) {
        User u = authService.getUserById(companyId);
        return (u instanceof Company) ? (Company) u : null;
    }

    @Override
    public boolean updateCompanyProfile(Company company) {
        if (company == null) return false;
        authService.saveAll();
        return true;
    }

    @Override
    public PlacementDrive createDrive(PlacementDrive drive) {
        if (drive == null) return null;
        String driveId = IDGenerator.generateDriveId(placementService.getDriveCount());
        drive.setDriveId(driveId);
        placementService.addDrive(drive);

        // Broadcast notification to all students
        notificationService.sendNotification(
                "ALL",
                "New Placement Drive Announced!",
                String.format("%s is hiring for %s (Package: %.2f LPA). Apply before %s.",
                        drive.getCompanyName(), drive.getJobRole(), drive.getPackageLPA(), drive.getDeadline())
        );

        return drive;
    }

    @Override
    public boolean updateDrive(PlacementDrive drive) {
        return placementService.updateDrive(drive);
    }

    @Override
    public List<PlacementDrive> getDrivesByCompany(String companyId) {
        return placementService.getDrivesByCompany(companyId);
    }

    @Override
    public List<Application> getApplicantsForDrive(String driveId) {
        return applicationService.getApplicationsByDrive(driveId);
    }

    @Override
    public boolean updateApplicationStatus(String applicationId, Application.ApplicationStatus status, String remarks) {
        boolean ok = applicationService.updateStatus(applicationId, status, remarks);
        if (ok) {
            Application app = applicationService.getApplicationById(applicationId);
            if (app != null) {
                // Dispatch notification to candidate
                String title = "Application Update: " + status;
                String msg = String.format("Your application for %s at %s was updated to %s. Note: %s",
                        app.getJobRole(), app.getCompanyName(), status, remarks);
                notificationService.sendNotification(app.getStudentId(), title, msg);
            }
        }
        return ok;
    }

    @Override
    public Student getApplicantProfile(String studentId) {
        User u = authService.getUserById(studentId);
        return (u instanceof Student) ? (Student) u : null;
    }
}
