package services;

import exceptions.AlreadyAppliedException;
import exceptions.DriveNotFoundException;
import exceptions.InvalidInputException;
import interfaces.Searchable;
import interfaces.StudentOperations;
import models.Application;
import models.PlacementDrive;
import models.Student;
import models.User;

import java.util.*;

/**
 * Implements Student operations, resume scoring, skill-gap analysis, and drive search.
 * Demonstrates Interfaces, Polymorphism, and Collections.
 */
public class StudentService implements StudentOperations, Searchable {

    private final AuthenticationService authService;
    private final PlacementService placementService;
    private final ApplicationService applicationService;
    private final NotificationService notificationService;

    public StudentService(AuthenticationService authService,
                          PlacementService placementService,
                          ApplicationService applicationService,
                          NotificationService notificationService) {
        this.authService = authService;
        this.placementService = placementService;
        this.applicationService = applicationService;
        this.notificationService = notificationService;
    }

    @Override
    public Student getProfile(String studentId) {
        User u = authService.getUserById(studentId);
        return (u instanceof Student) ? (Student) u : null;
    }

    @Override
    public boolean updateProfile(Student student) {
        if (student == null) return false;
        authService.saveAll();
        return true;
    }

    @Override
    public boolean addSkill(String studentId, String skill) {
        Student s = getProfile(studentId);
        if (s != null && skill != null && !skill.trim().isEmpty()) {
            boolean added = s.addSkill(skill.trim());
            if (added) {
                authService.saveAll();
            }
            return added;
        }
        return false;
    }

    @Override
    public boolean removeSkill(String studentId, String skill) {
        Student s = getProfile(studentId);
        if (s != null && skill != null) {
            boolean removed = s.removeSkill(skill.trim());
            if (removed) {
                authService.saveAll();
            }
            return removed;
        }
        return false;
    }

    @Override
    public List<PlacementDrive> getAllActiveDrives() {
        return placementService.getActiveDrives();
    }

    @Override
    public List<PlacementDrive> getEligibleDrives(String studentId) {
        Student s = getProfile(studentId);
        if (s == null) return new ArrayList<>();
        return placementService.getEligibleDrives(s);
    }

    @Override
    public Application applyForDrive(String studentId, String driveId)
            throws AlreadyAppliedException, DriveNotFoundException, InvalidInputException {
        Student s = getProfile(studentId);
        if (s == null) {
            throw new InvalidInputException("Student account not found: " + studentId);
        }

        PlacementDrive drive = placementService.getDriveById(driveId);
        if (drive == null || !drive.isActive()) {
            throw new DriveNotFoundException("Placement drive " + driveId + " was not found or is currently closed.");
        }

        // Verify eligibility before submitting
        if (!placementService.isStudentEligible(s, drive)) {
            throw new InvalidInputException("You do not meet the minimum eligibility requirements for this drive.");
        }

        Application app = applicationService.apply(s, drive);

        // Send confirmation notification
        notificationService.sendNotification(
                s.getId(),
                "Application Submitted Successfully",
                String.format("You have applied for %s at %s. Application ID: %s",
                        drive.getJobRole(), drive.getCompanyName(), app.getApplicationId())
        );

        return app;
    }

    @Override
    public List<Application> getApplicationsByStudent(String studentId) {
        return applicationService.getApplicationsByStudent(studentId);
    }

    /**
     * Calculates transparent resume readiness score (0-100).
     * Formula:
     * - CGPA Score: (CGPA / 10.0) * 50 pts (Max 50)
     * - Skills Score: min(count * 6, 30 pts) (Max 30, achieved with 5+ skills)
     * - Backlogs Score: 0 backlogs -> 20 pts, 1 backlog -> 10 pts, >=2 backlogs -> 0 pts
     */
    @Override
    public int calculateResumeReadinessScore(Student student) {
        if (student == null) return 0;

        double cgpaPart = (Math.min(10.0, Math.max(0.0, student.getCgpa())) / 10.0) * 50.0;
        int skillCount = student.getSkills() != null ? student.getSkills().size() : 0;
        double skillPart = Math.min(30.0, skillCount * 6.0);
        double backlogPart;
        if (student.getBacklogs() == 0) {
            backlogPart = 20.0;
        } else if (student.getBacklogs() == 1) {
            backlogPart = 10.0;
        } else {
            backlogPart = 0.0;
        }

        int total = (int) Math.round(cgpaPart + skillPart + backlogPart);
        return Math.min(100, Math.max(0, total));
    }

    /**
     * Performs skill-gap analysis by comparing candidate skills against drive prerequisites.
     */
    @Override
    public Map<String, Object> performSkillGapAnalysis(Student student, PlacementDrive drive) {
        Map<String, Object> result = new HashMap<>();

        Set<String> studentSkills = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        if (student.getSkills() != null) {
            studentSkills.addAll(student.getSkills());
        }

        Set<String> requiredSkills = drive.getRequiredSkills() != null ? drive.getRequiredSkills() : new HashSet<String>();

        Set<String> matched = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        Set<String> missing = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        for (String req : requiredSkills) {
            if (studentSkills.contains(req)) {
                matched.add(req);
            } else {
                missing.add(req);
            }
        }

        int totalRequired = requiredSkills.size();
        int matchPct = (totalRequired == 0) ? 100 : (int) Math.round(((double) matched.size() / totalRequired) * 100.0);

        List<String> recommendations = new ArrayList<>();
        for (String m : missing) {
            recommendations.add("Acquire fundamental knowledge and projects in: " + m);
        }
        if (student.getBacklogs() > 0) {
            recommendations.add("Clear existing " + student.getBacklogs() + " active backlog(s) to expand drive eligibility.");
        }
        if (student.getCgpa() < 8.0) {
            recommendations.add("Aim to raise overall CGPA above 8.0 for tier-1 product recruiters.");
        }

        result.put("matchedSkills", matched);
        result.put("missingSkills", missing);
        result.put("matchPercentage", matchPct);
        result.put("recommendations", recommendations);

        return result;
    }

    // ==========================================
    // SEARCHABLE INTERFACE IMPLEMENTATION
    // ==========================================
    @Override
    public List<PlacementDrive> searchDrivesByCompany(String keyword) {
        return placementService.searchByCompany(keyword);
    }

    @Override
    public List<PlacementDrive> searchDrivesByRole(String keyword) {
        return placementService.searchByRole(keyword);
    }

    @Override
    public List<PlacementDrive> searchDrivesByLocation(String keyword) {
        return placementService.searchByLocation(keyword);
    }

    @Override
    public List<Student> searchStudentsBySkill(String skill) {
        List<Student> match = new ArrayList<>();
        String sk = skill.trim().toLowerCase();
        for (Student s : authService.getStudents()) {
            for (String sSkill : s.getSkills()) {
                if (sSkill.toLowerCase().contains(sk)) {
                    match.add(s);
                    break;
                }
            }
        }
        return match;
    }

    @Override
    public List<Student> searchStudentsByBranch(String branch) {
        List<Student> match = new ArrayList<>();
        String br = branch.trim().toLowerCase();
        for (Student s : authService.getStudents()) {
            if (s.getBranch().toLowerCase().contains(br)) {
                match.add(s);
            }
        }
        return match;
    }
}
