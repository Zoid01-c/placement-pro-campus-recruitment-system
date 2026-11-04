package interfaces;

import models.PlacementDrive;
import models.Student;

import java.util.List;

/**
 * Interface defining search capabilities across placement drives and candidates.
 */
public interface Searchable {
    List<PlacementDrive> searchDrivesByCompany(String keyword);
    List<PlacementDrive> searchDrivesByRole(String keyword);
    List<PlacementDrive> searchDrivesByLocation(String keyword);
    List<Student> searchStudentsBySkill(String skill);
    List<Student> searchStudentsByBranch(String branch);
}
