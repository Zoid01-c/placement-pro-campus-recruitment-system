package models;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents a student user in the placement system.
 * Inherits from User and manages academic credentials and technical skills.
 */
public class Student extends User {
    private static final long serialVersionUID = 1L;

    private String branch;
    private double cgpa;
    private int backlogs;
    private int graduationYear;
    private String contactNumber;
    private Set<String> skills; // Using HashSet for fast membership and duplicate prevention

    public Student() {
        super();
        this.role = "STUDENT";
        this.skills = new HashSet<>();
    }

    public Student(String id, String name, String email, String password,
                   String branch, double cgpa, int backlogs, int graduationYear,
                   String contactNumber) {
        super(id, name, email, password, "STUDENT");
        this.branch = branch;
        this.cgpa = cgpa;
        this.backlogs = backlogs;
        this.graduationYear = graduationYear;
        this.contactNumber = contactNumber;
        this.skills = new HashSet<>();
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public double getCgpa() {
        return cgpa;
    }

    public void setCgpa(double cgpa) {
        this.cgpa = cgpa;
    }

    public int getBacklogs() {
        return backlogs;
    }

    public void setBacklogs(int backlogs) {
        this.backlogs = backlogs;
    }

    public int getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(int graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public Set<String> getSkills() {
        return skills;
    }

    public void setSkills(Set<String> skills) {
        this.skills = skills != null ? skills : new HashSet<String>();
    }

    public boolean addSkill(String skill) {
        if (skill != null && !skill.trim().isEmpty()) {
            return this.skills.add(skill.trim());
        }
        return false;
    }

    public boolean removeSkill(String skill) {
        if (skill != null) {
            return this.skills.remove(skill.trim());
        }
        return false;
    }

    /**
     * Returns skills sorted alphabetically using TreeSet.
     */
    public Set<String> getSortedSkills() {
        return new TreeSet<>(this.skills);
    }

    @Override
    public String getDisplaySummary() {
        return String.format("Student: %s | ID: %s | Branch: %s | CGPA: %.2f | Backlogs: %d | Skills: %d",
                name, id, branch, cgpa, backlogs, skills.size());
    }
}
