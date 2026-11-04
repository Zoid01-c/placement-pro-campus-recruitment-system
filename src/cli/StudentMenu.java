package cli;

import exceptions.AlreadyAppliedException;
import exceptions.DriveNotFoundException;
import exceptions.InvalidInputException;
import models.Application;
import models.Notification;
import models.PlacementDrive;
import models.Student;
import services.NotificationService;
import services.StudentService;
import utils.ConsoleUI;
import utils.InputValidator;

import java.util.*;

/**
 * Console UI controller for the Student module.
 */
public class StudentMenu {

    private final StudentService studentService;
    private final NotificationService notificationService;
    private final Scanner scanner;

    public StudentMenu(StudentService studentService,
                       NotificationService notificationService,
                       Scanner scanner) {
        this.studentService = studentService;
        this.notificationService = notificationService;
        this.scanner = scanner;
    }

    public void showDashboard(Student student) {
        boolean running = true;
        while (running) {
            // Fetch fresh state
            Student currentStudent = studentService.getProfile(student.getId());
            if (currentStudent == null) {
                ConsoleUI.printError("Student account no longer exists.");
                return;
            }

            int unreadNotifs = notificationService.getUnreadCount(currentStudent.getId());
            int readinessScore = studentService.calculateResumeReadinessScore(currentStudent);

            ConsoleUI.printHeader("STUDENT DASHBOARD - PLACEMENT PRO");
            System.out.println("  " + ConsoleUI.bold("Welcome, " + currentStudent.getName()));
            System.out.printf("  Student ID      : %s%n", ConsoleUI.cyan(currentStudent.getId()));
            System.out.printf("  Branch          : %s%n", currentStudent.getBranch());
            System.out.printf("  CGPA            : %.2f / 10.0%n", currentStudent.getCgpa());
            System.out.printf("  Active Backlogs : %d%n", currentStudent.getBacklogs());
            System.out.printf("  Notifications   : %s%n",
                    unreadNotifs > 0 ? ConsoleUI.yellow(unreadNotifs + " Unread") : ConsoleUI.dim("0 Unread"));
            ConsoleUI.printScoreBar(readinessScore, 100);
            ConsoleUI.printDivider();

            System.out.println("  " + ConsoleUI.bold("RECRUITMENT & DRIVES:"));
            System.out.println("    [1] View All Available Placement Drives");
            System.out.println("    [2] View Eligible Drives Only");
            System.out.println("    [3] Search Placement Drives (Company / Role / Location)");
            System.out.println("    [4] Apply for a Placement Drive");
            System.out.println("    [5] My Applications & Status Tracking");

            System.out.println("\n  " + ConsoleUI.bold("CAREER READINESS & ANALYTICS:"));
            System.out.println("    [6] Skill-Gap Analysis for a Drive");
            System.out.println("    [7] Resume Readiness Breakdown & Formula");

            System.out.println("\n  " + ConsoleUI.bold("PROFILE & NOTIFICATIONS:"));
            System.out.println("    [8] View Complete Profile");
            System.out.println("    [9] Manage Technical Skills (Add / Remove)");
            System.out.println("   [10] Update Academic & Contact Details");
            System.out.println("   [11] Notifications Inbox (" + unreadNotifs + " unread)");
            System.out.println("    [0] Logout to Main Menu");
            ConsoleUI.printDivider();

            int choice = InputValidator.readInt(scanner, "  Select an option: ", 0, 11);

            switch (choice) {
                case 1:
                    viewAllDrives();
                    break;
                case 2:
                    viewEligibleDrives(currentStudent);
                    break;
                case 3:
                    searchDrives();
                    break;
                case 4:
                    applyForDrive(currentStudent);
                    break;
                case 5:
                    viewMyApplications(currentStudent);
                    break;
                case 6:
                    performSkillGapAnalysis(currentStudent);
                    break;
                case 7:
                    showResumeScoreBreakdown(currentStudent, readinessScore);
                    break;
                case 8:
                    viewProfile(currentStudent);
                    break;
                case 9:
                    manageSkills(currentStudent);
                    break;
                case 10:
                    updateProfile(currentStudent);
                    break;
                case 11:
                    viewNotifications(currentStudent);
                    break;
                case 0:
                    ConsoleUI.printInfo("Logging out from student portal...");
                    running = false;
                    break;
            }
        }
    }

    private void viewAllDrives() {
        ConsoleUI.printSubHeader("ALL AVAILABLE RECRUITMENT DRIVES");
        List<PlacementDrive> drives = studentService.getAllActiveDrives();
        if (drives.isEmpty()) {
            ConsoleUI.printWarning("No active placement drives found at this time.");
        } else {
            printDrivesTable(drives);
        }
        InputValidator.pressEnterToContinue(scanner);
    }

    private void viewEligibleDrives(Student student) {
        ConsoleUI.printSubHeader("DRIVES MATCHING YOUR ELIGIBILITY CRITERIA");
        System.out.printf("  (Filtered for CGPA >= Drive Cutoff, Backlogs <= Drive Limit, and Branch: %s)%n%n",
                student.getBranch());
        List<PlacementDrive> eligible = studentService.getEligibleDrives(student.getId());
        if (eligible.isEmpty()) {
            ConsoleUI.printWarning("No placement drives currently match your CGPA / Branch / Backlogs criteria.");
        } else {
            printDrivesTable(eligible);
        }
        InputValidator.pressEnterToContinue(scanner);
    }

    private void searchDrives() {
        ConsoleUI.printSubHeader("SEARCH RECRUITMENT DRIVES");
        System.out.println("  [1] Search by Company Name");
        System.out.println("  [2] Search by Job Role");
        System.out.println("  [3] Search by Location");
        System.out.println("  [0] Back");

        int opt = InputValidator.readInt(scanner, "  Choose filter: ", 0, 3);
        List<PlacementDrive> results = new ArrayList<>();

        if (opt == 1) {
            String kw = InputValidator.readNonEmptyString(scanner, "  Enter company keyword: ");
            results = studentService.searchDrivesByCompany(kw);
        } else if (opt == 2) {
            String kw = InputValidator.readNonEmptyString(scanner, "  Enter job role keyword: ");
            results = studentService.searchDrivesByRole(kw);
        } else if (opt == 3) {
            String kw = InputValidator.readNonEmptyString(scanner, "  Enter location keyword: ");
            results = studentService.searchDrivesByLocation(kw);
        } else {
            return;
        }

        if (results.isEmpty()) {
            ConsoleUI.printWarning("No drives matched your search criteria.");
        } else {
            printDrivesTable(results);
        }
        InputValidator.pressEnterToContinue(scanner);
    }

    private void applyForDrive(Student student) {
        ConsoleUI.printSubHeader("APPLY FOR PLACEMENT DRIVE");
        String driveId = InputValidator.readNonEmptyString(scanner, "  Enter Drive ID to apply (e.g. DRV3001): ").toUpperCase();

        try {
            Application app = studentService.applyForDrive(student.getId(), driveId);
            ConsoleUI.printSuccess(String.format("Application submitted successfully! Application ID: %s", app.getApplicationId()));
            System.out.printf("  Company  : %s%n", app.getCompanyName());
            System.out.printf("  Job Role : %s%n", app.getJobRole());
            System.out.printf("  Status   : %s%n", ConsoleUI.getStatusBadge(app.getStatus().name()));
        } catch (AlreadyAppliedException e) {
            ConsoleUI.printWarning(e.getMessage());
        } catch (DriveNotFoundException | InvalidInputException e) {
            ConsoleUI.printError(e.getMessage());
        }

        InputValidator.pressEnterToContinue(scanner);
    }

    private void viewMyApplications(Student student) {
        ConsoleUI.printSubHeader("MY PLACEMENT APPLICATIONS HISTORY");
        List<Application> apps = studentService.getApplicationsByStudent(student.getId());

        if (apps.isEmpty()) {
            ConsoleUI.printInfo("You have not applied to any placement drives yet.");
        } else {
            System.out.printf("  %-10s | %-20s | %-25s | %-16s | %-12s%n",
                    "App ID", "Company", "Job Role", "Status", "Applied On");
            ConsoleUI.printDivider();
            for (Application a : apps) {
                System.out.printf("  %-10s | %-20s | %-25s | %-25s | %-12s%n",
                        a.getApplicationId(),
                        truncate(a.getCompanyName(), 20),
                        truncate(a.getJobRole(), 25),
                        ConsoleUI.getStatusBadge(a.getStatus().name()),
                        a.getAppliedDate());
                if (a.getRemarks() != null && !a.getRemarks().isEmpty()) {
                    System.out.println(ConsoleUI.dim("    |-> Remarks: " + a.getRemarks()));
                }
            }
        }
        InputValidator.pressEnterToContinue(scanner);
    }

    private void performSkillGapAnalysis(Student student) {
        ConsoleUI.printSubHeader("SKILL-GAP ANALYSIS");
        String driveId = InputValidator.readNonEmptyString(scanner, "  Enter Target Drive ID (e.g. DRV3001): ").toUpperCase();

        List<PlacementDrive> drives = studentService.getAllActiveDrives();
        PlacementDrive target = null;
        for (PlacementDrive d : drives) {
            if (d.getDriveId().equalsIgnoreCase(driveId)) {
                target = d;
                break;
            }
        }

        if (target == null) {
            ConsoleUI.printError("Drive with ID " + driveId + " not found or inactive.");
            InputValidator.pressEnterToContinue(scanner);
            return;
        }

        Map<String, Object> gap = studentService.performSkillGapAnalysis(student, target);

        @SuppressWarnings("unchecked")
        Set<String> matched = (Set<String>) gap.get("matchedSkills");
        @SuppressWarnings("unchecked")
        Set<String> missing = (Set<String>) gap.get("missingSkills");
        int pct = (int) gap.get("matchPercentage");
        @SuppressWarnings("unchecked")
        List<String> recommendations = (List<String>) gap.get("recommendations");

        System.out.println("\n  " + ConsoleUI.bold("ANALYSIS FOR: " + target.getCompanyName() + " (" + target.getJobRole() + ")"));
        System.out.printf("  Required Skills : %s%n", String.join(", ", target.getSortedRequiredSkills()));
        System.out.printf("  Your Skills     : %s%n", String.join(", ", student.getSortedSkills()));
        ConsoleUI.printDivider();

        System.out.println("  " + ConsoleUI.green("Matching Skills (" + matched.size() + "): ") + (matched.isEmpty() ? "None" : String.join(", ", matched)));
        System.out.println("  " + ConsoleUI.red("Missing Skills  (" + missing.size() + "): ") + (missing.isEmpty() ? "None! Complete match!" : String.join(", ", missing)));

        System.out.println();
        ConsoleUI.printScoreBar(pct, 100);

        System.out.println("\n  " + ConsoleUI.bold("Personalized Improvement Recommendations:"));
        for (String rec : recommendations) {
            System.out.println("   * " + ConsoleUI.cyan(rec));
        }

        InputValidator.pressEnterToContinue(scanner);
    }

    private void showResumeScoreBreakdown(Student student, int totalScore) {
        ConsoleUI.printSubHeader("RESUME READINESS SCORING ENGINE");
        System.out.println("  Transparent scoring formula developed for campus recruitment evaluation:\n");

        double cgpaPart = (Math.min(10.0, Math.max(0.0, student.getCgpa())) / 10.0) * 50.0;
        int skillCount = student.getSkills() != null ? student.getSkills().size() : 0;
        double skillPart = Math.min(30.0, skillCount * 6.0);
        double backlogPart = student.getBacklogs() == 0 ? 20.0 : (student.getBacklogs() == 1 ? 10.0 : 0.0);

        System.out.printf("  1. Academic CGPA Weightage (Max 50 pts) : %.1f pts (CGPA: %.2f)%n", cgpaPart, student.getCgpa());
        System.out.printf("  2. Technical Skills Depth (Max 30 pts) : %.1f pts (%d skills recorded)%n", skillPart, skillCount);
        System.out.printf("  3. Backlog Freedom Score  (Max 20 pts) : %.1f pts (%d active backlogs)%n", backlogPart, student.getBacklogs());
        ConsoleUI.printDivider();
        System.out.printf("  Total Resume Readiness Score            : %s / 100%n%n", ConsoleUI.bold(String.valueOf(totalScore)));

        ConsoleUI.printScoreBar(totalScore, 100);
        System.out.println("\n  " + ConsoleUI.dim("Note: This score is calculated dynamically using Core Java logic"));
        System.out.println("  " + ConsoleUI.dim("to provide structured benchmarking for mock drives and appraisals."));

        InputValidator.pressEnterToContinue(scanner);
    }

    private void viewProfile(Student student) {
        ConsoleUI.printSubHeader("STUDENT ACADEMIC & PLACEMENT DOSSIER");
        System.out.printf("  Student ID      : %s%n", student.getId());
        System.out.printf("  Full Name       : %s%n", student.getName());
        System.out.printf("  Email           : %s%n", student.getEmail());
        System.out.printf("  Contact Number  : %s%n", student.getContactNumber());
        System.out.printf("  Branch / Dept   : %s%n", student.getBranch());
        System.out.printf("  Cumulative GPA  : %.2f%n", student.getCgpa());
        System.out.printf("  Active Backlogs : %d%n", student.getBacklogs());
        System.out.printf("  Graduation Year : %d%n", student.getGraduationYear());
        System.out.printf("  Technical Skills: %s%n",
                student.getSkills().isEmpty() ? "No skills added yet" : String.join(", ", student.getSortedSkills()));
        InputValidator.pressEnterToContinue(scanner);
    }

    private void manageSkills(Student student) {
        boolean managing = true;
        while (managing) {
            ConsoleUI.printSubHeader("MANAGE TECHNICAL SKILLS");
            System.out.printf("  Current Skills (%d): %s%n",
                    student.getSkills().size(),
                    student.getSkills().isEmpty() ? "[None]" : String.join(", ", student.getSortedSkills()));
            ConsoleUI.printDivider();
            System.out.println("  [1] Add New Technical Skill");
            System.out.println("  [2] Remove Existing Skill");
            System.out.println("  [0] Back to Dashboard");

            int opt = InputValidator.readInt(scanner, "  Select: ", 0, 2);
            if (opt == 1) {
                String skill = InputValidator.readNonEmptyString(scanner, "  Enter skill name (e.g. Docker, Python, Spring Boot): ");
                if (studentService.addSkill(student.getId(), skill)) {
                    ConsoleUI.printSuccess("Skill '" + skill + "' added successfully!");
                } else {
                    ConsoleUI.printWarning("Skill already present or invalid.");
                }
            } else if (opt == 2) {
                if (student.getSkills().isEmpty()) {
                    ConsoleUI.printWarning("No skills available to remove.");
                } else {
                    String skill = InputValidator.readNonEmptyString(scanner, "  Enter skill name to remove: ");
                    if (studentService.removeSkill(student.getId(), skill)) {
                        ConsoleUI.printSuccess("Skill '" + skill + "' removed.");
                    } else {
                        ConsoleUI.printWarning("Skill not found in your profile.");
                    }
                }
            } else {
                managing = false;
            }
        }
    }

    private void updateProfile(Student student) {
        ConsoleUI.printSubHeader("UPDATE PROFILE");
        System.out.println("  (Press Enter to keep current values)");

        System.out.print("  Full Name [" + student.getName() + "]: ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) student.setName(name);

        System.out.print("  Branch [" + student.getBranch() + "]: ");
        String branch = scanner.nextLine().trim();
        if (!branch.isEmpty()) student.setBranch(branch);

        System.out.print("  CGPA [" + student.getCgpa() + "]: ");
        String cgpaStr = scanner.nextLine().trim();
        if (!cgpaStr.isEmpty()) {
            try {
                double cgpa = Double.parseDouble(cgpaStr);
                if (cgpa >= 0.0 && cgpa <= 10.0) student.setCgpa(cgpa);
            } catch (Exception ignored) {}
        }

        System.out.print("  Active Backlogs [" + student.getBacklogs() + "]: ");
        String backlogsStr = scanner.nextLine().trim();
        if (!backlogsStr.isEmpty()) {
            try {
                int backlogs = Integer.parseInt(backlogsStr);
                if (backlogs >= 0) student.setBacklogs(backlogs);
            } catch (Exception ignored) {}
        }

        System.out.print("  Contact Number [" + student.getContactNumber() + "]: ");
        String phone = scanner.nextLine().trim();
        if (!phone.isEmpty()) student.setContactNumber(phone);

        studentService.updateProfile(student);
        ConsoleUI.printSuccess("Profile updated successfully!");
        InputValidator.pressEnterToContinue(scanner);
    }

    private void viewNotifications(Student student) {
        ConsoleUI.printSubHeader("NOTIFICATIONS INBOX");
        List<Notification> notifs = notificationService.getNotificationsForUser(student.getId());

        if (notifs.isEmpty()) {
            ConsoleUI.printInfo("No notifications in your inbox.");
        } else {
            for (Notification n : notifs) {
                String status = n.isRead() ? ConsoleUI.dim("[READ]") : ConsoleUI.yellow("[NEW]");
                System.out.printf("  %s %s - %s%n", status, ConsoleUI.bold(n.getTitle()), ConsoleUI.dim(n.getTimestamp()));
                System.out.println("     " + n.getMessage());
                ConsoleUI.printDivider();
            }
            notificationService.markAllAsRead(student.getId());
            ConsoleUI.printInfo("All notifications marked as read.");
        }
        InputValidator.pressEnterToContinue(scanner);
    }

    private void printDrivesTable(List<PlacementDrive> drives) {
        System.out.printf("  %-8s | %-18s | %-24s | %-9s | %-8s | %-6s | %-10s%n",
                "Drive ID", "Company", "Job Role", "Package", "Min CGPA", "Bklogs", "Deadline");
        ConsoleUI.printDivider();
        for (PlacementDrive d : drives) {
            System.out.printf("  %-8s | %-18s | %-24s | %6.2f LPA | %-8.2f | %-6d | %-10s%n",
                    d.getDriveId(),
                    truncate(d.getCompanyName(), 18),
                    truncate(d.getJobRole(), 24),
                    d.getPackageLPA(),
                    d.getMinCGPA(),
                    d.getMaxBacklogs(),
                    d.getDeadline());
            System.out.println(ConsoleUI.dim(String.format("    |-> Skills: [%s] | Branches: [%s]",
                    String.join(", ", d.getSortedRequiredSkills()),
                    String.join(", ", d.getSortedEligibleBranches()))));
        }
    }

    private String truncate(String text, int max) {
        if (text == null) return "";
        if (text.length() <= max) return text;
        return text.substring(0, max - 3) + "...";
    }
}
