package services;

import interfaces.ReportGenerator;
import models.Application;
import models.PlacementDrive;
import models.Student;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Service for generating comprehensive formatted reports and exporting them to text files.
 * Implements ReportGenerator interface.
 */
public class ReportService implements ReportGenerator {

    private final AdminService adminService;
    private final ApplicationService applicationService;
    private final StudentService studentService;

    public ReportService(AdminService adminService,
                         ApplicationService applicationService,
                         StudentService studentService) {
        this.adminService = adminService;
        this.applicationService = applicationService;
        this.studentService = studentService;
    }

    @Override
    public String generateStudentReport(Student student) {
        if (student == null) return "No student record provided.";

        int score = studentService.calculateResumeReadinessScore(student);
        List<Application> apps = applicationService.getApplicationsByStudent(student.getId());

        StringBuilder sb = new StringBuilder();
        sb.append("==================================================================\n");
        sb.append("           STUDENT CAREER & PLACEMENT DOSSIER\n");
        sb.append("==================================================================\n\n");
        sb.append(String.format("Student ID         : %s%n", student.getId()));
        sb.append(String.format("Full Name          : %s%n", student.getName()));
        sb.append(String.format("Email Address      : %s%n", student.getEmail()));
        sb.append(String.format("Contact Number     : %s%n", student.getContactNumber()));
        sb.append(String.format("Academic Branch    : %s%n", student.getBranch()));
        sb.append(String.format("Cumulative GPA     : %.2f / 10.0%n", student.getCgpa()));
        sb.append(String.format("Active Backlogs    : %d%n", student.getBacklogs()));
        sb.append(String.format("Graduation Year    : %d%n", student.getGraduationYear()));
        sb.append(String.format("Resume Score       : %d / 100%n", score));
        sb.append(String.format("Technical Skills   : %s%n", String.join(", ", student.getSortedSkills())));
        sb.append("\n------------------------------------------------------------------\n");
        sb.append("APPLICATION HISTORY:\n");
        if (apps.isEmpty()) {
            sb.append("  No applications submitted yet.\n");
        } else {
            for (Application a : apps) {
                sb.append(String.format("  * [%s] %s - %s | Status: %s | Remarks: %s%n",
                        a.getApplicationId(), a.getCompanyName(), a.getJobRole(), a.getStatus(), a.getRemarks()));
            }
        }
        sb.append("==================================================================\n");
        return sb.toString();
    }

    @Override
    public String generateDriveReport(PlacementDrive drive) {
        if (drive == null) return "No drive record provided.";

        List<Application> applicants = applicationService.getApplicationsByDrive(drive.getDriveId());

        StringBuilder sb = new StringBuilder();
        sb.append("==================================================================\n");
        sb.append("           PLACEMENT DRIVE RECRUITMENT REPORT\n");
        sb.append("==================================================================\n\n");
        sb.append(String.format("Drive ID           : %s%n", drive.getDriveId()));
        sb.append(String.format("Recruiting Company : %s%n", drive.getCompanyName()));
        sb.append(String.format("Job Designation    : %s%n", drive.getJobRole()));
        sb.append(String.format("Compensation (CTC) : %.2f LPA%n", drive.getPackageLPA()));
        sb.append(String.format("Min CGPA Cutoff    : %.2f%n", drive.getMinCGPA()));
        sb.append(String.format("Max Backlogs Allowed: %d%n", drive.getMaxBacklogs()));
        sb.append(String.format("Location           : %s%n", drive.getLocation()));
        sb.append(String.format("Application Cutoff : %s%n", drive.getDeadline()));
        sb.append(String.format("Eligible Branches  : %s%n", String.join(", ", drive.getSortedEligibleBranches())));
        sb.append(String.format("Required Skills    : %s%n", String.join(", ", drive.getSortedRequiredSkills())));
        sb.append(String.format("Total Applicants   : %d%n", applicants.size()));
        sb.append("\n------------------------------------------------------------------\n");
        sb.append("APPLICANT ROSTER:\n");
        if (applicants.isEmpty()) {
            sb.append("  No applications received for this drive yet.\n");
        } else {
            for (Application a : applicants) {
                sb.append(String.format("  * [%s] Candidate: %s (%s) | Status: %s%n",
                        a.getApplicationId(), a.getStudentName(), a.getStudentId(), a.getStatus()));
            }
        }
        sb.append("==================================================================\n");
        return sb.toString();
    }

    @Override
    public String generateSystemSummaryReport() {
        return adminService.generateSummaryReport();
    }

    @Override
    public boolean exportReportToFile(String filename, String content) {
        File reportsDir = new File("reports");
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }
        File target = new File(reportsDir, filename);
        try (FileWriter fw = new FileWriter(target)) {
            fw.write(content);
            return true;
        } catch (IOException e) {
            System.err.println("Failed to write report to file: " + e.getMessage());
            return false;
        }
    }
}
