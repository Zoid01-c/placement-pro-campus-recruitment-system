package models;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Model representing a campus placement recruitment drive.
 */
public class PlacementDrive implements Serializable {
    private static final long serialVersionUID = 1L;

    private String driveId;
    private String companyId;
    private String companyName;
    private String jobRole;
    private double packageLPA;
    private double minCGPA;
    private int maxBacklogs;
    private Set<String> eligibleBranches; // HashSet for fast lookup
    private Set<String> requiredSkills;   // HashSet for fast comparison
    private String location;
    private String deadline;
    private boolean active;

    public PlacementDrive() {
        this.eligibleBranches = new HashSet<>();
        this.requiredSkills = new HashSet<>();
        this.active = true;
    }

    public PlacementDrive(String driveId, String companyId, String companyName,
                          String jobRole, double packageLPA, double minCGPA,
                          int maxBacklogs, String location, String deadline) {
        this.driveId = driveId;
        this.companyId = companyId;
        this.companyName = companyName;
        this.jobRole = jobRole;
        this.packageLPA = packageLPA;
        this.minCGPA = minCGPA;
        this.maxBacklogs = maxBacklogs;
        this.eligibleBranches = new HashSet<>();
        this.requiredSkills = new HashSet<>();
        this.location = location;
        this.deadline = deadline;
        this.active = true;
    }

    public String getDriveId() {
        return driveId;
    }

    public void setDriveId(String driveId) {
        this.driveId = driveId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJobRole() {
        return jobRole;
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }

    public double getPackageLPA() {
        return packageLPA;
    }

    public void setPackageLPA(double packageLPA) {
        this.packageLPA = packageLPA;
    }

    public double getMinCGPA() {
        return minCGPA;
    }

    public void setMinCGPA(double minCGPA) {
        this.minCGPA = minCGPA;
    }

    public int getMaxBacklogs() {
        return maxBacklogs;
    }

    public void setMaxBacklogs(int maxBacklogs) {
        this.maxBacklogs = maxBacklogs;
    }

    public Set<String> getEligibleBranches() {
        return eligibleBranches;
    }

    public void setEligibleBranches(Set<String> eligibleBranches) {
        this.eligibleBranches = eligibleBranches != null ? eligibleBranches : new HashSet<String>();
    }

    public Set<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(Set<String> requiredSkills) {
        this.requiredSkills = requiredSkills != null ? requiredSkills : new HashSet<String>();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void addEligibleBranch(String branch) {
        if (branch != null && !branch.trim().isEmpty()) {
            this.eligibleBranches.add(branch.trim());
        }
    }

    public void addRequiredSkill(String skill) {
        if (skill != null && !skill.trim().isEmpty()) {
            this.requiredSkills.add(skill.trim());
        }
    }

    public Set<String> getSortedRequiredSkills() {
        return new TreeSet<>(this.requiredSkills);
    }

    public Set<String> getSortedEligibleBranches() {
        return new TreeSet<>(this.eligibleBranches);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%.2f LPA) | Min CGPA: %.2f | Max Backlogs: %d | Status: %s",
                driveId, companyName, jobRole, packageLPA, minCGPA, maxBacklogs, active ? "ACTIVE" : "INACTIVE");
    }
}
