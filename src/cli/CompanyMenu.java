package cli;

import models.Application;
import models.Company;
import models.PlacementDrive;
import models.Student;
import services.CompanyService;
import utils.ConsoleUI;
import utils.InputValidator;

import java.util.*;

/**
 * Console UI controller for the Company module.
 */
public class CompanyMenu {

    private final CompanyService companyService;
    private final Scanner scanner;

    public CompanyMenu(CompanyService companyService, Scanner scanner) {
        this.companyService = companyService;
        this.scanner = scanner;
    }

    public void showDashboard(Company company) {
        boolean running = true;
        while (running) {
            Company current = companyService.getCompanyProfile(company.getId());
            if (current == null) {
                ConsoleUI.printError("Company account no longer exists.");
                return;
            }

            List<PlacementDrive> drives = companyService.getDrivesByCompany(current.getId());

            ConsoleUI.printHeader("COMPANY PORTAL - RECRUITER SUITE");
            System.out.println("  " + ConsoleUI.bold("Welcome, " + current.getName()));
            System.out.printf("  Company ID   : %s%n", ConsoleUI.cyan(current.getId()));
            System.out.printf("  Industry     : %s%n", current.getIndustry());
            System.out.printf("  Location     : %s%n", current.getLocation());
            System.out.printf("  Active Drives: %d Published%n", drives.size());
            ConsoleUI.printDivider();

            System.out.println("  " + ConsoleUI.bold("DRIVE MANAGEMENT:"));
            System.out.println("    [1] Create New Campus Placement Drive");
            System.out.println("    [2] View My Published Placement Drives");

            System.out.println("\n  " + ConsoleUI.bold("APPLICANT EVALUATION:"));
            System.out.println("    [3] View Applicants for a Drive");
            System.out.println("    [4] View Candidate Profile & Technical Dossier");
            System.out.println("    [5] Update Application Status (Shortlist / Select / Reject)");

            System.out.println("\n  " + ConsoleUI.bold("PROFILE:"));
            System.out.println("    [6] View Company Profile");
            System.out.println("    [7] Update Company Profile");
            System.out.println("    [0] Logout to Main Menu");
            ConsoleUI.printDivider();

            int choice = InputValidator.readInt(scanner, "  Select an option: ", 0, 7);

            switch (choice) {
                case 1:
                    createDrive(current);
                    break;
                case 2:
                    viewCompanyDrives(current);
                    break;
                case 3:
                    viewApplicantsForDrive(current);
                    break;
                case 4:
                    viewApplicantDossier();
                    break;
                case 5:
                    updateApplicantStatus();
                    break;
                case 6:
                    viewCompanyProfile(current);
                    break;
                case 7:
                    updateCompanyProfile(current);
                    break;
                case 0:
                    ConsoleUI.printInfo("Logging out recruiter session...");
                    running = false;
                    break;
            }
        }
    }

    private void createDrive(Company company) {
        ConsoleUI.printSubHeader("CREATE CAMPUS PLACEMENT DRIVE");
        String role = InputValidator.readNonEmptyString(scanner, "  Job Role / Title (e.g. SDE-1, Cloud Engineer): ");
        double pkg = InputValidator.readDouble(scanner, "  CTC Package in LPA (e.g. 12.5): ", 1.0, 150.0);
        double minCgpa = InputValidator.readDouble(scanner, "  Minimum CGPA Required (0.0 - 10.0): ", 0.0, 10.0);
        int maxBacklogs = InputValidator.readInt(scanner, "  Maximum Allowed Backlogs: ", 0, 10);
        String location = InputValidator.readNonEmptyString(scanner, "  Job Location (e.g. Bangalore / Remote): ");
        String deadline = InputValidator.readNonEmptyString(scanner, "  Application Deadline (YYYY-MM-DD): ");

        PlacementDrive drive = new PlacementDrive("", company.getId(), company.getName(),
                role, pkg, minCgpa, maxBacklogs, location, deadline);

        System.out.println("  Enter Eligible Branches (comma-separated, e.g. Computer Science, Information Technology):");
        String branchLine = scanner.nextLine().trim();
        if (!branchLine.isEmpty()) {
            for (String b : branchLine.split(",")) {
                drive.addEligibleBranch(b.trim());
            }
        }

        System.out.println("  Enter Required Technical Skills (comma-separated, e.g. Java, Spring Boot, SQL):");
        String skillLine = scanner.nextLine().trim();
        if (!skillLine.isEmpty()) {
            for (String s : skillLine.split(",")) {
                drive.addRequiredSkill(s.trim());
            }
        }

        PlacementDrive created = companyService.createDrive(drive);
        ConsoleUI.printSuccess(String.format("Placement Drive [%s] created and broadcast to all students!", created.getDriveId()));
        InputValidator.pressEnterToContinue(scanner);
    }

    private void viewCompanyDrives(Company company) {
        ConsoleUI.printSubHeader("MY PUBLISHED RECRUITMENT DRIVES");
        List<PlacementDrive> drives = companyService.getDrivesByCompany(company.getId());
        if (drives.isEmpty()) {
            ConsoleUI.printInfo("No recruitment drives posted yet.");
        } else {
            System.out.printf("  %-8s | %-24s | %-10s | %-8s | %-7s | %-12s | %-8s%n",
                    "Drive ID", "Job Role", "Package", "Min CGPA", "Backlog", "Deadline", "Status");
            ConsoleUI.printDivider();
            for (PlacementDrive d : drives) {
                System.out.printf("  %-8s | %-24s | %6.2f LPA | %-8.2f | %-7d | %-12s | %s%n",
                        d.getDriveId(),
                        d.getJobRole(),
                        d.getPackageLPA(),
                        d.getMinCGPA(),
                        d.getMaxBacklogs(),
                        d.getDeadline(),
                        ConsoleUI.getStatusBadge(d.isActive() ? "ACTIVE" : "INACTIVE"));
                System.out.println(ConsoleUI.dim("    |-> Required Skills: " + String.join(", ", d.getSortedRequiredSkills())));
            }
        }
        InputValidator.pressEnterToContinue(scanner);
    }

    private void viewApplicantsForDrive(Company company) {
        ConsoleUI.printSubHeader("APPLICANTS BY PLACEMENT DRIVE");
        List<PlacementDrive> drives = companyService.getDrivesByCompany(company.getId());
        if (drives.isEmpty()) {
            ConsoleUI.printWarning("You must post at least one drive first.");
            InputValidator.pressEnterToContinue(scanner);
            return;
        }

        System.out.println("  Select from your drives:");
        for (PlacementDrive d : drives) {
            System.out.printf("   * [%s] %s%n", d.getDriveId(), d.getJobRole());
        }

        String driveId = InputValidator.readNonEmptyString(scanner, "  Enter Drive ID: ").toUpperCase();
        List<Application> applicants = companyService.getApplicantsForDrive(driveId);

        if (applicants.isEmpty()) {
            ConsoleUI.printInfo("No candidates have applied for drive " + driveId + " yet.");
        } else {
            System.out.printf("  %-10s | %-10s | %-20s | %-16s | %-12s%n",
                    "App ID", "Student ID", "Candidate Name", "Status", "Applied On");
            ConsoleUI.printDivider();
            for (Application a : applicants) {
                System.out.printf("  %-10s | %-10s | %-20s | %-25s | %-12s%n",
                        a.getApplicationId(),
                        a.getStudentId(),
                        a.getStudentName(),
                        ConsoleUI.getStatusBadge(a.getStatus().name()),
                        a.getAppliedDate());
                if (a.getRemarks() != null && !a.getRemarks().isEmpty()) {
                    System.out.println(ConsoleUI.dim("    |-> Remarks: " + a.getRemarks()));
                }
            }
        }
        InputValidator.pressEnterToContinue(scanner);
    }

    private void viewApplicantDossier() {
        ConsoleUI.printSubHeader("CANDIDATE TECHNICAL DOSSIER");
        String stuId = InputValidator.readNonEmptyString(scanner, "  Enter Student ID (e.g. STU1001): ").toUpperCase();
        Student s = companyService.getApplicantProfile(stuId);

        if (s == null) {
            ConsoleUI.printError("Student with ID " + stuId + " not found.");
        } else {
            System.out.printf("  Student ID      : %s%n", s.getId());
            System.out.printf("  Candidate Name  : %s%n", s.getName());
            System.out.printf("  Email           : %s%n", s.getEmail());
            System.out.printf("  Contact         : %s%n", s.getContactNumber());
            System.out.printf("  Branch          : %s%n", s.getBranch());
            System.out.printf("  CGPA            : %.2f / 10.0%n", s.getCgpa());
            System.out.printf("  Active Backlogs : %d%n", s.getBacklogs());
            System.out.printf("  Graduation Year : %d%n", s.getGraduationYear());
            System.out.printf("  Technical Skills: %s%n",
                    s.getSkills().isEmpty() ? "No skills listed" : String.join(", ", s.getSortedSkills()));
        }
        InputValidator.pressEnterToContinue(scanner);
    }

    private void updateApplicantStatus() {
        ConsoleUI.printSubHeader("UPDATE CANDIDATE APPLICATION STATUS");
        String appId = InputValidator.readNonEmptyString(scanner, "  Enter Application ID (e.g. APP4001): ").toUpperCase();

        System.out.println("  Select New Status:");
        System.out.println("    [1] SHORTLISTED  (Selected for next interview round)");
        System.out.println("    [2] SELECTED     (Job offer extended)");
        System.out.println("    [3] REJECTED     (Application not moving forward)");
        System.out.println("    [0] Cancel");

        int choice = InputValidator.readInt(scanner, "  Choose status: ", 0, 3);
        if (choice == 0) return;

        Application.ApplicationStatus newStatus = null;
        if (choice == 1) newStatus = Application.ApplicationStatus.SHORTLISTED;
        else if (choice == 2) newStatus = Application.ApplicationStatus.SELECTED;
        else if (choice == 3) newStatus = Application.ApplicationStatus.REJECTED;

        String remarks = InputValidator.readString(scanner, "  Enter remarks / feedback for candidate: ", true);

        if (companyService.updateApplicationStatus(appId, newStatus, remarks)) {
            ConsoleUI.printSuccess(String.format("Application %s updated to %s. Candidate has been notified.", appId, newStatus));
        } else {
            ConsoleUI.printError("Application ID " + appId + " not found.");
        }
        InputValidator.pressEnterToContinue(scanner);
    }

    private void viewCompanyProfile(Company c) {
        ConsoleUI.printSubHeader("COMPANY PROFILE DETAILS");
        System.out.printf("  Company ID   : %s%n", c.getId());
        System.out.printf("  Name         : %s%n", c.getName());
        System.out.printf("  Email        : %s%n", c.getEmail());
        System.out.printf("  Industry     : %s%n", c.getIndustry());
        System.out.printf("  Location     : %s%n", c.getLocation());
        System.out.printf("  HR Contact   : %s%n", c.getHrContact());
        System.out.printf("  Description  : %s%n", c.getDescription());
        InputValidator.pressEnterToContinue(scanner);
    }

    private void updateCompanyProfile(Company c) {
        ConsoleUI.printSubHeader("UPDATE COMPANY PROFILE");
        System.out.println("  (Press Enter to keep current values)");

        System.out.print("  Company Name [" + c.getName() + "]: ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) c.setName(name);

        System.out.print("  Industry [" + c.getIndustry() + "]: ");
        String ind = scanner.nextLine().trim();
        if (!ind.isEmpty()) c.setIndustry(ind);

        System.out.print("  Location [" + c.getLocation() + "]: ");
        String loc = scanner.nextLine().trim();
        if (!loc.isEmpty()) c.setLocation(loc);

        System.out.print("  HR Contact [" + c.getHrContact() + "]: ");
        String hr = scanner.nextLine().trim();
        if (!hr.isEmpty()) c.setHrContact(hr);

        System.out.print("  Description [" + c.getDescription() + "]: ");
        String desc = scanner.nextLine().trim();
        if (!desc.isEmpty()) c.setDescription(desc);

        companyService.updateCompanyProfile(c);
        ConsoleUI.printSuccess("Company profile updated successfully!");
        InputValidator.pressEnterToContinue(scanner);
    }
}
