package utils;

import models.*;

import java.io.*;
import java.util.*;

/**
 * Handles all file persistence operations (reading, writing, initializing).
 * Demonstrates Java Core I/O, File, BufferedReader, BufferedWriter, and Collections.
 */
public class FileManager {

    private static final String DATA_DIR = "data";
    private static final String STUDENTS_FILE = DATA_DIR + File.separator + "students.txt";
    private static final String COMPANIES_FILE = DATA_DIR + File.separator + "companies.txt";
    private static final String DRIVES_FILE = DATA_DIR + File.separator + "drives.txt";
    private static final String APPLICATIONS_FILE = DATA_DIR + File.separator + "applications.txt";
    private static final String NOTIFICATIONS_FILE = DATA_DIR + File.separator + "notifications.txt";
    private static final String ADMINS_FILE = DATA_DIR + File.separator + "admins.txt";

    public static void initializeStorage() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Check if primary files exist, if not create and seed
        File studFile = new File(STUDENTS_FILE);
        if (!studFile.exists() || studFile.length() == 0) {
            seedInitialData();
        }
    }

    // ==========================================
    // STUDENTS PERSISTENCE
    // ==========================================
    public static List<Student> loadStudents() {
        List<Student> list = new ArrayList<>();
        File file = new File(STUDENTS_FILE);
        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\|", -1);
                if (parts.length >= 10) {
                    Student s = new Student(
                            parts[0], parts[1], parts[2], parts[3],
                            parts[4], Double.parseDouble(parts[5]),
                            Integer.parseInt(parts[6]), Integer.parseInt(parts[7]),
                            parts[8]
                    );
                    if (!parts[9].trim().isEmpty()) {
                        String[] sks = parts[9].split(",");
                        for (String sk : sks) {
                            s.addSkill(sk.trim());
                        }
                    }
                    list.add(s);
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading students: " + e.getMessage());
        }
        return list;
    }

    public static void saveStudents(List<Student> students) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STUDENTS_FILE))) {
            for (Student s : students) {
                String skillsStr = String.join(",", s.getSkills());
                writer.write(String.format("%s|%s|%s|%s|%s|%.2f|%d|%d|%s|%s%n",
                        s.getId(), s.getName(), s.getEmail(), s.getPassword(),
                        s.getBranch(), s.getCgpa(), s.getBacklogs(), s.getGraduationYear(),
                        s.getContactNumber(), skillsStr));
            }
        } catch (IOException e) {
            System.err.println("Error saving students: " + e.getMessage());
        }
    }

    // ==========================================
    // COMPANIES PERSISTENCE
    // ==========================================
    public static List<Company> loadCompanies() {
        List<Company> list = new ArrayList<>();
        File file = new File(COMPANIES_FILE);
        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\|", -1);
                if (parts.length >= 8) {
                    Company c = new Company(
                            parts[0], parts[1], parts[2], parts[3],
                            parts[4], parts[5], parts[6], parts[7]
                    );
                    list.add(c);
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading companies: " + e.getMessage());
        }
        return list;
    }

    public static void saveCompanies(List<Company> companies) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(COMPANIES_FILE))) {
            for (Company c : companies) {
                writer.write(String.format("%s|%s|%s|%s|%s|%s|%s|%s%n",
                        c.getId(), c.getName(), c.getEmail(), c.getPassword(),
                        c.getIndustry(), c.getLocation(), c.getDescription(), c.getHrContact()));
            }
        } catch (IOException e) {
            System.err.println("Error saving companies: " + e.getMessage());
        }
    }

    // ==========================================
    // PLACEMENT DRIVES PERSISTENCE
    // ==========================================
    public static List<PlacementDrive> loadDrives() {
        List<PlacementDrive> list = new ArrayList<>();
        File file = new File(DRIVES_FILE);
        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\|", -1);
                if (parts.length >= 12) {
                    PlacementDrive d = new PlacementDrive(
                            parts[0], parts[1], parts[2], parts[3],
                            Double.parseDouble(parts[4]), Double.parseDouble(parts[5]),
                            Integer.parseInt(parts[6]), parts[7], parts[8]
                    );
                    d.setActive(Boolean.parseBoolean(parts[9]));
                    if (!parts[10].trim().isEmpty()) {
                        for (String b : parts[10].split(",")) d.addEligibleBranch(b.trim());
                    }
                    if (!parts[11].trim().isEmpty()) {
                        for (String sk : parts[11].split(",")) d.addRequiredSkill(sk.trim());
                    }
                    list.add(d);
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading placement drives: " + e.getMessage());
        }
        return list;
    }

    public static void saveDrives(List<PlacementDrive> drives) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DRIVES_FILE))) {
            for (PlacementDrive d : drives) {
                String branches = String.join(",", d.getEligibleBranches());
                String skills = String.join(",", d.getRequiredSkills());
                writer.write(String.format("%s|%s|%s|%s|%.2f|%.2f|%d|%s|%s|%s|%s|%s%n",
                        d.getDriveId(), d.getCompanyId(), d.getCompanyName(), d.getJobRole(),
                        d.getPackageLPA(), d.getMinCGPA(), d.getMaxBacklogs(), d.getLocation(),
                        d.getDeadline(), d.isActive(), branches, skills));
            }
        } catch (IOException e) {
            System.err.println("Error saving placement drives: " + e.getMessage());
        }
    }

    // ==========================================
    // APPLICATIONS PERSISTENCE
    // ==========================================
    public static List<Application> loadApplications() {
        List<Application> list = new ArrayList<>();
        File file = new File(APPLICATIONS_FILE);
        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\|", -1);
                if (parts.length >= 10) {
                    Application a = new Application(
                            parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[7]
                    );
                    try {
                        a.setStatus(Application.ApplicationStatus.valueOf(parts[6].trim()));
                    } catch (Exception ex) {
                        a.setStatus(Application.ApplicationStatus.APPLIED);
                    }
                    a.setLastUpdated(parts[8]);
                    a.setRemarks(parts[9]);
                    list.add(a);
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading applications: " + e.getMessage());
        }
        return list;
    }

    public static void saveApplications(List<Application> applications) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(APPLICATIONS_FILE))) {
            for (Application a : applications) {
                writer.write(String.format("%s|%s|%s|%s|%s|%s|%s|%s|%s|%s%n",
                        a.getApplicationId(), a.getStudentId(), a.getStudentName(),
                        a.getDriveId(), a.getCompanyName(), a.getJobRole(),
                        a.getStatus().name(), a.getAppliedDate(), a.getLastUpdated(), a.getRemarks()));
            }
        } catch (IOException e) {
            System.err.println("Error saving applications: " + e.getMessage());
        }
    }

    // ==========================================
    // NOTIFICATIONS PERSISTENCE
    // ==========================================
    public static List<Notification> loadNotifications() {
        List<Notification> list = new ArrayList<>();
        File file = new File(NOTIFICATIONS_FILE);
        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\|", -1);
                if (parts.length >= 6) {
                    Notification n = new Notification(
                            parts[0], parts[1], parts[2], parts[3], parts[4]
                    );
                    n.setRead(Boolean.parseBoolean(parts[5]));
                    list.add(n);
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading notifications: " + e.getMessage());
        }
        return list;
    }

    public static void saveNotifications(List<Notification> notifications) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NOTIFICATIONS_FILE))) {
            for (Notification n : notifications) {
                writer.write(String.format("%s|%s|%s|%s|%s|%s%n",
                        n.getNotificationId(), n.getRecipientId(), n.getTitle(),
                        n.getMessage(), n.getTimestamp(), n.isRead()));
            }
        } catch (IOException e) {
            System.err.println("Error saving notifications: " + e.getMessage());
        }
    }

    // ==========================================
    // ADMIN PERSISTENCE
    // ==========================================
    public static List<Admin> loadAdmins() {
        List<Admin> list = new ArrayList<>();
        File file = new File(ADMINS_FILE);
        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\|", -1);
                if (parts.length >= 7) {
                    Admin adm = new Admin(
                            parts[0], parts[1], parts[2], parts[3],
                            parts[4], parts[5], Boolean.parseBoolean(parts[6])
                    );
                    list.add(adm);
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading admins: " + e.getMessage());
        }
        return list;
    }

    public static void saveAdmins(List<Admin> admins) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ADMINS_FILE))) {
            for (Admin a : admins) {
                writer.write(String.format("%s|%s|%s|%s|%s|%s|%s%n",
                        a.getId(), a.getName(), a.getEmail(), a.getPassword(),
                        a.getDepartment(), a.getContactNumber(), a.isDefaultPassword()));
            }
        } catch (IOException e) {
            System.err.println("Error saving admins: " + e.getMessage());
        }
    }

    // ==========================================
    // SEED INITIAL DATA
    // ==========================================
    public static void seedInitialData() {
        // 1. Admin
        List<Admin> admins = new ArrayList<>();
        admins.add(new Admin("ADM0001", "Chief Placement Officer", "admin@placementpro.com",
                "Admin@123", "Training & Placement Cell", "9876543210", true));
        saveAdmins(admins);

        // 2. Students (8 realistic students with diverse branches, skills, CGPA)
        List<Student> students = new ArrayList<>();

        Student s1 = new Student("STU1001", "Rahul Sharma", "rahul.sharma@college.edu", "pass123",
                "Computer Science", 8.85, 0, 2026, "9876500001");
        s1.addSkill("Java"); s1.addSkill("Spring Boot"); s1.addSkill("SQL"); s1.addSkill("Git"); s1.addSkill("Docker");
        students.add(s1);

        Student s2 = new Student("STU1002", "Priya Patel", "priya.patel@college.edu", "pass123",
                "Information Technology", 9.20, 0, 2026, "9876500002");
        s2.addSkill("Python"); s2.addSkill("Machine Learning"); s2.addSkill("SQL"); s2.addSkill("React"); s2.addSkill("AWS");
        students.add(s2);

        Student s3 = new Student("STU1003", "Vikram Aditya", "vikram.aditya@college.edu", "pass123",
                "Computer Science", 7.60, 0, 2026, "9876500003");
        s3.addSkill("Java"); s3.addSkill("HTML/CSS"); s3.addSkill("JavaScript"); s3.addSkill("SQL");
        students.add(s3);

        Student s4 = new Student("STU1004", "Ananya Iyer", "ananya.iyer@college.edu", "pass123",
                "Electronics & Communication", 8.40, 0, 2026, "9876500004");
        s4.addSkill("C++"); s4.addSkill("Embedded Systems"); s4.addSkill("Python"); s4.addSkill("IoT");
        students.add(s4);

        Student s5 = new Student("STU1005", "Rohan Mehta", "rohan.mehta@college.edu", "pass123",
                "Information Technology", 6.80, 1, 2026, "9876500005");
        s5.addSkill("Java"); s5.addSkill("SQL"); s5.addSkill("Web Development");
        students.add(s5);

        Student s6 = new Student("STU1006", "Sneha Kulkarni", "sneha.k@college.edu", "pass123",
                "Computer Science", 9.45, 0, 2026, "9876500006");
        s6.addSkill("Java"); s6.addSkill("Distributed Systems"); s6.addSkill("Kubernetes"); s6.addSkill("AWS"); s6.addSkill("System Design");
        students.add(s6);

        Student s7 = new Student("STU1007", "Amit Kumar", "amit.kumar@college.edu", "pass123",
                "Mechanical Engineering", 7.20, 0, 2026, "9876500007");
        s7.addSkill("AutoCAD"); s7.addSkill("Python"); s7.addSkill("MATLAB"); s7.addSkill("Data Analysis");
        students.add(s7);

        Student s8 = new Student("STU1008", "Tanvi Verma", "tanvi.verma@college.edu", "pass123",
                "Electronics & Communication", 8.10, 0, 2026, "9876500008");
        s8.addSkill("Python"); s8.addSkill("VLSI"); s8.addSkill("C++"); s8.addSkill("Linux");
        students.add(s8);

        saveStudents(students);

        // 3. Companies (4 realistic enterprise recruiters)
        List<Company> companies = new ArrayList<>();
        companies.add(new Company("COM2001", "Google India", "recruit@google.com", "pass123",
                "Software & Cloud", "Bangalore", "Global leader in technology, internet search, and AI innovation.", "HR Campus Lead"));
        companies.add(new Company("COM2002", "Microsoft IDC", "careers@microsoft.com", "pass123",
                "Software & Enterprise", "Hyderabad", "Empowering every person and organization on the planet to achieve more.", "University Relations Manager"));
        companies.add(new Company("COM2003", "TCS Digital", "campus@tcs.com", "pass123",
                "IT Services & Consulting", "Mumbai", "Tata Consultancy Services high-growth digital transformation wing.", "Campus Talent Acquisition"));
        companies.add(new Company("COM2004", "Infosys Technologies", "recruitment@infosys.com", "pass123",
                "Information Technology", "Pune", "Global leader in next-generation digital services and consulting.", "Senior HR Specialist"));
        saveCompanies(companies);

        // 4. Placement Drives (5 realistic drives)
        List<PlacementDrive> drives = new ArrayList<>();

        PlacementDrive d1 = new PlacementDrive("DRV3001", "COM2001", "Google India", "Software Engineer - Campus",
                28.50, 8.50, 0, "Bangalore", "2026-12-15");
        d1.addEligibleBranch("Computer Science");
        d1.addEligibleBranch("Information Technology");
        d1.addRequiredSkill("Java");
        d1.addRequiredSkill("Distributed Systems");
        d1.addRequiredSkill("System Design");
        d1.addRequiredSkill("SQL");
        drives.add(d1);

        PlacementDrive d2 = new PlacementDrive("DRV3002", "COM2002", "Microsoft IDC", "Software Development Engineer (SDE-1)",
                24.00, 8.00, 0, "Hyderabad", "2026-12-20");
        d2.addEligibleBranch("Computer Science");
        d2.addEligibleBranch("Information Technology");
        d2.addEligibleBranch("Electronics & Communication");
        d2.addRequiredSkill("C++");
        d2.addRequiredSkill("Python");
        d2.addRequiredSkill("AWS");
        d2.addRequiredSkill("SQL");
        drives.add(d2);

        PlacementDrive d3 = new PlacementDrive("DRV3003", "COM2003", "TCS Digital", "Digital Systems Engineer",
                7.50, 7.00, 0, "Pan India", "2026-12-10");
        d3.addEligibleBranch("Computer Science");
        d3.addEligibleBranch("Information Technology");
        d3.addEligibleBranch("Electronics & Communication");
        d3.addEligibleBranch("Mechanical Engineering");
        d3.addRequiredSkill("Java");
        d3.addRequiredSkill("Python");
        d3.addRequiredSkill("SQL");
        drives.add(d3);

        PlacementDrive d4 = new PlacementDrive("DRV3004", "COM2004", "Infosys Technologies", "Specialist Programmer",
                9.50, 7.50, 1, "Pune / Bangalore", "2026-12-05");
        d4.addEligibleBranch("Computer Science");
        d4.addEligibleBranch("Information Technology");
        d4.addRequiredSkill("Java");
        d4.addRequiredSkill("React");
        d4.addRequiredSkill("SQL");
        drives.add(d4);

        PlacementDrive d5 = new PlacementDrive("DRV3005", "COM2002", "Microsoft IDC", "Cloud Solution Engineer",
                18.00, 7.80, 0, "Bangalore", "2026-12-25");
        d5.addEligibleBranch("Computer Science");
        d5.addEligibleBranch("Information Technology");
        d5.addRequiredSkill("Python");
        d5.addRequiredSkill("AWS");
        d5.addRequiredSkill("Docker");
        drives.add(d5);

        saveDrives(drives);

        // 5. Applications across statuses
        List<Application> apps = new ArrayList<>();
        Application a1 = new Application("APP4001", "STU1001", "Rahul Sharma", "DRV3001", "Google India", "Software Engineer - Campus", "2026-11-01");
        a1.setStatus(Application.ApplicationStatus.SHORTLISTED);
        a1.setRemarks("Cleared preliminary online coding assessment.");
        apps.add(a1);

        Application a2 = new Application("APP4002", "STU1006", "Sneha Kulkarni", "DRV3001", "Google India", "Software Engineer - Campus", "2026-11-01");
        a2.setStatus(Application.ApplicationStatus.SELECTED);
        a2.setRemarks("Selected following technical interviews.");
        apps.add(a2);

        Application a3 = new Application("APP4003", "STU1002", "Priya Patel", "DRV3002", "Microsoft IDC", "Software Development Engineer (SDE-1)", "2026-11-02");
        a3.setStatus(Application.ApplicationStatus.SHORTLISTED);
        a3.setRemarks("Invited for virtual technical round.");
        apps.add(a3);

        Application a4 = new Application("APP4004", "STU1003", "Vikram Aditya", "DRV3003", "TCS Digital", "Digital Systems Engineer", "2026-11-03");
        a4.setStatus(Application.ApplicationStatus.APPLIED);
        apps.add(a4);

        Application a5 = new Application("APP4005", "STU1005", "Rohan Mehta", "DRV3004", "Infosys Technologies", "Specialist Programmer", "2026-11-03");
        a5.setStatus(Application.ApplicationStatus.REJECTED);
        a5.setRemarks("Did not meet technical interview cutoff.");
        apps.add(a5);

        saveApplications(apps);

        // 6. Notifications
        List<Notification> notifs = new ArrayList<>();
        notifs.add(new Notification("NOT5001", "ALL", "Placement Season 2026 Commenced",
                "Campus placement drives for the 2026 graduating batch are now live. Keep your profile updated.", "2026-11-01 09:00"));
        notifs.add(new Notification("NOT5002", "STU1001", "Application Status Updated",
                "Your application for Google India (Software Engineer - Campus) has been SHORTLISTED!", "2026-11-02 14:30"));
        notifs.add(new Notification("NOT5003", "STU1006", "Offer Letter Issued",
                "Congratulations! You have been SELECTED for Google India. Report to T&P Cell.", "2026-11-03 16:00"));
        notifs.add(new Notification("NOT5004", "STU1002", "Microsoft IDC Interview Scheduled",
                "Your application for Microsoft SDE-1 is SHORTLISTED. Check your email for call link.", "2026-11-03 11:15"));
        saveNotifications(notifs);
    }
}
