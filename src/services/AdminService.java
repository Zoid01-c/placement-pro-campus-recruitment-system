package services;

import interfaces.AdminOperations;
import models.Application;
import models.Company;
import models.PlacementDrive;
import models.Student;

import java.util.*;

/**
 * Implements Administrator operations, system monitoring, and placement intelligence.
 */
public class AdminService implements AdminOperations {

    private final AuthenticationService authService;
    private final PlacementService placementService;
    private final ApplicationService applicationService;

    public AdminService(AuthenticationService authService,
                        PlacementService placementService,
                        ApplicationService applicationService) {
        this.authService = authService;
        this.placementService = placementService;
        this.applicationService = applicationService;
    }

    @Override
    public List<Student> getAllStudents() {
        return authService.getStudents();
    }

    @Override
    public List<Company> getAllCompanies() {
        return authService.getCompanies();
    }

    @Override
    public List<PlacementDrive> getAllDrives() {
        return placementService.getAllDrives();
    }

    @Override
    public List<Application> getAllApplications() {
        return applicationService.getAllApplications();
    }

    @Override
    public boolean removeUser(String userId) {
        return authService.removeUser(userId);
    }

    @Override
    public boolean toggleDriveStatus(String driveId) {
        return placementService.toggleDriveStatus(driveId);
    }

    @Override
    public Map<String, Object> generatePlacementStatistics() {
        Map<String, Object> stats = new HashMap<>();

        List<Student> students = authService.getStudents();
        List<Company> companies = authService.getCompanies();
        List<PlacementDrive> drives = placementService.getAllDrives();
        List<Application> apps = applicationService.getAllApplications();

        int totalStudents = students.size();
        int totalCompanies = companies.size();
        int totalDrives = drives.size();
        int activeDrives = 0;
        double maxPackage = 0.0;
        double totalPackageSum = 0.0;

        for (PlacementDrive d : drives) {
            if (d.isActive()) activeDrives++;
            if (d.getPackageLPA() > maxPackage) maxPackage = d.getPackageLPA();
            totalPackageSum += d.getPackageLPA();
        }
        double avgPackage = totalDrives > 0 ? (totalPackageSum / totalDrives) : 0.0;

        int totalApplications = apps.size();
        int appliedCount = 0;
        int shortlistedCount = 0;
        int selectedCount = 0;
        int rejectedCount = 0;

        Set<String> placedStudentIds = new HashSet<>();

        for (Application a : apps) {
            switch (a.getStatus()) {
                case APPLIED: appliedCount++; break;
                case SHORTLISTED: shortlistedCount++; break;
                case SELECTED:
                    selectedCount++;
                    placedStudentIds.add(a.getStudentId());
                    break;
                case REJECTED: rejectedCount++; break;
            }
        }

        double placementRate = totalStudents > 0 ? ((double) placedStudentIds.size() / totalStudents) * 100.0 : 0.0;

        stats.put("totalStudents", totalStudents);
        stats.put("totalCompanies", totalCompanies);
        stats.put("totalDrives", totalDrives);
        stats.put("activeDrives", activeDrives);
        stats.put("totalApplications", totalApplications);
        stats.put("appliedCount", appliedCount);
        stats.put("shortlistedCount", shortlistedCount);
        stats.put("selectedCount", selectedCount);
        stats.put("rejectedCount", rejectedCount);
        stats.put("uniquePlacedStudents", placedStudentIds.size());
        stats.put("placementRate", placementRate);
        stats.put("highestPackage", maxPackage);
        stats.put("averagePackage", avgPackage);

        return stats;
    }

    @Override
    public String generateSummaryReport() {
        Map<String, Object> stats = generatePlacementStatistics();
        StringBuilder sb = new StringBuilder();

        sb.append("==================================================================\n");
        sb.append("             PLACEMENT PRO - SYSTEM SUMMARY REPORT\n");
        sb.append("==================================================================\n\n");
        sb.append(String.format("Generated on: %s%n", new Date()));
        sb.append("------------------------------------------------------------------\n");
        sb.append("1. GENERAL METRICS\n");
        sb.append(String.format("   Total Registered Students    : %d%n", stats.get("totalStudents")));
        sb.append(String.format("   Total Registered Companies   : %d%n", stats.get("totalCompanies")));
        sb.append(String.format("   Total Placement Drives       : %d (Active: %d)%n", stats.get("totalDrives"), stats.get("activeDrives")));
        sb.append(String.format("   Highest CTC Package Offered  : %.2f LPA%n", stats.get("highestPackage")));
        sb.append(String.format("   Average CTC Package Offered  : %.2f LPA%n", stats.get("averagePackage")));
        sb.append("\n------------------------------------------------------------------\n");
        sb.append("2. APPLICATION METRICS\n");
        sb.append(String.format("   Total Applications Processed : %d%n", stats.get("totalApplications")));
        sb.append(String.format("   - In Applied Stage           : %d%n", stats.get("appliedCount")));
        sb.append(String.format("   - Shortlisted for Interview  : %d%n", stats.get("shortlistedCount")));
        sb.append(String.format("   - Successfully Selected (Offers) : %d%n", stats.get("selectedCount")));
        sb.append(String.format("   - Rejected                   : %d%n", stats.get("rejectedCount")));
        sb.append("\n------------------------------------------------------------------\n");
        sb.append("3. PLACEMENT PERFORMANCE\n");
        sb.append(String.format("   Unique Students Placed       : %d%n", stats.get("uniquePlacedStudents")));
        sb.append(String.format("   Campus Placement Rate        : %.2f%%%n", stats.get("placementRate")));
        sb.append("==================================================================\n");

        return sb.toString();
    }
}
