package models;

import java.io.Serializable;

/**
 * Represents a student's application to a placement drive.
 * Demonstrates enum usage and status lifecycle tracking.
 */
public class Application implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum ApplicationStatus {
        APPLIED,
        SHORTLISTED,
        SELECTED,
        REJECTED
    }

    private String applicationId;
    private String studentId;
    private String studentName;
    private String driveId;
    private String companyName;
    private String jobRole;
    private ApplicationStatus status;
    private String appliedDate;
    private String lastUpdated;
    private String remarks;

    public Application() {
        this.status = ApplicationStatus.APPLIED;
    }

    public Application(String applicationId, String studentId, String studentName,
                       String driveId, String companyName, String jobRole,
                       String appliedDate) {
        this.applicationId = applicationId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.driveId = driveId;
        this.companyName = companyName;
        this.jobRole = jobRole;
        this.status = ApplicationStatus.APPLIED;
        this.appliedDate = appliedDate;
        this.lastUpdated = appliedDate;
        this.remarks = "Application submitted successfully.";
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getDriveId() {
        return driveId;
    }

    public void setDriveId(String driveId) {
        this.driveId = driveId;
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

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public String getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(String appliedDate) {
        this.appliedDate = appliedDate;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return String.format("[%s] Student: %s (%s) | Drive: %s - %s | Status: %s | Updated: %s",
                applicationId, studentName, studentId, companyName, jobRole, status, lastUpdated);
    }
}
