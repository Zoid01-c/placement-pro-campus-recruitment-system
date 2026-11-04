package cli;

import models.Admin;
import models.Application;
import models.Company;
import models.PlacementDrive;
import models.Student;
import services.AdminService;
import services.AuthenticationService;
import services.PlacementService;
import services.ReportService;
import services.StudentService;
import utils.ConsoleUI;
import utils.InputValidator;

import java.util.*;

/**
 * Console UI controller for the Administrator portal.
 */
public class AdminMenu {

    private final AdminService adminService;
    private final AuthenticationService authService;
    private final PlacementService placementService;
    private final StudentService studentService;
    private final ReportService reportService;
    private final Scanner scanner;

    public AdminMenu(AdminService adminService,
                     AuthenticationService authService,
                     PlacementService placementService,
                     StudentService studentService,
                     ReportService reportService,
                     Scanner scanner) {
        this.adminService = adminService;
        this.authService = authService;
        this.placementService = placementService;
        this.studentService = studentService;
        this.reportService = reportService;
        this.scanner = scanner;
    }

    public void showDashboard(Admin admin) {
        // Enforce password change prompt if on default password
        if (admin.isDefaultPassword()) {
            ConsoleUI.printWarning("SECURITY ALERT: You are using the default administrator password!");
            if (InputValidator.readConfirmation(scanner, "  Would you like to change your password now?")) {
                changePassword(admin);
            }
        }

        boolean running = true;
        while (running) {
            Map<String, Object> stats = adminService.generatePlacementStatistics();

            ConsoleUI.printHeader("ADMINISTRATIVE OVERSIGHT PORTAL");
            System.out.println("  " + ConsoleUI.bold("Administrator: " + admin.getName()));
            System.out.printf("  Department    : %s%n", admin.getDepartment());
            System.out.printf("  System Status : %s Students | %s Companies | %s Drives | %s Apps%n",
                    ConsoleUI.cyan(String.valueOf(stats.get("totalStudents"))),
                    ConsoleUI.cyan(String.valueOf(stats.get("totalCompanies"))),
                    ConsoleUI.cyan(String.valueOf(stats.get("totalDrives"))),
                    ConsoleUI.cyan(String.valueOf(stats.get("totalApplications"))));
            ConsoleUI.printDivider();

            System.out.println("  " + ConsoleUI.bold("RECORDS MANAGEMENT:"));
            System.out.println("    [1] View All Registered Students (with CGPA sorting)");
            System.out.println("    [2] View All Registered Companies");
            System.out.println("    [3] View All Placement Drives (with Package sorting)");
            System.out.println("    [4] View All Applications Across System");

            System.out.println("\n  " + ConsoleUI.bold("SEARCH & DRIVE OPERATIONS:"));
            System.out.println("    [5] Search Students (by Skill or Branch)");
            System.out.println("    [6] Activate / Deactivate Placement Drive");
            System.out.println("    [7] Remove User Account (Student or Company)");

            System.out.println("\n  " + ConsoleUI.bold("ANALYTICS & REPORTING:"));
            System.out.println("    [8] View Placement Analytics & Live Statistics");
            System.out.println("    [9] Generate & Export Placement Summary Report");
            System.out.println("   [10] Change Administrator Password");
            System.out.println("    [0] Logout to Main Menu");
            ConsoleUI.printDivider();

            int choice = InputValidator.readInt(scanner, "  Select an option: ", 0, 10);

            switch (choice) {
                case 1:
                    viewAllStudents();
                    break;
                case 2:
                    viewAllCompanies();
                    break;
                case 3:
                    viewAllDrives();
                    break;
                case 4:
                    viewAllApplications();
                    break;
                case 5:
                    searchStudents();
                    break;
                case 6:
                    toggleDrive();
                    break;
                case 7:
                    removeAccount();
                    break;
                case 8:
                    viewLiveAnalytics(stats);
                    break;
                case 9:
                    exportReport();
                    break;
                case 10:
                    changePassword(admin);
                    break;
                case 0:
                    ConsoleUI.printInfo("Logging out administrator session...");
                    running = false;
                    break;
            }
        }
    }

    private void viewAllStudents() {
        ConsoleUI.printSubHeader("ALL REGISTERED STUDENTS");
        List<Student> students = new ArrayList<>(adminService.getAllStudents());

        if (students.isEmpty()) {
            ConsoleUI.printWarning("No students registered.");
            InputValidator.pressEnterToContinue(scanner);
            return;
        }

        System.out.println("  Sort by:");
        System.out.println("    [1] Registration Order");
        System.out.println("    [2] Cumulative GPA (Highest to Lowest)");
        System.out.println("    [3] Alphabetical (Name)");

        int opt = InputValidator.readInt(scanner, "  Select order: ", 1, 3);
        if (opt == 2) {
            students.sort(new Comparator<Student>() {
                @Override
                public int compare(Student o1, Student o2) {
                    return Double.compare(o2.getCgpa(), o1.getCgpa());
                }
            });
        } else if (opt == 3) {
            students.sort(new Comparator<Student>() {
                @Override
                public int compare(Student o1, Student o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });
        }

        System.out.printf("  %-10s | %-18s | %-24s | %-6s | %-6s | %-12s%n",
                "ID", "Name", "Branch", "CGPA", "Bklog", "Contact");
        ConsoleUI.printDivider();
        for (Student s : students) {
            System.out.printf("  %-10s | %-18s | %-24s | %-6.2f | %-6d | %-12s%n",
                    s.getId(),
                    truncate(s.getName(), 18),
                    truncate(s.getBranch(), 24),
                    s.getCgpa(),
                    s.getBacklogs(),
                    s.getContactNumber());
            System.out.println(ConsoleUI.dim("    |-> Skills: " + String.join(", ", s.getSortedSkills())));
        }
        InputValidator.pressEnterToContinue(scanner);
    }

    private void viewAllCompanies() {
        ConsoleUI.printSubHeader("ALL REGISTERED COMPANIES");
        List<Company> companies = adminService.getAllCompanies();

        if (companies.isEmpty()) {
            ConsoleUI.printWarning("No companies registered.");
        } else {
            System.out.printf("  %-10s | %-22s | %-22s | %-14s | %-18s%n",
                    "Company ID", "Company Name", "Industry", "Location", "HR Contact");
            ConsoleUI.printDivider();
            for (Company c : companies) {
                System.out.printf("  %-10s | %-22s | %-22s | %-14s | %-18s%n",
                        c.getId(),
                        truncate(c.getName(), 22),
                        truncate(c.getIndustry(), 22),
                        truncate(c.getLocation(), 14),
                        truncate(c.getHrContact(), 18));
            }
        }
        InputValidator.pressEnterToContinue(scanner);
    }

    private void viewAllDrives() {
        ConsoleUI.printSubHeader("ALL CAMPUS PLACEMENT DRIVES");
        List<PlacementDrive> drives = new ArrayList<>(adminService.getAllDrives());

        if (drives.isEmpty()) {
            ConsoleUI.printWarning("No placement drives in database.");
            InputValidator.pressEnterToContinue(scanner);
            return;
        }

        System.out.println("  Sort by:");
        System.out.println("    [1] Default Order");
        System.out.println("    [2] Package / CTC (Highest to Lowest)");
        System.out.println("    [3] Minimum CGPA Cutoff");

        int opt = InputValidator.readInt(scanner, "  Select order: ", 1, 3);
        if (opt == 2) {
            drives.sort(new Comparator<PlacementDrive>() {
                @Override
                public int compare(PlacementDrive o1, PlacementDrive o2) {
                    return Double.compare(o2.getPackageLPA(), o1.getPackageLPA());
                }
            });
        } else if (opt == 3) {
            drives.sort(new Comparator<PlacementDrive>() {
                @Override
                public int compare(PlacementDrive o1, PlacementDrive o2) {
                    return Double.compare(o1.getMinCGPA(), o2.getMinCGPA());
                }
            });
        }

        System.out.printf("  %-8s | %-18s | %-24s | %-10s | %-8s | %-6s | %-8s%n",
                "Drive ID", "Company", "Job Role", "Package", "Min CGPA", "Bklogs", "Status");
        ConsoleUI.printDivider();
        for (PlacementDrive d : drives) {
            System.out.printf("  %-8s | %-18s | %-24s | %6.2f LPA | %-8.2f | %-6d | %s%n",
                    d.getDriveId(),
                    truncate(d.getCompanyName(), 18),
                    truncate(d.getJobRole(), 24),
                    d.getPackageLPA(),
                    d.getMinCGPA(),
                    d.getMaxBacklogs(),
                    ConsoleUI.getStatusBadge(d.isActive() ? "ACTIVE" : "INACTIVE"));
        }
        InputValidator.pressEnterToContinue(scanner);
    }

    private void viewAllApplications() {
        ConsoleUI.printSubHeader("ALL PLACEMENT APPLICATIONS");
        List<Application> apps = adminService.getAllApplications();

        if (apps.isEmpty()) {
            ConsoleUI.printInfo("No applications recorded yet.");
        } else {
            System.out.printf("  %-8s | %-16s | %-16s | %-22s | %-16s | %-10s%n",
                    "App ID", "Student", "Company", "Job Role", "Status", "Date");
            ConsoleUI.printDivider();
            for (Application a : apps) {
                System.out.printf("  %-8s | %-16s | %-16s | %-22s | %-25s | %-10s%n",
                        a.getApplicationId(),
                        truncate(a.getStudentName(), 16),
                        truncate(a.getCompanyName(), 16),
                        truncate(a.getJobRole(), 22),
                        ConsoleUI.getStatusBadge(a.getStatus().name()),
                        a.getAppliedDate());
            }
        }
        InputValidator.pressEnterToContinue(scanner);
    }

    private void searchStudents() {
        ConsoleUI.printSubHeader("SEARCH STUDENTS");
        System.out.println("  [1] Search by Technical Skill (e.g. Java, Python, AWS)");
        System.out.println("  [2] Search by Branch (e.g. Computer Science, Mechanical)");
        System.out.println("  [0] Back");

        int opt = InputValidator.readInt(scanner, "  Select search filter: ", 0, 2);
        List<Student> results = new ArrayList<>();

        if (opt == 1) {
            String skill = InputValidator.readNonEmptyString(scanner, "  Enter skill keyword: ");
            results = studentService.searchStudentsBySkill(skill);
        } else if (opt == 2) {
            String branch = InputValidator.readNonEmptyString(scanner, "  Enter branch keyword: ");
            results = studentService.searchStudentsByBranch(branch);
        } else {
            return;
        }

        if (results.isEmpty()) {
            ConsoleUI.printWarning("No students matched your search criteria.");
        } else {
            System.out.printf("  Found %d matching student(s):%n", results.size());
            for (Student s : results) {
                System.out.printf("   * [%s] %s | %s | CGPA: %.2f | Skills: [%s]%n",
                        s.getId(), s.getName(), s.getBranch(), s.getCgpa(), String.join(", ", s.getSortedSkills()));
            }
        }
        InputValidator.pressEnterToContinue(scanner);
    }

    private void toggleDrive() {
        ConsoleUI.printSubHeader("TOGGLE PLACEMENT DRIVE STATUS (ACTIVATE / DEACTIVATE)");
        String driveId = InputValidator.readNonEmptyString(scanner, "  Enter Drive ID: ").toUpperCase();
        PlacementDrive d = placementService.getDriveById(driveId);

        if (d == null) {
            ConsoleUI.printError("Drive ID " + driveId + " not found.");
        } else {
            boolean toggled = adminService.toggleDriveStatus(driveId);
            if (toggled) {
                ConsoleUI.printSuccess(String.format("Drive %s status changed to: %s",
                        driveId, ConsoleUI.getStatusBadge(d.isActive() ? "ACTIVE" : "INACTIVE")));
            }
        }
        InputValidator.pressEnterToContinue(scanner);
    }

    private void removeAccount() {
        ConsoleUI.printSubHeader("REMOVE USER ACCOUNT");
        String userId = InputValidator.readNonEmptyString(scanner, "  Enter User ID to remove (STU... or COM...): ").toUpperCase();

        if (InputValidator.readConfirmation(scanner, "  Are you sure you want to permanently delete account " + userId + "?")) {
            boolean ok = adminService.removeUser(userId);
            if (ok) {
                ConsoleUI.printSuccess("User account " + userId + " removed successfully.");
            } else {
                ConsoleUI.printError("User ID not found or could not be removed.");
            }
        } else {
            ConsoleUI.printInfo("Operation cancelled.");
        }
        InputValidator.pressEnterToContinue(scanner);
    }

    private void viewLiveAnalytics(Map<String, Object> stats) {
        ConsoleUI.printSubHeader("CAMPUS PLACEMENT LIVE ANALYTICS");
        System.out.printf("  Registered Students        : %s%n", stats.get("totalStudents"));
        System.out.printf("  Registered Companies       : %s%n", stats.get("totalCompanies"));
        System.out.printf("  Total Placement Drives     : %s (Active: %s)%n", stats.get("totalDrives"), stats.get("activeDrives"));
        System.out.printf("  Highest Package (CTC)      : %.2f LPA%n", stats.get("highestPackage"));
        System.out.printf("  Average Package (CTC)      : %.2f LPA%n", stats.get("averagePackage"));
        ConsoleUI.printDivider();
        System.out.printf("  Total Applications Filed   : %s%n", stats.get("totalApplications"));
        System.out.printf("  Applications Under Review  : %s%n", stats.get("appliedCount"));
        System.out.printf("  Candidates Shortlisted     : %s%n", stats.get("shortlistedCount"));
        System.out.printf("  Job Offers Extended        : %s%n", ConsoleUI.green(String.valueOf(stats.get("selectedCount"))));
        System.out.printf("  Candidates Rejected        : %s%n", stats.get("rejectedCount"));
        ConsoleUI.printDivider();
        System.out.printf("  Unique Placed Students     : %s%n", stats.get("uniquePlacedStudents"));
        System.out.printf("  Placement Conversion Rate  : %.2f%%%n", stats.get("placementRate"));
        ConsoleUI.printScoreBar((int) Math.round((Double) stats.get("placementRate")), 100);
        InputValidator.pressEnterToContinue(scanner);
    }

    private void exportReport() {
        ConsoleUI.printSubHeader("GENERATE & EXPORT PLACEMENT SUMMARY REPORT");
        String summary = adminService.generateSummaryReport();
        System.out.println(summary);

        String filename = "placement_summary_report.txt";
        if (reportService.exportReportToFile(filename, summary)) {
            ConsoleUI.printSuccess("Report exported successfully to: reports/" + filename);
        } else {
            ConsoleUI.printError("Could not save report to disk.");
        }
        InputValidator.pressEnterToContinue(scanner);
    }

    private void changePassword(Admin admin) {
        ConsoleUI.printSubHeader("CHANGE ADMINISTRATOR PASSWORD");
        String oldPass = InputValidator.readNonEmptyString(scanner, "  Enter current password: ");
        String newPass = InputValidator.readNonEmptyString(scanner, "  Enter new password (min 6 chars): ");

        try {
            authService.changePassword(admin.getId(), oldPass, newPass);
            ConsoleUI.printSuccess("Administrator password changed successfully!");
        } catch (Exception e) {
            ConsoleUI.printError(e.getMessage());
        }
        InputValidator.pressEnterToContinue(scanner);
    }

    private String truncate(String text, int max) {
        if (text == null) return "";
        if (text.length() <= max) return text;
        return text.substring(0, max - 3) + "...";
    }
}
