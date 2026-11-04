import cli.MainMenu;
import services.*;
import utils.ConsoleUI;
import utils.FileManager;

import java.util.Scanner;

/**
 * Main application bootstrap for PLACEMENT PRO.
 * Initializes storage, services, and CLI interface.
 */
public class Main {

    public static void main(String[] args) {
        // Optional flag to disable ANSI colors if needed: java Main --no-color
        if (args != null && args.length > 0 && "--no-color".equalsIgnoreCase(args[0])) {
            ConsoleUI.setUseColors(false);
        }

        try {
            // 1. Initialize persistent storage and verify/seed initial records
            FileManager.initializeStorage();

            // 2. Instantiate core services
            PlacementService placementService = new PlacementService();
            ApplicationService applicationService = new ApplicationService();
            NotificationService notificationService = new NotificationService();
            AuthenticationService authService = new AuthenticationService();

            StudentService studentService = new StudentService(
                    authService, placementService, applicationService, notificationService
            );

            CompanyService companyService = new CompanyService(
                    authService, placementService, applicationService, notificationService
            );

            AdminService adminService = new AdminService(
                    authService, placementService, applicationService
            );

            ReportService reportService = new ReportService(
                    adminService, applicationService, studentService
            );

            // 3. Launch console user interface
            Scanner scanner = new Scanner(System.in);
            MainMenu mainMenu = new MainMenu(
                    authService, studentService, companyService, adminService,
                    placementService, notificationService, reportService, scanner
            );

            mainMenu.start();

            // 4. Clean up resources
            scanner.close();

        } catch (Exception e) {
            System.err.println("Fatal application error during startup: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
