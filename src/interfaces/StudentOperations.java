package interfaces;

import exceptions.AlreadyAppliedException;
import exceptions.DriveNotFoundException;
import exceptions.InvalidInputException;
import models.Application;
import models.PlacementDrive;
import models.Student;

import java.util.List;
import java.util.Map;

/**
 * Interface defining all business operations available to a Student.
 */
public interface StudentOperations {
    Student getProfile(String studentId);
    boolean updateProfile(Student student);
    boolean addSkill(String studentId, String skill);
    boolean removeSkill(String studentId, String skill);
    List<PlacementDrive> getAllActiveDrives();
    List<PlacementDrive> getEligibleDrives(String studentId);
    Application applyForDrive(String studentId, String driveId)
            throws AlreadyAppliedException, DriveNotFoundException, InvalidInputException;
    List<Application> getApplicationsByStudent(String studentId);
    int calculateResumeReadinessScore(Student student);
    Map<String, Object> performSkillGapAnalysis(Student student, PlacementDrive drive);
}
