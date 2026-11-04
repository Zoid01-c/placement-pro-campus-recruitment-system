package cli;

import exceptions.InvalidInputException;
import exceptions.InvalidLoginException;
import exceptions.UserAlreadyExistsException;
import models.Admin;
import models.Company;
import models.Student;
import models.User;
import services.*;
import utils.ConsoleUI;
import utils.InputValidator;

import java.util.Scanner;

/**
 * Main application navigation menu and authentication gateway.
 */
public class MainMenu {

    private final AuthenticationService authService;
    private final StudentService studentService;
    private final CompanyService companyService;
    private final AdminService adminService;
    private final PlacementService placementService;
    private final NotificationService notificationService;
    private final ReportService reportService;

    private final StudentMenu studentMenu;
    private final CompanyMenu companyMenu;
    private final AdminMenu adminMenu;

    private final Scanner scanner;

    public MainMenu(AuthenticationService authService,
                    StudentService studentService,
                    CompanyService companyService,
                    AdminService adminService,
                    PlacementService placementService,
                    NotificationService notificationService,
                    ReportService reportService,
                    Scanner scanner) {
        this.authService = authService;
        this.studentService = studentService;
        this.companyService = companyService;
        this.adminService = adminService;
        this.placementService = placementService;
        this.notificationService = notificationService;
        this.reportService = reportService;
        this.scanner = scanner;

        this.studentMenu = new StudentMenu(studentService, notificationService, scanner);
        this.companyMenu = new CompanyMenu(companyService, scanner);
        this.adminMenu = new AdminMenu(adminService, authService, placementService, studentService, reportService, scanner);
    }

    public void start() {
        boolean exit = false;

        while (!exit) {
            ConsoleUI.printHeader("PLACEMENT PRO - RECRUITMENT MANAGEMENT");
            System.out.println("              Welcome to Placement Pro");
            System.out.println("     " + ConsoleUI.dim("Your Career. Your Opportunity. Your Future."));
            ConsoleUI.printDivider();
            System.out.println("  " + ConsoleUI.bold("SELECT PORTAL:"));
            System.out.println("    [1] Student Portal        (Browse drives, apply & gap analysis)");
            System.out.println("    [2] Company Portal        (Post drives, review candidates & shortlist)");
            System.out.println("    [3] Administrator Portal  (System statistics, drives & accounts)");
            System.out.println("    [4] About Project & Training Highlights");
            System.out.println("    [0] Exit Application");
            ConsoleUI.printDivider();

            int choice = InputValidator.readInt(scanner, "  Select an option: ", 0, 4);

            switch (choice) {
                case 1:
                    handleStudentPortal();
                    break;
                case 2:
                    handleCompanyPortal();
                    break;
                case 3:
                    handleAdminPortal();
                    break;
                case 4:
                    showAboutProject();
                    break;
                case 0:
                    exit = true;
                    ConsoleUI.printHeader("THANK YOU FOR USING PLACEMENT PRO");
                    System.out.println("  Session terminated successfully. Best wishes for placements!\n");
                    break;
            }
        }
    }

    private void handleStudentPortal() {
        ConsoleUI.printSubHeader("STUDENT PORTAL ACCESS");
        System.out.println("  [1] Student Login");
        System.out.println("  [2] Register New Student Account");
        System.out.println("  [0] Back to Main Menu");

        int opt = InputValidator.readInt(scanner, "  Choose option: ", 0, 2);
        if (opt == 1) {
            String email = InputValidator.readEmail(scanner, "  Enter student email: ");
            String pass = InputValidator.readNonEmptyString(scanner, "  Enter password: ");

            try {
                User u = authService.login(email, pass);
                if (u instanceof Student) {
                    ConsoleUI.printSuccess("Login successful. Welcome back, " + u.getName() + "!");
                    studentMenu.showDashboard((Student) u);
                } else {
                    ConsoleUI.printError("This account is not registered as a student.");
                }
            } catch (InvalidLoginException e) {
                ConsoleUI.printError(e.getMessage());
                InputValidator.pressEnterToContinue(scanner);
            }
        } else if (opt == 2) {
            registerNewStudent();
        }
    }

    private void registerNewStudent() {
        ConsoleUI.printSubHeader("STUDENT REGISTRATION");
        String name = InputValidator.readNonEmptyString(scanner, "  Full Name: ");
        String email = InputValidator.readEmail(scanner, "  Email Address: ");
        String password = InputValidator.readNonEmptyString(scanner, "  Create Password (min 6 chars): ");
        String branch = InputValidator.readNonEmptyString(scanner, "  Department / Branch (e.g. Computer Science): ");
        double cgpa = InputValidator.readDouble(scanner, "  Cumulative GPA (0.0 to 10.0): ", 0.0, 10.0);
        int backlogs = InputValidator.readInt(scanner, "  Number of Active Backlogs: ", 0, 20);
        int gradYear = InputValidator.readInt(scanner, "  Graduation Year: ", 2024, 2030);
        String phone = InputValidator.readPhone(scanner, "  Contact Phone Number (10 digits): ");

        Student student = new Student("", name, email, password, branch, cgpa, backlogs, gradYear, phone);

        System.out.println("  Enter technical skills (comma-separated, e.g. Java, Python, SQL):");
        String skillsInput = scanner.nextLine().trim();
        if (!skillsInput.isEmpty()) {
            for (String s : skillsInput.split(",")) {
                student.addSkill(s.trim());
            }
        }

        try {
            Student registered = authService.registerStudent(student);
            ConsoleUI.printSuccess("Account created successfully! Your Student ID is: " + registered.getId());
            notificationService.sendNotification(registered.getId(), "Welcome to Placement Pro",
                    "Your student profile has been registered. Explore available drives and start applying!");
            InputValidator.pressEnterToContinue(scanner);
        } catch (UserAlreadyExistsException | InvalidInputException e) {
            ConsoleUI.printError(e.getMessage());
            InputValidator.pressEnterToContinue(scanner);
        }
    }

    private void handleCompanyPortal() {
        ConsoleUI.printSubHeader("COMPANY PORTAL ACCESS");
        System.out.println("  [1] Recruiter Login");
        System.out.println("  [2] Register New Company");
        System.out.println("  [0] Back to Main Menu");

        int opt = InputValidator.readInt(scanner, "  Choose option: ", 0, 2);
        if (opt == 1) {
            String email = InputValidator.readEmail(scanner, "  Enter company email: ");
            String pass = InputValidator.readNonEmptyString(scanner, "  Enter password: ");

            try {
                User u = authService.login(email, pass);
                if (u instanceof Company) {
                    ConsoleUI.printSuccess("Login successful. Welcome, " + u.getName() + "!");
                    companyMenu.showDashboard((Company) u);
                } else {
                    ConsoleUI.printError("This account is not registered as a company recruiter.");
                }
            } catch (InvalidLoginException e) {
                ConsoleUI.printError(e.getMessage());
                InputValidator.pressEnterToContinue(scanner);
            }
        } else if (opt == 2) {
            registerNewCompany();
        }
    }

    private void registerNewCompany() {
        ConsoleUI.printSubHeader("COMPANY REGISTRATION");
        String name = InputValidator.readNonEmptyString(scanner, "  Company Name: ");
        String email = InputValidator.readEmail(scanner, "  Official Email: ");
        String password = InputValidator.readNonEmptyString(scanner, "  Password (min 6 chars): ");
        String industry = InputValidator.readNonEmptyString(scanner, "  Industry Domain (e.g. IT, FinTech, Automobile): ");
        String location = InputValidator.readNonEmptyString(scanner, "  Headquarters / Primary Office: ");
        String hrContact = InputValidator.readNonEmptyString(scanner, "  HR Contact Name & Designation: ");
        String description = InputValidator.readString(scanner, "  Brief Company Overview: ", true);

        Company company = new Company("", name, email, password, industry, location, description, hrContact);

        try {
            Company registered = authService.registerCompany(company);
            ConsoleUI.printSuccess("Company registered successfully! Company ID: " + registered.getId());
            InputValidator.pressEnterToContinue(scanner);
        } catch (UserAlreadyExistsException | InvalidInputException e) {
            ConsoleUI.printError(e.getMessage());
            InputValidator.pressEnterToContinue(scanner);
        }
    }

    private void handleAdminPortal() {
        ConsoleUI.printSubHeader("ADMINISTRATOR SECURE LOGIN");
        String email = InputValidator.readEmail(scanner, "  Enter Admin Email: ");
        String pass = InputValidator.readNonEmptyString(scanner, "  Enter Admin Password: ");

        try {
            User u = authService.login(email, pass);
            if (u instanceof Admin) {
                ConsoleUI.printSuccess("Admin authentication verified.");
                adminMenu.showDashboard((Admin) u);
            } else {
                ConsoleUI.printError("Access denied: Not an administrator account.");
            }
        } catch (InvalidLoginException e) {
            ConsoleUI.printError(e.getMessage());
            InputValidator.pressEnterToContinue(scanner);
        }
    }

    private void showAboutProject() {
        ConsoleUI.printSubHeader("ABOUT PLACEMENT PRO - TECHNICAL ARCHITECTURE");
        System.out.println("  Project Name : PLACEMENT PRO - Smart Campus Recruitment System");
        System.out.println("  Language     : Pure Core Java (JDK 8+ compatible syntax)");
        System.out.println("  Architecture : 3-Tier Console Architecture (CLI - Services - Data)");
        System.out.println("  Persistence  : Text-based Flat-File Engine (Buffered I/O, UTF-8)");
        ConsoleUI.printDivider();
        System.out.println("  " + ConsoleUI.bold("JAVA CONCEPTS DEMONSTRATED:"));
        System.out.println("   1. Java Collections Framework: List, ArrayList, HashMap, HashSet, TreeSet, Comparator");
        System.out.println("   2. Object-Oriented Principles: Inheritance (User->Student/Company/Admin), Polymorphism");
        System.out.println("   3. Interfaces: StudentOperations, CompanyOperations, AdminOperations, Authentication, Searchable, ReportGenerator");
        System.out.println("   4. Custom Exception Handling: InvalidLoginException, UserAlreadyExistsException, etc.");
        System.out.println("   5. File Persistence & Recovery with automated seed data generation");
        System.out.println("   6. Transparent scoring algorithm & Skill-Gap Analysis engine");
        ConsoleUI.printDivider();
        System.out.println("  " + ConsoleUI.bold("DEFAULT DEMO CREDENTIALS:"));
        System.out.println("   * Admin    : admin@placementpro.com / Admin@123");
        System.out.println("   * Student  : rahul.sharma@college.edu / pass123");
        System.out.println("   * Company  : recruit@google.com / pass123");
        InputValidator.pressEnterToContinue(scanner);
    }
}
