package services;

import exceptions.AlreadyAppliedException;
import models.Application;
import models.PlacementDrive;
import models.Student;
import utils.FileManager;
import utils.IDGenerator;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Service managing job applications, application statuses, and duplicate checks.
 * Uses HashSet for O(1) duplicate application prevention.
 */
public class ApplicationService {

    private List<Application> applications;
    private Map<String, Application> applicationMap; // Lookup by applicationId
    private Set<String> appliedKeys; // Form: "studentId#driveId" to prevent duplicate applications

    public ApplicationService() {
        this.applications = new ArrayList<>();
        this.applicationMap = new HashMap<>();
        this.appliedKeys = new HashSet<>();
        reload();
    }

    public void reload() {
        this.applications = FileManager.loadApplications();
        this.applicationMap.clear();
        this.appliedKeys.clear();
        for (Application a : this.applications) {
            this.applicationMap.put(a.getApplicationId(), a);
            this.appliedKeys.add(createKey(a.getStudentId(), a.getDriveId()));
        }
    }

    public void saveAll() {
        FileManager.saveApplications(this.applications);
    }

    private String createKey(String studentId, String driveId) {
        return studentId.trim().toUpperCase() + "#" + driveId.trim().toUpperCase();
    }

    public boolean hasAlreadyApplied(String studentId, String driveId) {
        return appliedKeys.contains(createKey(studentId, driveId));
    }

    public Application apply(Student student, PlacementDrive drive) throws AlreadyAppliedException {
        if (hasAlreadyApplied(student.getId(), drive.getDriveId())) {
            throw new AlreadyAppliedException(String.format(
                    "Student %s has already submitted an application for %s (%s).",
                    student.getName(), drive.getCompanyName(), drive.getJobRole()
            ));
        }

        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String appId = IDGenerator.generateApplicationId(this.applications.size());

        Application app = new Application(
                appId, student.getId(), student.getName(),
                drive.getDriveId(), drive.getCompanyName(), drive.getJobRole(),
                today
        );

        this.applications.add(app);
        this.applicationMap.put(app.getApplicationId(), app);
        this.appliedKeys.add(createKey(student.getId(), drive.getDriveId()));
        saveAll();

        return app;
    }

    public boolean updateStatus(String applicationId, Application.ApplicationStatus newStatus, String remarks) {
        Application app = applicationMap.get(applicationId.trim().toUpperCase());
        if (app != null) {
            app.setStatus(newStatus);
            String today = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
            app.setLastUpdated(today);
            if (remarks != null && !remarks.trim().isEmpty()) {
                app.setRemarks(remarks.trim());
            }
            saveAll();
            return true;
        }
        return false;
    }

    public Application getApplicationById(String appId) {
        return applicationMap.get(appId.trim().toUpperCase());
    }

    public List<Application> getAllApplications() {
        return Collections.unmodifiableList(this.applications);
    }

    public List<Application> getApplicationsByStudent(String studentId) {
        List<Application> list = new ArrayList<>();
        for (Application a : this.applications) {
            if (a.getStudentId().equalsIgnoreCase(studentId)) {
                list.add(a);
            }
        }
        return list;
    }

    public List<Application> getApplicationsByDrive(String driveId) {
        List<Application> list = new ArrayList<>();
        for (Application a : this.applications) {
            if (a.getDriveId().equalsIgnoreCase(driveId)) {
                list.add(a);
            }
        }
        return list;
    }

    public int getApplicationCount() {
        return this.applications.size();
    }
}
